package com.example.sfera_education.payload.res;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResNotification {

    private String title;

    private String content;
}
