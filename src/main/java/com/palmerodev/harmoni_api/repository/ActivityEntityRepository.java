package com.palmerodev.harmoni_api.repository;

import com.palmerodev.harmoni_api.model.entity.ActivityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityEntityRepository extends JpaRepository<ActivityEntity, Long> {
}