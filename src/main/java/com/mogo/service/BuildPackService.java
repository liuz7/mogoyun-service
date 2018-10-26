package com.mogo.service;

import com.mogo.model.BuildPack;

import java.io.FileNotFoundException;
import java.util.List;

public interface BuildPackService {

    List<BuildPack> listBuildPacks();

    BuildPack getBuildPack(String name) throws FileNotFoundException;

}
