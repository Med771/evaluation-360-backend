package ru.singularity.evaluation360.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.singularity.evaluation360.entity.TestEntity;
import ru.singularity.evaluation360.entity.model.StatusTestEnum;
import ru.singularity.evaluation360.repository.*;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DaemonServiceTest {

    @Mock
    private ReportRepository reportRepository;
    @Mock
    private ResultRepository resultRepository;
    @Mock
    private TestRepository testRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillRepository skillRepository;

    private DaemonService daemonService;

    @BeforeEach
    void setUp() {
        daemonService = new DaemonService(
                reportRepository,
                resultRepository,
                testRepository,
                userRepository,
                skillRepository,
                "!_!*!_!"
        );
    }

    @Test
    void checkTests_ShouldUpdateStatusAndCalculateResults() {
        long now = System.currentTimeMillis();

        TestEntity test1 = new TestEntity();
        test1.setId("test1");
        test1.setStatus(StatusTestEnum.CREATED);
        test1.setStartTimeStamp(now - 1000);
        test1.setEndTimeStamp(now + 1000);

        TestEntity test2 = new TestEntity();
        test2.setId("test2");
        test2.setStatus(StatusTestEnum.STARTED);
        test2.setEndTimeStamp(now - 1000);
        test2.setStartTimeStamp(now - 2000);

        when(testRepository.findAll()).thenReturn(List.of(test1, test2));
        when(reportRepository.findAllByTestId(anyString())).thenReturn(Collections.emptyList());
        when(skillRepository.findAll()).thenReturn(Collections.emptyList());
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        daemonService.checkTests();

        verify(testRepository).saveAll(anyList());
        verify(resultRepository, atLeastOnce()).saveAll(any());
    }
}
