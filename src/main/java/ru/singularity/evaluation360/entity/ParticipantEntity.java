package ru.singularity.evaluation360.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "participants")
public class ParticipantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Values for MVP [
    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "course", nullable = false, columnDefinition = "INTEGER default -10")
    private Integer course;
    // Values for MVP ]

    @OneToOne(cascade = CascadeType.ALL, mappedBy="participant")
    private UserEntity user;
}
