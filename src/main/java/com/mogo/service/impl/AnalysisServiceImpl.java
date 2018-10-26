package com.mogo.service.impl;

import com.mogo.model.MavenModule;
import com.mogo.model.entity.ProjectGroup;
import com.mogo.model.entity.ProjectService;
import com.mogo.repository.GroupRepository;
import com.mogo.repository.ProjectServiceRepository;
import com.mogo.service.AnalysisService;
import com.mogo.service.GitService;
import com.mogo.service.GitlabService;
import com.mogo.service.MavenService;
import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Log4j2
public class AnalysisServiceImpl implements AnalysisService {

    @Autowired
    private GitService gitService;

    @Autowired
    private GitlabService gitlabService;
    @Autowired
    private MavenService mavenService;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private ProjectServiceRepository projectServiceRepository;


    public void parseServices(String buildpackName) throws GitLabApiException, GitAPIException {
        for (ProjectGroup projectGroup : groupRepository.findAll()) {
            List<Project> projects = gitlabService.getProjectsByGroupName(projectGroup.getName());
            for (Project project : projects) {
                gitService.cloneProject(project.getName(), project.getSshUrlToRepo(), "master");
                TreeNode<MavenModule> moduleTree = mavenService.parseModuleTree(project.getName(),
                        project.getSshUrlToRepo(),
                        buildpackName);
                if (moduleTree != null) {
                    moduleTree.traversePostOrder(parseAction);
                }
            }
        }
    }

    TraversalAction<TreeNode<MavenModule>> parseAction = new TraversalAction<TreeNode<MavenModule>>() {
        @Override
        public void perform(TreeNode<MavenModule> node) {
            if (node.data().isService()) {
                ProjectService projectServiceInDB = projectServiceRepository.findByName(node.data().getServiceName());
                if (projectServiceInDB == null) {
                    ProjectService projectService = new ProjectService(node.data().getServiceName(),
                            node.data().getProjectName(),
                            node.data().getRepoUrl(),
                            node.data().getPomFile());
                    projectServiceRepository.save(projectService);
                } else {
                    projectServiceInDB.setProjectName(node.data().getProjectName());
                    projectServiceInDB.setRepoUrl(node.data().getRepoUrl());
                    projectServiceInDB.setPomFile(node.data().getPomFile());
                    projectServiceRepository.save(projectServiceInDB);
                }
            }
        }

        @Override
        public boolean isCompleted() {
            return false; // return true in order to stop traversing
        }
    };
}
