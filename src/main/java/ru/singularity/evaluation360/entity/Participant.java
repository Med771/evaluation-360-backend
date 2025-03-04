package ru.singularity.evaluation360.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "participants")
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "avatar", nullable = false, columnDefinition = "varchar(255) default 'url'")
    private String avatar;

    @Column(name = "web_id", nullable = false)
    private String webId;
}
