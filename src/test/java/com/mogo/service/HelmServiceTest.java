package com.mogo.service;

import hapi.chart.ChartOuterClass.ChartOrBuilder;
import hapi.chart.MetadataOuterClass;
import hapi.release.ReleaseOuterClass.Release;
import hapi.services.tiller.Tiller.RollbackReleaseResponse;
import lombok.extern.log4j.Log4j2;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class HelmServiceTest {

    @Autowired
    private HelmService helmService;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {
        //helmService.deleteAllReleases("");
    }

    @Test
    public void testLoadChart() throws Exception {
        ChartOrBuilder chartOrBuilder = helmService.loadChart("gradle");
        assertThat(chartOrBuilder).isNotNull();
        MetadataOuterClass.MetadataOrBuilder metadata = chartOrBuilder.getMetadataOrBuilder();
        assertThat(metadata).isNotNull();
        log.info("Name: {}", metadata.getName());
        log.info("Version: {}", metadata.getVersion());
    }

    @Test
    public void testInstallRelease() throws Exception {
        Map<String, Object> values = new LinkedHashMap<>();
        Map<String, Object> image = new LinkedHashMap<>();
        image.put("repository", "georgeliu/test");
        image.put("tag", "test");
        values.put("image", image);
        values.put("basedomain", "testdomian");
        Release release = helmService.installRelease("gradle", "", values);
        assertThat(release).isNotNull();
        log.info(release.getName());
    }

    @Test
    public void testListReleases() throws Exception {
        List<Release> releases = helmService.listReleases("");
        for (Release release : releases) {
            log.info("Release:{}", release.getName());
        }
    }

    @Test
    public void testDeleteRelease() throws Exception {
        for (Release release : helmService.listReleases("")) {
            helmService.deleteRelease(release.getName());
            int code = helmService.getReleaseStatus(release.getName()).getCodeValue();
            assertThat(code).isEqualTo(2);
        }
    }

    @Test
    public void testUpdateRelease() throws Exception {
        Map<String, Object> values = new LinkedHashMap<>();
        Map<String, Object> image = new LinkedHashMap<>();
        image.put("repository", "georgeliu/test");
        image.put("tag", "test");
        values.put("image", image);
        Release release = helmService.installRelease("gradle", "", values);
        assertThat(release).isNotNull();
        log.info(release.getName());
        ((Map) values.get("image")).put("tag", "test2");
        release = helmService.updateRelease("gradle", release.getName(), values);
        assertThat(release).isNotNull();
    }

    @Test
    public void testGetReleaseHistory() throws Exception {
        Map<String, Object> values = new LinkedHashMap<>();
        Map<String, Object> image = new LinkedHashMap<>();
        image.put("repository", "georgeliu/test");
        image.put("tag", "test");
        values.put("image", image);
        Release release = helmService.installRelease("gradle", "", values);
        assertThat(release).isNotNull();
        log.info(release.getName());
        ((Map) values.get("image")).put("tag", "test2");
        release = helmService.updateRelease("gradle", release.getName(), values);
        assertThat(release).isNotNull();
        List<Release> releaseHistory = helmService.getReleaseHistory(release.getName());
        assertThat(releaseHistory.size()).isEqualTo(2);
    }

    @Test
    public void testRollbackRelease() throws Exception {
        Map<String, Object> values = new LinkedHashMap<>();
        Map<String, Object> image = new LinkedHashMap<>();
        image.put("repository", "georgeliu/test");
        image.put("tag", "test");
        values.put("image", image);
        Release release = helmService.installRelease("gradle", "", values);
        assertThat(release).isNotNull();
        log.info(release.getName());
        ((Map) values.get("image")).put("tag", "test2");
        release = helmService.updateRelease("gradle", release.getName(), values);
        assertThat(release).isNotNull();
        List<Release> releaseHistory = helmService.getReleaseHistory(release.getName());
        int version = releaseHistory.get(1).getVersion();
        RollbackReleaseResponse rollbackReleaseResponse = helmService.rollbackRelease(release.getName(), version);
        assertThat(rollbackReleaseResponse).isNotNull();
        assertThat(rollbackReleaseResponse.getRelease().getVersion()).isEqualTo(releaseHistory.size() + 1);
    }

}
