package com.example.sfera_education.payload.top;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TopStudent {

    private Long id;

    private String fullName;

    private Integer groupId;

    private String groupName;

    private Integer scoreMonth;

}
