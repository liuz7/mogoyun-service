package com.mogo.repository;

import com.mogo.model.entity.ProjectService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectServiceRepository extends CrudRepository<ProjectService, Long> {

    ProjectService findByName(String name);
}
