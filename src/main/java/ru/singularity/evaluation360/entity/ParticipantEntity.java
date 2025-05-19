package ru.singularity.evaluation360.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "participants")
public class ParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Values for MVP [
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "course", nullable = false)
    private Integer course = -1;
    // Values for MVP ]

    @OneToOne(cascade = CascadeType.ALL, mappedBy="participant")
    private UserEntity user;
}
