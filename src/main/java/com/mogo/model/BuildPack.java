package com.mogo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.nio.file.Paths;

@Data
@AllArgsConstructor
@ToString
public class BuildPack {

    private String basePath;
    private String name;
    private String dockerFilePath;
    private String chartFilePath;

    public static final String DOCKER_FILE = "Dockerfile";
    public static final String CHARTS_DIR = "charts";

    public BuildPack(String basePath, String name) {
        this.name = name;
        this.basePath = basePath;
        this.dockerFilePath = Paths.get(basePath, name, DOCKER_FILE).toFile().getAbsolutePath();
        this.chartFilePath = Paths.get(basePath, name, CHARTS_DIR).toFile().getAbsolutePath();
    }
}
