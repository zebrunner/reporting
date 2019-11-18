package com.qaprosoft.zafira.models.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JenkinsJob {

    private String url;
    private String type;
    private String parameters;
}
