package com.mogo.repository;

import com.mogo.model.entity.ProjectGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupRepository extends CrudRepository<ProjectGroup, Long> {

    ProjectGroup findByName(String name);
}
