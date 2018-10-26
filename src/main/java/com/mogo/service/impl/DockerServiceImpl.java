package com.mogo.service.impl;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.BuildImageCmd;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.AuthConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.command.BuildImageResultCallback;
import com.github.dockerjava.core.command.PullImageResultCallback;
import com.github.dockerjava.core.command.PushImageResultCallback;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import com.mogo.service.BuildPackService;
import com.mogo.service.DockerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Log4j2
public class DockerServiceImpl implements DockerService {

    private DockerClient docker;

    @Value("${docker.auth.username}")
    private String authUsername;
    @Value("${docker.auth.password}")
    private String authPassword;

    @Autowired
    private BuildPackService buildPackService;


    public DockerServiceImpl(@Value("${docker.host}") String dockerHost,
                             @Value("${docker.version}") String dockerVersion,
                             @Value("${docker.registryUrl}") String dockerRegistryUrl,
                             @Value("${docker.registryUsername}") String dockerRegistryUsername,
                             @Value("${docker.registryPassword}") String dockerRegistryPassword) {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(dockerHost)
                .withDockerTlsVerify(false)
                //.withDockerCertPath("/home/user/.docker/certs")
                //.withDockerConfig("/home/user/.docker")
                .withApiVersion(dockerVersion)
                .withRegistryUrl(dockerRegistryUrl)
                .withRegistryUsername(dockerRegistryUsername)
                .withRegistryPassword(dockerRegistryPassword)
                //.withRegistryEmail("dockeruser@github.com")
                .build();
        this.docker = DockerClientBuilder.getInstance(config).build();
    }

    @Override
    public List<Image> listImages() {
        List<Image> images = this.docker.listImagesCmd().exec();
        log.info("Docker Images: {}", images);
        return images;
    }

    @Async("asyncExecutor")
    @Override
    public CompletableFuture<String> buildImage(String buildPackName, String repo, String tag, Map<String, String> buildArgs) throws FileNotFoundException {
        String dockerFilePath = buildPackService.getBuildPack(buildPackName).getDockerFilePath();
        log.info("Starting build docker image from Dockerfile: {} with tag {}", dockerFilePath, tag);
        String imageId;
        String repoTags;
        if (Strings.isNullOrEmpty(tag)) {
            repoTags = repo + ":latest";
        } else {
            repoTags = repo + ":" + tag;
        }
        if (new File(dockerFilePath).exists()) {
            BuildImageCmd buildImageCmd = this.docker.buildImageCmd()
                    .withDockerfile(new File(dockerFilePath))
                    .withPull(true)
                    .withForcerm(true)
                    .withNoCache(true)
                    .withTags(Sets.newHashSet(repoTags));
            for (Map.Entry<String, String> entry : buildArgs.entrySet()) {
                buildImageCmd.withBuildArg(entry.getKey(), entry.getValue());
            }
            imageId = buildImageCmd.exec(new BuildImageResultCallback())
                    .awaitImageId();
            log.info("Image {} is built", imageId);
            return CompletableFuture.completedFuture(imageId);
        } else {
            throw new FileNotFoundException("Dockerfile is not found: " + dockerFilePath);
        }
    }

    //@Async("asyncExecutor")
    @Override
    public void pushImage(String repo, String tag) {
        try {
            this.docker.pushImageCmd(repo)
                    .withTag(tag)
                    .withAuthConfig(new AuthConfig().withUsername(this.authUsername).withPassword(this.authPassword))
                    .exec(new PushImageResultCallback())
                    .awaitCompletion(10, TimeUnit.MINUTES);
            log.info("Image {} is pushed with tag {}", repo, tag);
        } catch (InterruptedException e) {
            log.info("Exception {}", e);
        }
    }

    @Override
    public void removeImage(String imageId, boolean isForced) {
        this.docker.removeImageCmd(imageId).withForce(isForced).exec();
        log.info("Image {} is removed", imageId);
    }

    @Override
    public void removeAllImages(boolean isForced) {
        for (Image image : listImages()) {
            removeImage(image.getId(), true);
        }
    }

    @Override
    public List<SearchItem> searchImages(String searchString) {
        log.info("Search images with {}", searchString);
        return this.docker.searchImagesCmd(searchString).exec();
    }

    //@Async("asyncExecutor")
    @Override
    public void pullImage(String repo, String tag) {
        try {
            this.docker.pullImageCmd(repo)
                    .withTag(tag)
                    .exec(new PullImageResultCallback())
                    .awaitCompletion(10, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.info("Image {} with tag {} is pulled", repo, tag);
            log.info("Exception {}", e);
        }
    }

    @Override
    public InspectImageResponse inspectImage(String imageId) {
        return this.docker.inspectImageCmd(imageId).exec();
    }
}
