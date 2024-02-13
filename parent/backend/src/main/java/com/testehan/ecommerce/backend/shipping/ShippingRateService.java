package com.testehan.ecommerce.backend.shipping;

import com.testehan.ecommerce.backend.setting.country.CountryRepository;
import com.testehan.ecommerce.common.entity.ShippingRate;
import com.testehan.ecommerce.common.exception.ShippingRateAlreadyExistsException;
import com.testehan.ecommerce.common.exception.ShippingRateNotFoundException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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

    public Page<ShippingRate> listShippingRatesByPage(int pageNumber, String sortField, String sortOrder, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equalsIgnoreCase("ASC") ? sort.ascending() : sort.descending();

        // the first pageNumber displayed in the UI is 1...but the paging starts from 0, hence why we need to substract 1
        Pageable pageable = PageRequest.of(pageNumber -1, SHIPPING_RATES_PER_PAGE, sort);

        if (Strings.isNotBlank(keyword)){
            return shippingRateRepository.findAllByKeyword(keyword,pageable);
        }

        return shippingRateRepository.findAll(pageable);

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

}
