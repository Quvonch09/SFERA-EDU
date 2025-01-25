package com.example.sfera_education.payload;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactDTO {

    private Integer id;
    private String street;
    private String districtName;
}
