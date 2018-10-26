package com.mogo.service.impl;

import com.google.common.collect.Lists;
import com.mogo.model.BuildPack;
import com.mogo.service.BuildPackService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
@Log4j2
public class BuildPackServiceImpl implements BuildPackService {

    @Value("${buildPack.path}")
    private String buildPackPath;

    @Override
    public List<BuildPack> listBuildPacks() {
        List<BuildPack> buildpacks = Lists.newArrayList();
        try {
            Files.walk(Paths.get(buildPackPath), 1)
                    .filter(Files::isDirectory)
                    .filter(e -> !e.getFileName().toString().equalsIgnoreCase(buildPackPath))
                    .forEach(e -> buildpacks.add(new BuildPack(buildPackPath, e.getFileName().toString())));
        } catch (IOException ioe) {
            log.info("Exception:{}", ioe);
        }
        log.info("List BuildPacks: {}", buildpacks);
        return buildpacks;
    }

    @Override
    public BuildPack getBuildPack(String name) throws FileNotFoundException {
        if (!Paths.get(buildPackPath, name).toFile().exists()) {
            throw new FileNotFoundException("Build pack " + name + " is not found");
        }
        return new BuildPack(buildPackPath, name);
    }
}
