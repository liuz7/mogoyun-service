package com.mogo.service.impl;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mogo.service.BuildPackService;
import com.mogo.service.HelmService;
import hapi.chart.ChartOuterClass.Chart.Builder;
import hapi.chart.ChartOuterClass.ChartOrBuilder;
import hapi.release.ReleaseOuterClass.Release;
import hapi.release.StatusOuterClass.Status;
import hapi.services.tiller.Tiller.*;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import lombok.extern.log4j.Log4j2;
import org.microbean.helm.ReleaseManager;
import org.microbean.helm.Tiller;
import org.microbean.helm.chart.DirectoryChartLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
@Log4j2
public class HelmServiceImpl implements HelmService {

    @Autowired
    private BuildPackService buildPackService;
    public static final long TIME_OUT = 500L;
    private ReleaseManager releaseManager;

    public HelmServiceImpl() {
        try {
            DefaultKubernetesClient client = new DefaultKubernetesClient();
            Tiller tiller = new Tiller(client);
            this.releaseManager = new ReleaseManager(tiller);
            log.info("Helm release manager is opened");
        } catch (Exception e) {
            log.info("Helm release manager failed to open");
        }
    }

    @Override
    public ChartOrBuilder loadChart(String buildPackName) throws IOException {
        Path chartFilePath = Paths.get(buildPackService.getBuildPack(buildPackName).getChartFilePath());
        DirectoryChartLoader directoryChartLoader = new DirectoryChartLoader();
        ChartOrBuilder chartOrBuilder = directoryChartLoader.load(chartFilePath);
        return chartOrBuilder;
    }

    private Builder loadChartBuilder(String buildPackName) throws IOException {
        Path chartFilePath = Paths.get(buildPackService.getBuildPack(buildPackName).getChartFilePath());
        DirectoryChartLoader directoryChartLoader = new DirectoryChartLoader();
        Builder chartBuilder = directoryChartLoader.load(chartFilePath);
        return chartBuilder;
    }

    @Override
    public Release installRelease(String buildPackName, String namespace, Map<String, Object> values) throws IOException, InterruptedException, ExecutionException {
        Builder chartBuilder = loadChartBuilder(buildPackName);
        InstallReleaseRequest.Builder requestBuilder = InstallReleaseRequest.newBuilder();
        requestBuilder.setTimeout(TIME_OUT);
        requestBuilder.setWait(true);
        if (!Strings.isNullOrEmpty(namespace)) {
            requestBuilder.setNamespace(namespace);
        }
        if (values != null && !values.isEmpty()) {
            String yamlString = new Yaml().dump(values);
            requestBuilder.getValuesBuilder().setRaw(yamlString);
        }
        Future<InstallReleaseResponse> releaseFuture = releaseManager.install(requestBuilder, chartBuilder);
        Release release = releaseFuture.get().getRelease();
        log.info("Release {} is installed", release.getName());
        return release;
    }

    @Override
    public List<Release> listReleases(String namespace) {
        List<Release> allReleases = Lists.newArrayList();
        ListReleasesRequest.Builder listReleaseBuilder = ListReleasesRequest.newBuilder();
        if (!Strings.isNullOrEmpty(namespace)) {
            listReleaseBuilder.setNamespace(namespace);
        }
        Iterator<ListReleasesResponse> listReleasesResponseIterator = releaseManager.list(listReleaseBuilder.build());
        while (listReleasesResponseIterator.hasNext()) {
            List<Release> releases = listReleasesResponseIterator.next().getReleasesList();
            for (Release release : releases) {
                allReleases.add(release);
            }
        }
        return allReleases;
    }

    @Override
    public UninstallReleaseResponse deleteRelease(String releaseName) throws IOException, InterruptedException, ExecutionException {
        UninstallReleaseRequest.Builder uninstallReleaseBuilder = UninstallReleaseRequest.newBuilder();
        uninstallReleaseBuilder.setTimeout(TIME_OUT);
        uninstallReleaseBuilder.setName(releaseName);
        Future<UninstallReleaseResponse> uninstallReleaseResponseFuture = releaseManager.uninstall(uninstallReleaseBuilder.build());
        UninstallReleaseResponse uninstallReleaseResponse = uninstallReleaseResponseFuture.get();
        log.info("Release {} is deleted", releaseName);
        return uninstallReleaseResponse;
    }

    @Override
    public Release getRelease(String releaseName) throws IOException, InterruptedException, ExecutionException {
        GetReleaseContentRequest.Builder getReleaseBuilder = GetReleaseContentRequest.newBuilder();
        getReleaseBuilder.setName(releaseName);
        Future<GetReleaseContentResponse> getReleaseContentResponseFuture = releaseManager.getContent(getReleaseBuilder.build());
        return getReleaseContentResponseFuture.get().getRelease();
    }

    @Override
    public Status getReleaseStatus(String releaseName) throws IOException, InterruptedException, ExecutionException {
        GetReleaseStatusRequest.Builder getReleaseStatusBuilder = GetReleaseStatusRequest.newBuilder();
        getReleaseStatusBuilder.setName(releaseName);
        Future<GetReleaseStatusResponse> getReleaseStatusResponseFuture = releaseManager.getStatus(getReleaseStatusBuilder.build());
        return getReleaseStatusResponseFuture.get().getInfo().getStatus();
    }

    @Override
    public List<Release> getReleaseHistory(String releaseName) throws IOException, InterruptedException, ExecutionException {
        GetHistoryRequest.Builder getHistoryBuilder = GetHistoryRequest.newBuilder();
        getHistoryBuilder.setName(releaseName);
        getHistoryBuilder.setMax(20);
        Future<GetHistoryResponse> getHistoryResponseFuture = releaseManager.getHistory(getHistoryBuilder.build());
        List<Release> historyReleases = getHistoryResponseFuture.get().getReleasesList();
        return historyReleases;
    }

    @Override
    public Release updateRelease(String buildPackName, String releaseName, Map<String, Object> values) throws IOException, InterruptedException, ExecutionException {
        Builder chartBuilder = loadChartBuilder(buildPackName);
        UpdateReleaseRequest.Builder updateRequestBuilder = UpdateReleaseRequest.newBuilder();
        updateRequestBuilder.setTimeout(TIME_OUT);
        updateRequestBuilder.setWait(true);
        updateRequestBuilder.setName(releaseName);
        if (values != null && !values.isEmpty()) {
            String yamlString = new Yaml().dump(values);
            updateRequestBuilder.getValuesBuilder().setRaw(yamlString);
        }
        Future<UpdateReleaseResponse> releaseFuture = releaseManager.update(updateRequestBuilder, chartBuilder);
        Release release = releaseFuture.get().getRelease();
        log.info("Release {} is update with values {}", releaseName, values);
        return release;
    }

    @Override
    public RollbackReleaseResponse rollbackRelease(String releaseName, int version) throws IOException, InterruptedException, ExecutionException {
        RollbackReleaseRequest.Builder rollbackReleaseRequestBuilder = RollbackReleaseRequest.newBuilder();
        rollbackReleaseRequestBuilder.setName(releaseName);
        rollbackReleaseRequestBuilder.setTimeout(TIME_OUT);
        rollbackReleaseRequestBuilder.setWait(true);
        rollbackReleaseRequestBuilder.setVersion(version);
        Future<RollbackReleaseResponse> rollbackReleaseResponseFuture = releaseManager.rollback(rollbackReleaseRequestBuilder.build());
        log.info("Release {} is rollback to version {}", releaseName, version);
        return rollbackReleaseResponseFuture.get();
    }

    @Override
    public void deleteAllReleases(String namespace) throws IOException, InterruptedException, ExecutionException {
        for (Release release : listReleases(namespace)) {
            deleteRelease(release.getName());
            log.info("Release:{} is deleted", release.getName());
        }
    }

    @PreDestroy
    public void onDestroy() throws Exception {
        releaseManager.close();
        log.info("Release Manager is closed");
    }

}
