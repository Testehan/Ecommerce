package com.testehan.ecommerce.backend.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryPageInfo {
    private int totalPages;
    private long totalElements;
}
