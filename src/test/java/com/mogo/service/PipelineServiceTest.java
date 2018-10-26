package com.mogo.service;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class PipelineServiceTest {

    @Autowired
    private PipelineService pipelineService;

    @Test
    public void testBuildAndPublishImage() throws Exception {
        pipelineService.buildAndPublishImage("mgzf-search-service",
                "git@git.mogo.com:eq/mgzf-search-service.git",
                "origin",
                "dev",
                "75e970be1584e32ad8b2aa0e49e6d535ade4515c",
                "gradle");
    }

    @Test
    public void testBuildAndPublishServiceTree() throws Exception {
        pipelineService.buildAndPublishServiceTree("room-find-web");
    }
}
