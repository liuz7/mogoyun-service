package com.mogo.controller.rest;

import com.mogo.service.PipelineService;
import io.swagger.annotations.Api;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pipeline")
@Log4j2
@Api(description = "Set of endpoints for clone, build and publish actions in pipeline.")
public class PipelineServiceController {

    @Autowired
    private PipelineService pipelineService;


}
