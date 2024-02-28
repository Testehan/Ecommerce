package com.testehan.ecommerce.backend.shipping;

import com.testehan.ecommerce.backend.product.ProductRepository;
import com.testehan.ecommerce.backend.setting.country.CountryRepository;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.ShippingRate;
import com.testehan.ecommerce.common.exception.ShippingRateAlreadyExistsException;
import com.testehan.ecommerce.common.exception.ShippingRateNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class ShippingRateService {

    public static final int SHIPPING_RATES_PER_PAGE = 10;
    // YOU CAN read more about dimensional weight here : https://www.jaygroup.com/how-to-calculate-dimensional-weight/
    private static final int FED_EX_DIM_FACTOR = 139;

    private ShippingRateRepository shippingRateRepository;
    private CountryRepository countryRepository;
    private ProductRepository productRepository;

    public ShippingRateService(ShippingRateRepository shippingRateRepository, CountryRepository countryRepository,
                               ProductRepository productRepository) {
        this.shippingRateRepository = shippingRateRepository;
        this.countryRepository = countryRepository;
        this.productRepository = productRepository;
    }

    public void save(ShippingRate rateInForm) throws ShippingRateAlreadyExistsException {

        var rateInDB = shippingRateRepository.findByCountryAndState(rateInForm.getCountry().getId(), rateInForm.getState());
        boolean foundExistingRateInNewMode = rateInForm.getId() == null && rateInDB != null;
        boolean foundDifferentExistingRateInEditMode = rateInForm.getId() != null && rateInDB != null && !rateInDB.equals(rateInForm);

        if (foundExistingRateInNewMode || foundDifferentExistingRateInEditMode) {
            throw new ShippingRateAlreadyExistsException("There's already a rate for the destination "
                    + rateInForm.getCountry().getName() + ", " + rateInForm.getState());
        }
        shippingRateRepository.save(rateInForm);
    }

    public void listShippingRatesByPage(int pageNumber, PagingAndSortingHelper pagingAndSortingHelper){
        pagingAndSortingHelper.listEntities(pageNumber,SHIPPING_RATES_PER_PAGE,shippingRateRepository);
    }

    public ShippingRate get(Integer id) throws ShippingRateNotFoundException {
        try {
            return shippingRateRepository.findById(id).get();
        } catch (NoSuchElementException ex) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }
    }

    public void delete(Integer id) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);

        }
        shippingRateRepository.deleteById(id);
    }

    public void updateCashOnDeliveryStatus(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }

        shippingRateRepository.updateCashOnDeliveryStatus(id, codSupported);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public long calculateShippingCost(Integer productId, Integer countryId, String state)
            throws ShippingRateNotFoundException {
        var shippingRate = shippingRateRepository.findByCountryAndState(countryId, state);

        if (shippingRate == null) {
            throw new ShippingRateNotFoundException("No shipping rate found for the given "
                    + "destination. You have to enter shipping cost manually.");
        }

        var product = productRepository.findById(productId).get();

        long dimWeight = (product.getLength() * product.getWidth() * product.getHeight()) / FED_EX_DIM_FACTOR;
        long finalWeight = product.getWeight() > dimWeight ? product.getWeight() : dimWeight;

        return finalWeight * shippingRate.getRate();
    }
}
