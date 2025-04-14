package ru.singularity.evaluation360.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    private String password;

    @OneToOne
    @JoinColumn(name = "participant", nullable = false)
    private ParticipantEntity participant;
}
