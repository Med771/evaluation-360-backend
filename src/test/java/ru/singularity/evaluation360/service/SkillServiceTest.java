package ru.singularity.evaluation360.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.singularity.evaluation360.dto.test.SkillRequestDTO;
import ru.singularity.evaluation360.entity.SkillEntity;
import ru.singularity.evaluation360.mapper.SkillMapper;
import ru.singularity.evaluation360.repository.SkillRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    private SkillRequestDTO skillRequest;

    private SkillEntity skillEntity;

    private List<SkillEntity> skillEntities;

    @BeforeEach
    void setUp() {
        skillRequest = new SkillRequestDTO("Test Skill");

        skillEntity = new SkillEntity();
        skillEntity.setSkillsText("Test Skill");

        skillEntities = Arrays.asList(skillEntity, skillEntity);

    }

    @Test
    void addSkill_Success() {

        when(skillMapper.toSkillEntity(skillRequest)).thenReturn(skillEntity);
        when(skillRepository.save(skillEntity)).thenReturn(skillEntity);

        SkillEntity result = skillService.addSkill(skillRequest);

        assertEquals(skillEntity, result);
        verify(skillMapper).toSkillEntity(skillRequest);
        verify(skillRepository).save(skillEntity);
    }

    @Test
    void addSkills_Success() {
        List<SkillRequestDTO> skillRequests = Arrays.asList(
            new SkillRequestDTO("Skill 1"),
            new SkillRequestDTO("Skill 2")
        );
        
        when(skillMapper.toSkillsEntity(skillRequests)).thenReturn(skillEntities);
        when(skillRepository.saveAll(skillEntities)).thenReturn(skillEntities);

        List<SkillEntity> result = skillService.addSkills(skillRequests);

        assertEquals(skillEntities, result);
        verify(skillMapper).toSkillsEntity(skillRequests);
        verify(skillRepository).saveAll(skillEntities);
    }

    @Test
    void getSkills_Success() {

        when(skillRepository.findAll()).thenReturn(skillEntities);

        List<SkillEntity> result = skillService.getSkills();

        assertEquals(skillEntities, result);
        verify(skillRepository).findAll();
    }
} 