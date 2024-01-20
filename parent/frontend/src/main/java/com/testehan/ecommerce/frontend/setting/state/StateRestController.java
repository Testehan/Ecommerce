package com.testehan.ecommerce.frontend.setting.state;

import com.testehan.ecommerce.common.dto.StateDTO;
import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class StateRestController {

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/settings/list_states_by_country/{id}")
    public List<StateDTO> listAllStatesByCountry(@PathVariable("id") Integer countryId){
        var countryToSearch = new Country();
        countryToSearch.setId(countryId);
        var states = stateRepository.findByCountryOrderByNameAsc(countryToSearch);

        List<StateDTO> result = new ArrayList<>();
        for (State state : states) {
            result.add(new StateDTO(state.getId(), state.getName()));
        }

        return result;
    }

}
