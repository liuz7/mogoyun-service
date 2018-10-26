package com.mogo.service.impl;

import com.mogo.service.GitlabService;
import lombok.extern.log4j.Log4j2;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class GitlabServiceImpl implements GitlabService {

    private GitLabApi gitLabApi;

    public GitlabServiceImpl(@Value("${gitlab.host}") String host, @Value("${gitlab.token}") String token) {
        this.gitLabApi = new GitLabApi(host, token);
        this.gitLabApi.enableRequestResponseLogging();
        log.info("Gitlab client to {} is initialized", host);
    }

    @Override
    public User getCurrentUser() throws GitLabApiException {
        return this.gitLabApi.getUserApi().getCurrentUser();
    }

    @Override
    public List<Project> getProjectsByCurrentUser() throws GitLabApiException {
        return this.gitLabApi.getProjectApi().getMemberProjects();
    }

    @Override
    public List<Project> getProjectsByGroupName(String groupName) throws GitLabApiException {
        return this.gitLabApi.getGroupApi().getGroup(groupName).getProjects();
    }

    @Override
    public List<Branch> getBranchesByProject(int projectId) throws GitLabApiException {
        return this.gitLabApi.getRepositoryApi().getBranches(projectId);
    }

    @Override
    public List<Commit> getCommitsByBranch(int projectId, String branchName, Date since, Date until) throws GitLabApiException, ParseException {
        if (until == null) {
            until = DateTime.now(DateTimeZone.UTC).toDate();
        }

        if (since == null) {
            since = new DateTime(until).minusDays(30).toDate();
        }
        return this.gitLabApi.getCommitsApi().getCommits(projectId, branchName, since, until);
    }
}
