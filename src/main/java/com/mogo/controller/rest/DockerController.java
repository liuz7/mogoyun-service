package com.mogo.controller.rest;

import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.SearchItem;
import com.mogo.exception.model.BuildPackNotFoundException;
import com.mogo.model.BuildImageRequest;
import com.mogo.model.BuildImageResponse;
import com.mogo.model.PushImageRequest;
import com.mogo.service.DockerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/docker")
@Log4j2
@Api(description = "Set of endpoints for list, search, build and push docker images.")
public class DockerController {

    @Autowired
    private DockerService dockerService;

    @GetMapping("/images")
    @ApiOperation("Returns list of all docker images in the system.")
    public List<Image> listImages() {
        return dockerService.listImages();
    }

    @GetMapping(value = "/image/search", params = "term")
    @ApiOperation("Search docker images from docker registry.")
    public List<SearchItem> searchImages(@RequestParam("term") String term) {
        return dockerService.searchImages(term);
    }

    @PostMapping(value = "image/push")
    @ApiOperation("Push docker image to docker registry.")
    public void pushImage(@RequestBody PushImageRequest pushImageRequest) {
        dockerService.pushImage(pushImageRequest.getName(), pushImageRequest.getTag());
    }

    @DeleteMapping(value = "images/clean")
    @ApiOperation("Delete all docker image locally")
    public void removeAllImages() {
        dockerService.removeAllImages(true);
    }

    @PostMapping(value = "image/build")
    @ApiOperation("Build docker image using build pack and push")
    public BuildImageResponse buildAndPushImage(@RequestBody BuildImageRequest buildImageRequest) throws InterruptedException, ExecutionException {
        try {
            dockerService.buildImage(buildImageRequest.getBuildPack(), buildImageRequest.getRepo(), buildImageRequest.getTag(), buildImageRequest.getBuildArgs()).get();
        } catch (FileNotFoundException e) {
            throw new BuildPackNotFoundException("Build pack is not found");
        } catch (ExecutionException e) {
            if (e.getCause() != null && e.getCause() instanceof FileNotFoundException) {
                throw new BuildPackNotFoundException("Build pack is not found");
            }
        }
        dockerService.pushImage(buildImageRequest.getRepo(), buildImageRequest.getTag());
        return new BuildImageResponse(buildImageRequest.getTag());
    }

}
