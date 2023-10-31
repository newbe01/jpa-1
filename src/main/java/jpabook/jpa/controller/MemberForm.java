package jpabook.jpa.controller;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
public class MemberForm {

    @NotEmpty
    private String name;

    private String city;
    private String street;
    private String zipcode;

}

