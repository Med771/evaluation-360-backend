package ru.singularity.evaluation360.filter;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.entity.model.RoleUserEnum;

import ru.singularity.evaluation360.repository.TestRepository;

import java.util.Optional;

@Component("testAuthFilter")
@AllArgsConstructor
public class TestAuthFilter {
    private final TestRepository testRepository;

    public boolean hasAdminAccess(RoleUserEnum role) {
        return role == RoleUserEnum.ADMIN;
    }

    public boolean hasTestAccess(String testId, Integer userId, RoleUserEnum role) {
        Optional<TestEntity> test = testRepository.findById(testId);

        if (hasAdminAccess(role)) {
            return true;
        }

        return test.map(testEntity -> testEntity.getParticipantsIds().contains(userId)).orElse(false);
    }


}
