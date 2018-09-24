package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qaprosoft.zafira.models.db.Tenancy;
import com.qaprosoft.zafira.models.dto.AbstractType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import java.util.Arrays;

public class TenancyType extends AbstractType {

    private static final long serialVersionUID = 8230787643243488944L;

    @NotEmpty(message = "{error.name.required}")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @AssertTrue(message = "{error.name.invalid}")
    @JsonIgnore
    public boolean isNameConfirmationValid() {
        return ! Arrays.asList(Tenancy.getDefaultNames()).contains(name.toLowerCase());
    }
}
