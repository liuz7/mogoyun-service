package com.mogo.model.zipkin;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ZipkinDependency {

    private String parent;
    private String child;
    private int callCount;
}
