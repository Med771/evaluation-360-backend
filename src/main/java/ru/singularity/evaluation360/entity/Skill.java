package ru.singularity.evaluation360.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "skills")
public class Skill  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "skills_text", nullable = false)
    private String skillsText;

    @OneToMany(mappedBy = "skill")
    private List<Question> questions;
}
