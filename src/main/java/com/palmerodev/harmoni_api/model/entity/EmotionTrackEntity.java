package com.palmerodev.harmoni_api.model.entity;

import com.palmerodev.harmoni_api.model.enums.EmotionTrackType;
import com.palmerodev.harmoni_api.model.enums.EmotionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Timestamp;
import java.util.Objects;

@Entity
@Table(name = "emotion_track")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
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

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        EmotionTrackEntity that = (EmotionTrackEntity) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }

}
