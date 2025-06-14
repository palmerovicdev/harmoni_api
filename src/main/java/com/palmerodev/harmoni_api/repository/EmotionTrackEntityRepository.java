package com.palmerodev.harmoni_api.repository;

import com.palmerodev.harmoni_api.model.entity.ActivityEntity;
import com.palmerodev.harmoni_api.model.entity.EmotionTrackEntity;
import com.palmerodev.harmoni_api.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmotionTrackEntityRepository extends JpaRepository<EmotionTrackEntity, Long> {

    List<EmotionTrackEntity> findByUserInfoAndActivity(UserInfo userInfo, ActivityEntity activity);

    List<EmotionTrackEntity> findByUserInfo(UserInfo user);

}