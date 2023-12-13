package com.testehan.ecommerce.common.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Setting {

    @Id
    @Column(length = 256, nullable = false, unique = true)
    private String key;

    @Column(length = 1024, nullable = false, unique = true)
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private SettingCategory category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Setting setting)) return false;
        return Objects.equals(getKey(), setting.getKey()) && Objects.equals(getValue(), setting.getValue()) && getCategory() == setting.getCategory();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue(), getCategory());
    }
}
