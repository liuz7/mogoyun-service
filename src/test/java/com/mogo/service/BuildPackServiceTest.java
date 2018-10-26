package com.mogo.service;

import com.mogo.model.BuildPack;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BuildPackServiceTest {

    @Autowired
    private BuildPackService buildPackService;


    @Test
    public void testListBuildPacks() throws Exception {
        List<BuildPack> buildPacks = buildPackService.listBuildPacks();
        assertThat(buildPacks).extracting("name").contains("gradle");
    }

    @Test
    public void testGetBuildPack() throws Exception {
        BuildPack buildPack = buildPackService.getBuildPack("gradle");
        assertThat(buildPack).isNotNull();
        assertThat(buildPack.getName()).isEqualToIgnoringCase("gradle");
    }
}
