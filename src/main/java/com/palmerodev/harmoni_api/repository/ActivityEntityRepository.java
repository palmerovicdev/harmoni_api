package com.palmerodev.harmoni_api.repository;

import com.palmerodev.harmoni_api.model.entity.ActivityEntity;
import com.palmerodev.harmoni_api.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityEntityRepository extends JpaRepository<ActivityEntity, Long> {

    Object findAllByUserInfo(UserInfo userInfo);

}