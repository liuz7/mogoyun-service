package com.mogo.service;

import hapi.chart.ChartOuterClass.ChartOrBuilder;
import hapi.release.ReleaseOuterClass.Release;
import hapi.release.StatusOuterClass.Status;
import hapi.services.tiller.Tiller.RollbackReleaseResponse;
import hapi.services.tiller.Tiller.UninstallReleaseResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface HelmService {

    ChartOrBuilder loadChart(String buildPackName) throws IOException;

    Release installRelease(String buildPackName, String namespace, Map<String, Object> values) throws IOException, InterruptedException, ExecutionException;

    List<Release> listReleases(String namespace) throws MalformedURLException;

    UninstallReleaseResponse deleteRelease(String releaseName) throws IOException, InterruptedException, ExecutionException;

    Release getRelease(String releaseName) throws IOException, InterruptedException, ExecutionException;

    Status getReleaseStatus(String releaseName) throws IOException, InterruptedException, ExecutionException;

    List<Release> getReleaseHistory(String releaseName) throws IOException, InterruptedException, ExecutionException;

    Release updateRelease(String buildPackName, String releaseName, Map<String, Object> values) throws IOException, InterruptedException, ExecutionException;

    void deleteAllReleases(String namespace) throws IOException, InterruptedException, ExecutionException;

    RollbackReleaseResponse rollbackRelease(String releaseName, int version) throws IOException, InterruptedException, ExecutionException;
}
