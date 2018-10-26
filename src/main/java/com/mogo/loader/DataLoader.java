package com.mogo.loader;

import com.mogo.model.entity.ProjectGroup;
import com.mogo.repository.GroupRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class DataLoader implements CommandLineRunner {

    @Autowired
    private GroupRepository groupRepository;

    @Override
    public void run(String... strings) throws Exception {
        log.info("Loading data...");
        if (groupRepository.findByName("EQ") == null) {
            groupRepository.save(new ProjectGroup("EQ"));
        }

    }
}
