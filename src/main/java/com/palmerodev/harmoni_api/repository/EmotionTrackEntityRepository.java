package com.palmerodev.harmoni_api.repository;

import com.palmerodev.harmoni_api.model.entity.EmotionTrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionTrackEntityRepository extends JpaRepository<EmotionTrackEntity, Long> {
}