package com.mogo.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.mogo.model.MavenModule;
import com.mogo.model.entity.ProjectService;
import com.mogo.repository.ProjectServiceRepository;
import com.mogo.service.*;
import com.mogo.utils.FileUtil;
import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import lombok.extern.log4j.Log4j2;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
@Log4j2
public class PipelineServiceImpl implements PipelineService {

    @Autowired
    private GitService gitService;
    @Autowired
    private MavenService mavenService;
    @Autowired
    private DockerService dockerService;
    @Autowired
    private ProjectServiceRepository projectServiceRepository;
    @Autowired
    private DependencyService dependencyService;
    @Value("${buildPack.path}")
    private String buildPackPath;
    @Value("${docker.auth.username}")
    private String authUsername;
    private static final String TMP_DIR = "tmp";

    @Override
    public void buildAndPublishImage(String projectName,
                                     String repoUrl,
                                     String remoteName,
                                     String branch,
                                     String commitId,
                                     String buildpackName) throws GitAPIException, MavenInvocationException {
        gitService.cloneAndCheckout(projectName, repoUrl, remoteName, branch, commitId);
        mavenService.cleanAndPackage(projectName);
        TreeNode<MavenModule> moduleTree = mavenService.parseModuleTree(projectName, repoUrl, buildpackName);
        moduleTree.traversePostOrder(buildAction);
    }

    TraversalAction<TreeNode<MavenModule>> buildAction = new TraversalAction<TreeNode<MavenModule>>() {
        @Override
        public void perform(TreeNode<MavenModule> node) {
            if (!Strings.isNullOrEmpty(node.data().getTargetFile()) && node.data().isService()) {
                Path targetFile = Paths.get(node.data().getTargetFile());
                Path destTargetFile = Paths.get(buildPackPath, node.data().getBuildPack(), TMP_DIR, targetFile.getFileName().toString());
                FileUtil.copyFile(targetFile, destTargetFile);
                log.info("{}", node.data().getTargetFile());
                Map<String, String> buildArgs = Maps.newHashMap();
                buildArgs.put("target_file", Paths.get(TMP_DIR, targetFile.getFileName().toString()).toString());
                String imageRepo = authUsername + "/" + node.data().getArtifactId();
                String imageTag = node.data().getVersion();
                try {
                    dockerService.buildImage(node.data().getBuildPack(),
                            imageRepo,
                            imageTag,
                            buildArgs).get();
                } catch (FileNotFoundException | InterruptedException | ExecutionException e) {
                    log.info("Exception:{}", e);
                }
                dockerService.pushImage(imageRepo, imageTag);
                ProjectService projectServiceInDB = projectServiceRepository.findByName(node.data().getServiceName());
                if (projectServiceInDB != null) {
                    projectServiceInDB.setImageRepo(imageRepo);
                    projectServiceInDB.setImageTag(imageTag);
                    projectServiceRepository.save(projectServiceInDB);
                }
                FileUtil.deleteFile(destTargetFile);
            } else if (!Strings.isNullOrEmpty(node.data().getTargetFile()) && !node.data().isService()) {
                try {
                    mavenService.cleanAndInstall(node.data().getPomFile());
                } catch (MavenInvocationException e) {
                    log.info("Exception:{}", e);
                }
            }
        }

        @Override
        public boolean isCompleted() {
            return false; // return true in order to stop traversing
        }
    };

    @Override
    public void buildAndPublishServiceTree(String serviceName) {
        TreeNode<String> serviceRoot = dependencyService.getDependencyTree(serviceName);
        serviceRoot.traversePostOrder(buildServiceTreeAction);
    }

    TraversalAction<TreeNode<String>> buildServiceTreeAction = new TraversalAction<TreeNode<String>>() {
        @Override
        public void perform(TreeNode<String> node) {
            ProjectService projectService = projectServiceRepository.findByName(node.data());
            if (projectService != null) {
                try {
                    buildAndPublishImage(projectService.getProjectName(),
                            projectService.getRepoUrl(),
                            projectService.getRemoteName(),
                            projectService.getBranch(),
                            projectService.getCommitId(),
                            projectService.getBuildPackName());
                } catch (GitAPIException | MavenInvocationException e) {
                    log.info("Exception:{}", e);
                }
            }
        }

        @Override
        public boolean isCompleted() {
            return false; // return true in order to stop traversing
        }
    };

}
