package com.mogo.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "services")
public class ProjectService extends AuditModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String name;
    private String projectName;
    private String repoUrl;
    private String pomFile;
    private String imageRepo;
    private String imageTag;
    private String remoteName;
    private String branch;
    private String commitId;
    private String buildPackName;

    public ProjectService(@NotBlank String name, String projectName, String repoUrl, String pomFile) {
        this.name = name;
        this.projectName = projectName;
        this.repoUrl = repoUrl;
        this.pomFile = pomFile;
    }


    @PrePersist
    public void prePersist() {
        remoteName = "origin";
        branch = "master";
        buildPackName = "gradle";
    }

    /*@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "group_id", nullable = false)
    private ProjectGroup group;*/

}
