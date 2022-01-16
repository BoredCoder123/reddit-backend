package com.example.redditbackend.response;

import com.example.redditbackend.tempObjects.DashboardItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private Integer userId;
    private String username;
    private List<DashboardItem> posts;
}
