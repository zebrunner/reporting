package com.qaprosoft.zafira.models.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BuildParameterType extends AbstractType
{
    private static final long serialVersionUID = 3647801256222745555L;

    public enum BuildParameterClass {
        STRING, BOOLEAN, HIDDEN
    }

    public BuildParameterType() {
    }

    public BuildParameterType(BuildParameterClass parameterClass, String name, String value) {
        this.parameterClass = parameterClass;
        this.name = name;
        this.value = value;
    }

    @NotNull
    private BuildParameterClass parameterClass;
    @NotNull
    private String name;
    @NotNull
    private String value;

    public BuildParameterClass getParameterClass() {
        return parameterClass;
    }

    public void setParameterClass(BuildParameterClass parameterClass) {
        this.parameterClass = parameterClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
