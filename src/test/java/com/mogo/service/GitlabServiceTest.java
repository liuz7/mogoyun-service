package com.mogo.service;

import lombok.extern.log4j.Log4j2;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class GitlabServiceTest {

    @Autowired
    private GitlabService gitlabService;

    @Test
    public void testGetCurrentUser() throws Exception {
        assertThat(gitlabService.getCurrentUser().getName()).isEqualToIgnoringCase("liuzhiwen");
    }

    @Test
    public void testGetProjectsByCurrentUser() throws Exception {
        for (Project project : gitlabService.getProjectsByCurrentUser()) {
            log.info("{}:{}", project.getName(), project.getSshUrlToRepo());
        }
        assertThat(gitlabService.getProjectsByCurrentUser().size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testGetProjectsByGroupName() throws Exception {
        List<Project> projects = gitlabService.getProjectsByGroupName("eQ");
        for (Project project : projects) {
            log.info("{}:{}", project.getName(), project.getSshUrlToRepo());
        }
    }

    @Test
    public void testGetBranchesByProject() throws Exception {
        int projectId = gitlabService.getProjectsByCurrentUser().get(0).getId();
        for (Branch branch : gitlabService.getBranchesByProject(projectId)) {
            log.info(branch.getName());
        }
        assertThat(gitlabService.getBranchesByProject(projectId).size()).isGreaterThanOrEqualTo(1);
    }

    @Test
    public void testGetCommits() throws Exception {
        int projectId = gitlabService.getProjectsByCurrentUser().get(0).getId();
        String branchName = gitlabService.getBranchesByProject(projectId).get(0).getName();
        List<Commit> commits = gitlabService.getCommitsByBranch(projectId, branchName, null, null);
        for (Commit commit : commits) {
            log.info("{}:{}", commit.getShortId(), commit.getMessage());
        }
    }

}
