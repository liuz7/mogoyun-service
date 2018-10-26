package com.mogo.controller.rest;

import com.mogo.model.BuildPack;
import com.mogo.service.BuildPackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/buildpack")
@Log4j2
@Api(description = "Set of endpoints for list, get build packs.")
public class BuildPackController {

    @Autowired
    private BuildPackService buildPackService;

    @GetMapping("/list")
    @ApiOperation("Returns list of all build packs.")
    public List<BuildPack> listBuildPacks() {
        return buildPackService.listBuildPacks();
    }
}
