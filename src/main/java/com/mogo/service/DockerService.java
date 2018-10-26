package com.mogo.service;

import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface DockerService {

    List<Image> listImages();

    CompletableFuture<String> buildImage(String buildPackName, String repo, String tag, Map<String, String> buildArgs) throws FileNotFoundException;

    void pushImage(String repo, String tag);

    void removeImage(String imageId, boolean isForced);

    List<SearchItem> searchImages(String searchString);

    void pullImage(String repo, String tag);

    InspectImageResponse inspectImage(String imageId);

    void removeAllImages(boolean isForced);
}
