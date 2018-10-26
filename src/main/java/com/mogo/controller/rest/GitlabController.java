package com.mogo.controller.rest;

import com.mogo.service.GitlabService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/gitlab")
@Log4j2
@Api(description = "Set of endpoints for get project, branches and commits from gitlab.")
public class GitlabController {

    @Autowired
    private GitlabService gitlabService;

    @GetMapping("/currentUser")
    @ApiOperation("Get the current user logged in gitlab")
    public User getCurrentUser() throws GitLabApiException {
        return gitlabService.getCurrentUser();
    }

    @GetMapping("/projects")
    @ApiOperation("Get the projects by current user logged in gitlab")
    public List<Project> getProjectsByUser() throws GitLabApiException {
        return gitlabService.getProjectsByCurrentUser();
    }

    @GetMapping("/projects/{project_id}/branches")
    @ApiOperation("Get the branches by project id")
    public List<Branch> getBranchesByProjectId(@PathVariable("project_id") int projectId) throws GitLabApiException {
        return gitlabService.getBranchesByProject(projectId);
    }

    @GetMapping("/projects/{project_id}/branches/{branchName}/commits")
    @ApiOperation("Get the commits by project and branch")
    public List<Commit> getCommitsByProjectBranch(@PathVariable("project_id") int projectId,
                                                  @PathVariable("branchName") String branchName,
                                                  @RequestParam(value = "from", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fromDate,
                                                  @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date toDate)
            throws GitLabApiException, ParseException {
        return gitlabService.getCommitsByBranch(projectId, branchName, fromDate, toDate);
    }

}
