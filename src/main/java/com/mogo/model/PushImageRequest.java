package com.mogo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PushImageRequest {

    private String name;
    private String tag;

}
