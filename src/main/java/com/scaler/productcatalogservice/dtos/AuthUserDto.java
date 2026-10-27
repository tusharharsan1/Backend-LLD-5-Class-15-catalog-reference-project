package com.scaler.productcatalogservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthUserDto {
    private String email;
    private String role;
}
