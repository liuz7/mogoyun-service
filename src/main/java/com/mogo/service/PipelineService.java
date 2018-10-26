package com.mogo.service;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.eclipse.jgit.api.errors.GitAPIException;

public interface PipelineService {

    void buildAndPublishImage(String projectName,
                              String repoUrl,
                              String remoteName,
                              String branch,
                              String commitId,
                              String buildpackName) throws GitAPIException, MavenInvocationException;

    void buildAndPublishServiceTree(String serviceName);
}
