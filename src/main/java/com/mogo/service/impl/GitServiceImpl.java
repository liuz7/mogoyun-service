package com.mogo.service.impl;

import com.google.common.base.Strings;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.mogo.service.GitService;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.transport.*;
import org.eclipse.jgit.util.FS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

@Service
@Log4j2
public class GitServiceImpl implements GitService {

    @Value("${git.localPath}")
    private String gitLocalPath;
    @Value("${git.keyPath}")
    private String gitKeyPath;

    @Override
    public Git cloneProject(String projectName, String repoUrl, String branch) throws GitAPIException {
        SshSessionFactory sshSessionFactory = new JschConfigSessionFactory() {
            @Override
            protected void configure(OpenSshConfig.Host host, Session session) {
            }

            @Override
            protected JSch createDefaultJSch(FS fs) throws JSchException {
                JSch defaultJSch = super.createDefaultJSch(fs);
                defaultJSch.addIdentity(gitKeyPath);
                return defaultJSch;
            }

        };
        cleanGitProject(gitLocalPath, projectName);
        CloneCommand cloneCommand = Git.cloneRepository();
        cloneCommand.setURI(repoUrl);
        if (Strings.isNullOrEmpty(branch)) {
            branch = "master";
        }
        cloneCommand.setBranch(branch);
        cloneCommand.setDirectory(Paths.get(gitLocalPath, projectName).toFile());
        cloneCommand.setTransportConfigCallback(new TransportConfigCallback() {
            @Override
            public void configure(Transport transport) {
                SshTransport sshTransport = (SshTransport) transport;
                sshTransport.setSshSessionFactory(sshSessionFactory);
            }
        });
        Git git = null;
        try {
            git = cloneCommand.call();
            log.info("The repo {} and branch {} is cloned", projectName, branch);
        } catch (JGitInternalException e) {
            if (e.getMessage().contains("already exists and is not an empty directory")) {
                try {
                    git = Git.open(Paths.get(gitLocalPath, projectName, ".git").toFile());
                } catch (IOException ioe) {
                    log.info("Exception: {}", ioe);
                }
                log.info("Git repo {} and branch {} is found", projectName, branch);
            }
        }
        return git;
    }

    @Override
    public void checkoutCommit(Git git, String branch, String commitId) throws GitAPIException {
        git.checkout().setCreateBranch(false).setName(branch).call();
        if (!Strings.isNullOrEmpty(commitId)) {
            git.checkout().setName(commitId).call();
        }
    }

    @Override
    public Git cloneAndCheckout(String projectName, String repoUrl, String remoteName, String branch, String commitId) throws GitAPIException {
        Git git = cloneProject(projectName, repoUrl, branch);
        if (Strings.isNullOrEmpty(remoteName)) {
            List<RemoteConfig> remoteConfigs = git.remoteList().call();
            remoteName = remoteConfigs.get(0).getName();
        }
        if (Strings.isNullOrEmpty(branch)) {
            branch = "master";
        }
        checkoutCommit(git, remoteName + "/" + branch, commitId);
        return git;
    }

    private void cleanGitProject(String gitLocalPath, String projectName) {
        Path pathToDelete = Paths.get(gitLocalPath, projectName);
        if (pathToDelete.toFile().exists()) {
            try {
                Files.walk(pathToDelete)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException ioe) {
                log.info("Exception:{}", ioe);
            }
        }
    }
}
