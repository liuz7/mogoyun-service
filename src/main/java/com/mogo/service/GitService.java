package com.mogo.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

public interface GitService {

    Git cloneProject(String projectName, String repoUrl, String branch) throws GitAPIException;

    void checkoutCommit(Git git, String branch, String commitId) throws GitAPIException;

    Git cloneAndCheckout(String projectName, String repoUrl, String remoteName, String branch, String commitId) throws GitAPIException;
}
