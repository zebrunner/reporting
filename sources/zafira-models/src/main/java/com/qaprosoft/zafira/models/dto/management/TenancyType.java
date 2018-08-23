package com.qaprosoft.zafira.models.dto.management;

import com.qaprosoft.zafira.models.dto.AbstractType;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import java.util.Arrays;

public class TenancyType extends AbstractType {

    private static final long serialVersionUID = 8230787643243488944L;

    private static final String[] IGNORED_NAMES = {"zafira", "management"};

    @NotEmpty(message = "Name required")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @AssertTrue(message = "Email confirmation not matching")
    public boolean isNameConfirmationValid() {
        return Arrays.asList(IGNORED_NAMES).contains(name.toLowerCase());
    }
}
