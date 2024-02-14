package com.testehan.ecommerce.backend.shipping;

import com.testehan.ecommerce.backend.setting.country.CountryRepository;
import com.testehan.ecommerce.backend.util.paging.PagingAndSortingHelper;
import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.ShippingRate;
import com.testehan.ecommerce.common.exception.ShippingRateAlreadyExistsException;
import com.testehan.ecommerce.common.exception.ShippingRateNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ShippingRateService {

    public static final int SHIPPING_RATES_PER_PAGE = 10;

    private ShippingRateRepository shippingRateRepository;
    private CountryRepository countryRepository;

    public ShippingRateService(ShippingRateRepository shippingRateRepository, CountryRepository countryRepository) {
        this.shippingRateRepository = shippingRateRepository;
        this.countryRepository = countryRepository;
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

    public void updateCODSupport(Integer id, boolean codSupported) throws ShippingRateNotFoundException {
        Long count = shippingRateRepository.countById(id);
        if (count == null || count == 0) {
            throw new ShippingRateNotFoundException("Could not find shipping rate with ID " + id);
        }

        shippingRateRepository.updateCashOnDeliveryStatus(id, codSupported);
    }

    public List<Country> listAllCountries() {
        return countryRepository.findAllByOrderByNameAsc();
    }
}
