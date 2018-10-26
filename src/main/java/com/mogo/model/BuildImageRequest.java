package com.mogo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class BuildImageRequest {

    private String buildPack;
    private String repo;
    private String tag;
    private Map<String, String> buildArgs;

}
