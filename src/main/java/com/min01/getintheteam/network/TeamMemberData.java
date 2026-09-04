package com.min01.getintheteam.network;

public class TeamMemberData {
    private String name;
    private String uuid;
    private String entityType;

    public TeamMemberData() {
        this.name = "";
        this.uuid = "";
        this.entityType = "";
    }

    public TeamMemberData(String name, String uuid, String entityType) {
        this.name = name;
        this.uuid = uuid;
        this.entityType = entityType;
    }

    public String getName() {
        return name;
    }

    public String getUuid() {
        return uuid;
    }

    public String getEntityType() {
        return entityType;
    }
}