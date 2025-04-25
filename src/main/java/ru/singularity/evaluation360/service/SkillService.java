package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.dto.test.SkillRequestDTO;
import ru.singularity.evaluation360.entity.SkillEntity;
import ru.singularity.evaluation360.mapper.SkillMapper;
import ru.singularity.evaluation360.mapper.TestMapper;
import ru.singularity.evaluation360.repository.QuestionRepository;
import ru.singularity.evaluation360.repository.SkillRepository;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class SkillService {
    private final SkillRepository skillRepository;

    private final SkillMapper skillMapper;

    public SkillEntity addSkill(SkillRequestDTO skillRequestDto){
        return skillRepository.save(skillMapper.toSkillEntity(skillRequestDto));
    }

    public List<SkillEntity> addSkills(List<SkillRequestDTO> skillRequestDTOList){
        return skillRepository.saveAll(skillMapper.toSkillsEntity(skillRequestDTOList));
    }

    public List<SkillEntity> getSkills() {
        return skillRepository.findAll();
    }
}
