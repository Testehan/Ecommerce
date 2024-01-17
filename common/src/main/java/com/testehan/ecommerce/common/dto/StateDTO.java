package com.testehan.ecommerce.common.dto;

// this is the only info that we need for State in the browser...we don't need to know the Country
// this is why the DTO was created
public record StateDTO(Integer id, String name) {
}
