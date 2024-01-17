package com.testehan.ecommerce.backend.setting.state;

import com.testehan.ecommerce.common.dto.StateDTO;
import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StateRestController {

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/settings/list_states_by_country/{id}")
    public List<StateDTO> listAllStatesByCountry(@PathVariable("id") Integer countryId){
        var countryToSearch = new Country();
        countryToSearch.setId(countryId);
        List<State> states = stateRepository.findByCountryOrderByNameAsc(countryToSearch);
        return ;
    }
}
