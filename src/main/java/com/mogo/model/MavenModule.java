package com.mogo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class MavenModule {

    private String groupId;
    private String artifactId;
    private String version;
    private String packageType;
    private String targetFile;
    private String pomFile;
    private String buildPack;
    private boolean isService;
    private String serviceName;
    private String projectName;
    private String repoUrl;

}
