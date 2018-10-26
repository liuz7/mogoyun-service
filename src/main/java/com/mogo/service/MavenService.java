package com.mogo.service;

import com.mogo.model.MavenModule;
import com.scalified.tree.TreeNode;
import org.apache.maven.shared.invoker.MavenInvocationException;

public interface MavenService {

    void cleanAndPackage(String projectName) throws MavenInvocationException;

    void cleanAndInstall(String pomFile) throws MavenInvocationException;

    TreeNode<MavenModule> parseModuleTree(String projectName, String repoUrl, String buildPack);
}
