package com.mogo.service;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface GitlabService {

    User getCurrentUser() throws GitLabApiException;

    List<Project> getProjectsByCurrentUser() throws GitLabApiException;

    List<Branch> getBranchesByProject(int projectId) throws GitLabApiException;

    List<Commit> getCommitsByBranch(int projectId, String branchName, Date since, Date until) throws GitLabApiException, ParseException;

    List<Project> getProjectsByGroupName(String groupName) throws GitLabApiException;

}
