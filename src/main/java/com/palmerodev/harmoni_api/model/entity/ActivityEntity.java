package com.palmerodev.harmoni_api.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;

@Entity
@Table(name = "activity")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String color;

    @CreatedDate
    @CreationTimestamp
    private Timestamp createdAt;

    @LastModifiedDate
    @UpdateTimestamp
    private Timestamp updatedAt;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = UserInfo.class)
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

}
