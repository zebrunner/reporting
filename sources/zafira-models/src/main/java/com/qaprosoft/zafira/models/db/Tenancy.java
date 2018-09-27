package com.qaprosoft.zafira.models.db;

public class Tenancy extends AbstractEntity {

    private static final long serialVersionUID = -4999394495425059506L;

    private static final String DEFAULT_TENANT = "zafira";
    private static final String MANAGEMENT_SCHEMA = "management";
    private static final String[] DEFAULT_NAMES = {DEFAULT_TENANT, MANAGEMENT_SCHEMA};

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static String getDefaultTenant() {
        return DEFAULT_TENANT;
    }

    public static String getManagementSchema() {
        return MANAGEMENT_SCHEMA;
    }

    public static String[] getDefaultNames() {
        return DEFAULT_NAMES;
    }
}
