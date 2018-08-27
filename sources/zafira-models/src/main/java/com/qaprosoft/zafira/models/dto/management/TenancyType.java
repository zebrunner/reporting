package com.qaprosoft.zafira.models.dto.management;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qaprosoft.zafira.models.db.management.Tenancy;
import com.qaprosoft.zafira.models.dto.AbstractType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import java.util.Arrays;

public class TenancyType extends AbstractType {

    private static final long serialVersionUID = 8230787643243488944L;

    @NotEmpty(message = "Name required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @AssertTrue(message = "Name confirmation not matching")
    @JsonIgnore
    public boolean isNameConfirmationValid() {
        return ! Arrays.asList(Tenancy.getDefaultNames()).contains(name.toLowerCase());
    }
}
