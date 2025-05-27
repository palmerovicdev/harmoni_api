package com.palmerodev.harmoni_api.model.entity;

import com.palmerodev.harmoni_api.model.enums.EmotionTrackType;
import com.palmerodev.harmoni_api.model.enums.EmotionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;

@Entity
@Table(name = "emotion_track")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmotionTrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double percentage;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_track_type")
    private EmotionTrackType emotionTrackType;

    @Enumerated(EnumType.STRING)
    @Column(name = "emotion_type")
    private EmotionType emotionType;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = UserInfo.class)
    @JoinColumn(name = "user_id")
    private UserInfo userInfo;

    @ManyToOne(cascade = CascadeType.ALL, targetEntity = ActivityEntity.class)
    @JoinColumn(name = "activity_id")
    private ActivityEntity activity;

    @CreatedDate
    @CreationTimestamp
    private Timestamp createdAt;

    @LastModifiedDate
    @UpdateTimestamp
    private Timestamp updatedAt;
}
