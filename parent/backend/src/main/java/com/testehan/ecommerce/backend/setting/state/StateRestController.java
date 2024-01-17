package com.testehan.ecommerce.backend.setting.state;

import com.testehan.ecommerce.common.dto.StateDTO;
import com.testehan.ecommerce.common.entity.Country;
import com.testehan.ecommerce.common.entity.State;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
public class StateRestController {

    @Autowired
    private StateRepository stateRepository;

    @GetMapping("/states/list_states_by_country/{id}")
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

    @PostMapping("/states/save")
    public String save(@RequestBody State state){
        var savedState = stateRepository.save(state);
        return String.valueOf(savedState.getId());
    }

    @GetMapping("/states/delete/{id}")
    public void deleteState(@PathVariable("id") Integer stateId){
        stateRepository.deleteById(stateId);
    }

    @PostMapping("/states/check_unique")
    @ResponseBody
    public String checkUnique(@RequestBody Map<String,String> data) {

        String name = data.get("name");

        State state = stateRepository.findByName(name);
        if (Objects.nonNull(state)) {
            boolean isCreatingNew = (state.getId() != null ? true : false);

            if (isCreatingNew) {
                if (state != null) return "Duplicate";
            } else {
                if (state != null && state.getId() != null) {
                    return "Duplicate";
                }
            }
            return "OK";
        } else {
            return "OK";
        }
    }
}
