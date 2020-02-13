package com.qaprosoft.zafira.web;

import com.qaprosoft.zafira.models.db.UserPreference;
import com.qaprosoft.zafira.models.dto.UserPreferenceDTO;
import com.qaprosoft.zafira.service.UserPreferenceService;
import com.qaprosoft.zafira.web.documented.UserPreferenceDocumentedController;
import org.dozer.Mapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequestMapping(path = "api/user_preferences", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController

public class UserPreferenceController extends AbstractController implements UserPreferenceDocumentedController {

    private final UserPreferenceService userPreferenceService;
    private final Mapper mapper;

    public UserPreferenceController(UserPreferenceService userPreferenceService, Mapper mapper) {
        this.userPreferenceService = userPreferenceService;
        this.mapper = mapper;
    }

    @PutMapping("/{id}")
    @Override
    public UserPreferenceDTO updatePreference(@PathVariable("id") Long id, @RequestBody @Valid UserPreferenceDTO userPreferenceDTO){
        UserPreference userPreference = mapper.map(userPreferenceDTO, UserPreference.class);
        userPreference.setId(id);
        UserPreference updatedUserPreference = userPreferenceService.updateUserPreference(userPreference);
        return mapper.map(updatedUserPreference, UserPreferenceDTO.class);
    }
}
