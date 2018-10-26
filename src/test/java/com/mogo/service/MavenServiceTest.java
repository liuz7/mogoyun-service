package com.mogo.service;

import lombok.extern.log4j.Log4j2;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log4j2
public class MavenServiceTest {

    @Autowired
    private MavenService mavenService;


    @Test
    public void testCleanAndPackage() throws Exception {
        mavenService.cleanAndPackage("mgzf-search-service");
    }

    @Test
    public void testParsePom() throws Exception {
        mavenService.parseModuleTree("mgzf-search-service", "git@git.mogo.com:eq/mgzf-search-service.git", "gradle");
    }
}
