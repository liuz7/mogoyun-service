package com.mogo.service.impl;

import com.mogo.model.zipkin.ZipkinDependency;
import com.mogo.service.DependencyService;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
public class DependencyServiceImpl implements DependencyService {

    @Autowired
    private RestTemplate restTemplate;
    @Value("${zipkin.url}")
    private String zipkinUrl;

    @Override
    public TreeNode<String> getDependencyTree(String serviceName) {
        TreeNode<String> root = new ArrayMultiTreeNode<>(serviceName);
        List<ZipkinDependency> zipkinDependencies = getZipkinDependency();
        for (ZipkinDependency zipkinDependency : zipkinDependencies) {
            if (zipkinDependency.getParent().equalsIgnoreCase(serviceName)) {
                root.add(new ArrayMultiTreeNode<>(zipkinDependency.getChild()));
            }
        }
        log.info(root.toString());
        return root;
    }

    private List<ZipkinDependency> getZipkinDependency() {
        Instant now = Instant.now();
        Instant before = now.minus(Duration.ofDays(1));
        Date dateBefore = Date.from(before);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(zipkinUrl + "/api/v1/dependencies")
                .queryParam("endTs", String.valueOf(Date.from(now).getTime()))
                .queryParam("lookback", String.valueOf(dateBefore.getTime()));
        List<ZipkinDependency> zipkinDependencies = restTemplate.exchange(builder.build().encode().toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<ZipkinDependency>>() {
                }).getBody();
        return zipkinDependencies;
    }
}
