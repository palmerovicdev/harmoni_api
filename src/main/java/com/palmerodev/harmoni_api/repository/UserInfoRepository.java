package com.palmerodev.harmoni_api.repository;

import com.palmerodev.harmoni_api.model.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {

    Optional<UserInfo> findByEmail(String email);

    Optional<UserInfo> findByName(String name);

    boolean existsByEmail(String email);

    boolean existsByName(String name);

}