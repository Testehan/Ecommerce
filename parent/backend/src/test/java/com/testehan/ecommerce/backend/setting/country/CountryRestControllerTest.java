package com.testehan.ecommerce.backend.setting.country;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.testehan.ecommerce.common.entity.Country;
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
public class CountryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CountryRepository countryRepository;

    @Test
    @WithMockUser(username = "dan@yahoo.com", password = "123456abc", roles="ADMIN")
    public void listCountries() throws Exception {
        var url = "/countries/list";
        MvcResult mvcResult = mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        System.out.println(jsonResponse);

        Country[] countries = objectMapper.readValue(jsonResponse, Country[].class);
        for (Country country : countries){
            System.out.println(country);
        }

        assertThat(countries).hasSizeGreaterThan(0);
    }

    @Test
    @WithMockUser(username = "dan@yahoo.com", password = "123456abc", roles="ADMIN")
    public void createCountry() throws Exception {
        var url = "/countries/save";
        var countryName = "Canada";
        var countryCode = "Ca";
        var country = new Country(countryName,countryCode);

        MvcResult mvcResult = mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf())
                    )
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();
        Integer countryId = Integer.parseInt(responseBody);
        var persistedCountry = countryRepository.findById(countryId).get();

        assertThat(persistedCountry.getName()).isEqualTo(countryName);
        assertThat(persistedCountry.getCode()).isEqualTo(countryCode);
    }

    @Test
    @WithMockUser(username = "dan@yahoo.com", password = "123456abc", roles="ADMIN")
    public void updateCountry() throws Exception {
        var url = "/countries/save";
        var countryName = "United states of America";
        var countryCode = "Us";
        var countryId = 4;
        var country = new Country(countryId,countryName,countryCode);

        mockMvc.perform(post(url)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(country))
                        .with(csrf())
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(String.valueOf(countryId)));
    }

    @Test
    @WithMockUser(username = "dan@yahoo.com", password = "123456abc", roles="ADMIN")
    public void deleteCountry() throws Exception {
        var countryId = 4;
        var url = "/countries/delete/" + countryId;

        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andDo(print());


        var persistedCountry = countryRepository.findById(countryId);
        assertThat(persistedCountry.isEmpty()).isEqualTo(true);
    }
}
