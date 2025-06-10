package com.palmerodev.harmoni_api.repository;

import com.palmerodev.harmoni_api.model.entity.SettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingsEntityRepository extends JpaRepository<SettingsEntity, Long> {

    Optional<SettingsEntity> findByUserInfoIdId(Long userId);

    @Modifying
    @Query(value = "INSERT INTO settings_entity (settingsjson, user_id) VALUES (?1::json, ?2)", nativeQuery = true)
    void saveSettings(String json, Long userId);

}