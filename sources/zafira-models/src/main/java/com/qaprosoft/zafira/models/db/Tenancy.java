package com.qaprosoft.zafira.models.db;

public class Tenancy extends AbstractEntity {

    private static final long serialVersionUID = -4999394495425059506L;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
