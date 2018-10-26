package com.mogo.service;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.gitlab4j.api.GitLabApiException;

public interface AnalysisService {

    void parseServices(String buildpackName) throws GitLabApiException, GitAPIException;
}
