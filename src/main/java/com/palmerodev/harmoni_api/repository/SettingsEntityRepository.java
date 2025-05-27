package com.palmerodev.harmoni_api.repository;

import com.palmerodev.harmoni_api.model.entity.SettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsEntityRepository extends JpaRepository<SettingsEntity, Long> {

    Optional<SettingsEntity> findByUserInfoIdId(Long userId);

}