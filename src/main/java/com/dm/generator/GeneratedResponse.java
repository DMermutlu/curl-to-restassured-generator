package com.dm.generator;

public class GeneratedResponse {

    private String service;
    private String dto;
    private String pom;
    private String readme;

    public GeneratedResponse(String service, String dto, String pom, String readme) {
        this.service = service;
        this.dto = dto;
        this.pom = pom;
        this.readme = readme;
    }

    public String getService() {
        return service;
    }

    public String getDto() {
        return dto;
    }

    public String getPom() {
        return pom;
    }

    public String getReadme() {
        return readme;
    }
}