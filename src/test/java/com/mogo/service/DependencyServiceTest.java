package com.mogo.service;

import com.scalified.tree.TreeNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DependencyServiceTest {

    @Autowired
    private DependencyService dependencyService;

    @Test
    public void testGetDependencyTree() throws Exception {
        TreeNode<String> dependencyTree = dependencyService.getDependencyTree("room-find-web");
        assertThat(dependencyTree.find("room-detail-provider")).isNotNull();
    }
}
