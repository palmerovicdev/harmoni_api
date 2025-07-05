package com.palmerodev.harmoni_api.repository;

import com.palmerodev.harmoni_api.model.entity.ActivityEntity;
import com.palmerodev.harmoni_api.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface ActivityEntityRepository extends JpaRepository<ActivityEntity, Long> {

    List<ActivityEntity> findAllByUserInfo(UserInfo userInfo);

    List<ActivityEntity> findByNameInAndUserInfo_Id(Collection<String> names, Long userInfoId);

}