package com.mogo.service;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;
import com.google.common.collect.Maps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DockerServiceTest {

    @Autowired
    private DockerService dockerService;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        dockerService.removeAllImages(true);
    }

    @Test
    public void testListImages() {
        dockerService.pullImage("nginx", "latest");
        List<Image> imageList = dockerService.listImages();
        assertThat(imageList.size()).isEqualTo(1);
    }

    @Test
    public void testBuildImage() {
        Map<String, String> buildArgs = Maps.newHashMap();
        buildArgs.put("git_url", "git@git.mogo.com:liuzhiwen/mogoyun-service.git");
        buildArgs.put("project", "mogoyun-service");
        String imageId = null;
        try {
            imageId = dockerService.buildImage("gradle", "georgeliu/test", "test", buildArgs).get();
        } catch (InterruptedException | ExecutionException | FileNotFoundException e) {
            e.printStackTrace();
        }
        InspectImageResponse imageResponse = dockerService.inspectImage(imageId);
        assertThat(imageResponse).isNotNull();
    }

    @Test
    public void testBuildAndPushImage() throws FileNotFoundException, InterruptedException, ExecutionException {
        Map<String, String> buildArgs = Maps.newHashMap();
        buildArgs.put("git_url", "git@git.mogo.com:liuzhiwen/mogoyun-service.git");
        buildArgs.put("project", "mogoyun-service");
        String imageId = dockerService.buildImage("gradle", "georgeliu/test", "test", buildArgs).get();
        String imageName = dockerService.inspectImage(imageId).getRepoTags().get(0);
        String repo = imageName.split(":")[0];
        String tag = imageName.split(":")[1];
        dockerService.pushImage(repo, tag);
        dockerService.removeAllImages(true);
        dockerService.pullImage(repo, tag);
    }

    @Test
    public void testRemoveImage() {
        dockerService.pullImage("nginx", "latest");
        for (Image image : dockerService.listImages()) {
            dockerService.removeImage(image.getId(), true);
        }
        assertThat(dockerService.listImages().size()).isEqualTo(0);
    }

    @Test
    public void testSearchImages() {
        List<SearchItem> searchItems = dockerService.searchImages("Java");
        assertThat(searchItems.size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testPullImage() {
        dockerService.pullImage("nginx", "latest");
        assertThat(dockerService.listImages().size()).isEqualTo(1);
    }

}
