package com.qaprosoft.zafira.models.dto;

import com.qaprosoft.zafira.models.db.UserPreference;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserPreferenceDTO {

    @NotNull
    private UserPreference.Name name;

    @NotNull
    private String value;

    @NotNull
    private Long userId;
}

