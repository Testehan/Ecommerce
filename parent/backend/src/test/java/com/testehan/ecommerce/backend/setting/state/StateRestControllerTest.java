package com.testehan.ecommerce.backend.setting.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testehan.ecommerce.backend.setting.country.CountryRepository;
import com.testehan.ecommerce.common.dto.StateDTO;
import com.testehan.ecommerce.common.entity.State;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StateRestControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CountryRepository countryRepository;


    @Test
    @WithMockUser(username = "dan@yahoo.com", password = "123456abc", roles="ADMIN")
    public void listStatesOfCountry() throws Exception {
        var indiaCountryId = 2; // current id in the DB of india
        var url = "/states/list_states_by_country/" + indiaCountryId;
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);

        StateDTO[] states = objectMapper.readValue(jsonResponse, StateDTO[].class);
        for (StateDTO state : states){
            System.out.println(state);
        }

        assertThat(states).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "dan@yahoo.com", password = "123456abc", roles="ADMIN")
    public void createState() throws Exception {
        var url = "/states/save";
        var stateName = "Indian Alabama";
        var country = countryRepository.findById(2).get();
        var state = new State(stateName,country);

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Integer stateId = Integer.parseInt(responseBody);
        var persistedState = stateRepository.findById(stateId).get();

        assertThat(persistedState.getName()).isEqualTo(stateName);
        assertThat(persistedState.getCountry()).isEqualTo(country);
    }

    @Test
    @WithMockUser(username = "dan@yahoo.com", password = "123456abc", roles="ADMIN")
    public void updateState() throws Exception {
        var url = "/states/save";
        var stateId = 10; // id of a State from DB
        var state = stateRepository.findById(stateId).get();
        state.setName("Mumbaiii");

        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(state))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(String.valueOf(stateId)));
    }

    @Test
    @WithMockUser(username = "dan@yahoo.com", password = "123456abc", roles="ADMIN")
    public void deleteState() throws Exception {
        var stateId = 10; // id of a State from DB
        var url = "/states/delete/" + stateId;

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print());


        var persistedState = stateRepository.findById(stateId);
        assertThat(persistedState.isEmpty()).isEqualTo(true);
    }
}
