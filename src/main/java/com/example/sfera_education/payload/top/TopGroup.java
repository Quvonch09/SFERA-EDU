package com.example.sfera_education.payload.top;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopGroup {

    private Integer id;

    private String groupName;

    private Integer studentCount;

    private Integer scoreMonth;
}
