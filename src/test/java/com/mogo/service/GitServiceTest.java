package com.mogo.service;

import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class GitServiceTest {

    @Autowired
    private GitService gitService;

    @Test
    public void testCloneProject() throws Exception {
        Git git = gitService.cloneProject("mgzf-search-service", "git@git.mogo.com:eq/mgzf-search-service.git", "dev");
        assertThat(git).isNotNull();
    }

    @Test
    public void testCheckoutCommit() throws Exception {
        Git git = gitService.cloneAndCheckout("mgzf-search-service", "git@git.mogo.com:eq/mgzf-search-service.git", "origin", "dev", "75e970be1584e32ad8b2aa0e49e6d535ade4515c");
        ObjectId head = git.getRepository().resolve(Constants.HEAD);
        assertThat(head.toObjectId().getName()).isEqualToIgnoringCase("75e970be1584e32ad8b2aa0e49e6d535ade4515c");
    }

}
