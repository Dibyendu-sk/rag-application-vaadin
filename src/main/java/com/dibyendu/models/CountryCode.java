package com.dibyendu.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryCode {
    private String code;
    private String label;

    @Override
    public String toString() {
        return code + "("+label+")";
    }
}
