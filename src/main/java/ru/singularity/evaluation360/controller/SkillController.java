package ru.singularity.evaluation360.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.singularity.evaluation360.dto.test.SkillRequestDTO;
import ru.singularity.evaluation360.entity.SkillEntity;
import ru.singularity.evaluation360.service.SkillService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("skill")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    /**
     * Добавление навыка
     */
    @PostMapping("skill")
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<SkillEntity> addSkill(@RequestBody SkillRequestDTO skillRequestDTO){
        return ResponseEntity.ok(skillService.addSkill(skillRequestDTO));
    }

    /**
     * Добавление навыков
     */
    @PostMapping("skills")
    public ResponseEntity<List<SkillEntity>> addSkills(@RequestBody List<SkillRequestDTO> skillRequestDTOS){
        return ResponseEntity.ok(skillService.addSkills(skillRequestDTOS));
    }
    /**
     * Получить навыки
     */
    @GetMapping("skills")
    @PreAuthorize("@testAuthFilter.hasAdminAccess(authentication.principal.role)")
    public ResponseEntity<List<SkillEntity>> getSkills() {
        return ResponseEntity.ok(skillService.getSkills());
    }
}
