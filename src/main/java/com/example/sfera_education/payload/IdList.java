package com.example.sfera_education.payload;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdList {
    private List<Long> ids;
}
