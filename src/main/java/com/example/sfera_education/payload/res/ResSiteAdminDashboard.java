package com.example.sfera_education.payload.res;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResSiteAdminDashboard {

    private int countUser;

    private int countTeacher;

    private int countStudent;

    private int countAll;

    private List<ResDashboardUserP> rolePiece;

}
