package ru.singularity.evaluation360.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.singularity.evaluation360.dto.result.model.SkillsResultModel;
import ru.singularity.evaluation360.dto.result.model.SkillsTestModel;
import ru.singularity.evaluation360.entity.*;
import ru.singularity.evaluation360.entity.model.*;
import ru.singularity.evaluation360.repository.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Testcontainers
public class DaemonServiceMultiDbContainerTest {

    @Container
    static PostgreSQLContainer<?> pg = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:6.0");

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry registry) {
        // PostgreSQL
        registry.add("spring.datasource.url",    pg::getJdbcUrl);
        registry.add("spring.datasource.username", pg::getUsername);
        registry.add("spring.datasource.password", pg::getPassword);
        // MongoDB
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        // Отключаем авто-планировщик
        registry.add("spring.scheduling.enabled", () -> false);
    }

    @Autowired
    private DaemonService daemonService;
    @Autowired
    private TestRepository testRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SkillRepository skillRepository;
    @Autowired
    private ResultRepository resultRepository;
    @Autowired
    private ParticipantRepository participantRepository;

    private final long now = System.currentTimeMillis();

    @BeforeEach
    void setUp() {
        // Очистка баз
        mongoTemplate.getDb().drop();
        userRepository.deleteAll();
        skillRepository.deleteAll();

        // 1) Навыки
        SkillEntity se1 = new SkillEntity(); se1.setSkillsText("Skill A");
        SkillEntity se2 = new SkillEntity(); se2.setSkillsText("Skill B");
        var skillA = skillRepository.save(se1);
        var skillB = skillRepository.save(se2);

        // 2) Пользователь = self
        var p1 = new ParticipantEntity();
        p1.setFullName("Participant A");
        p1.setCourse(1);
        var user = new UserEntity();
        user.setRole(RoleUserEnum.USER);
        user.setEmail("111");
        user.setPassword("111");
        user.setParticipant(p1);
        p1.setUser(user);
        participantRepository.save(p1);
        userRepository.save(user);

        // 3) Эксперт
        var p2 = new ParticipantEntity();
        p2.setFullName("Participant B");
        p2.setCourse(1);
        var expert = new UserEntity();
        expert.setRole(RoleUserEnum.EXPERT);
        expert.setEmail("222");
        expert.setPassword("222");
        expert.setParticipant(p2);
        p2.setUser(expert);
        participantRepository.save(p2);
        userRepository.save(expert);

        // 4) Командный оценщик
        var p3 = new ParticipantEntity();
        p3.setFullName("Participant C (Team)");
        p3.setCourse(1);
        var teammate = new UserEntity();
        teammate.setRole(RoleUserEnum.USER);
        teammate.setEmail("333");
        teammate.setPassword("333");
        teammate.setParticipant(p3);
        p3.setUser(teammate);
        participantRepository.save(p3);
        userRepository.save(teammate);

        // 5) Тесты
        var done = new TestEntity();
        done.setId("t1");
        done.setTitle("Done Test");
        done.setType(TypeTestEnum.FULL);
        done.setStatus(StatusTestEnum.STARTED);
        done.setStartTimeStamp(now - 10_000);
        done.setEndTimeStamp(now - 5_000);
        testRepository.save(done);

        var toStart = new TestEntity();
        toStart.setId("t2");
        toStart.setTitle("Start Test");
        toStart.setType(TypeTestEnum.SELF);
        toStart.setStatus(StatusTestEnum.CREATED);
        toStart.setStartTimeStamp(now - 1_000);
        toStart.setEndTimeStamp(now + 60_000);
        testRepository.save(toStart);

        // 6) Отчёты
        // self
        var r1 = new ReportEntity();
        r1.setTestId("t1");
        r1.setEvaluatorId(user.getId());
        r1.setEvaluatedId(user.getId());
        r1.setSkills(List.of(
                new SkillsTestModel(skillA.getId(), 4.0),
                new SkillsTestModel(skillB.getId(), 2.0)
        ));
        reportRepository.save(r1);

        // эксперт
        var r2 = new ReportEntity();
        r2.setTestId("t1");
        r2.setEvaluatorId(expert.getId());
        r2.setEvaluatedId(user.getId());
        r2.setSkills(List.of(
                new SkillsTestModel(skillA.getId(), 3.0),
                new SkillsTestModel(skillB.getId(), 1.0)
        ));
        reportRepository.save(r2);

        // команда
        var r3 = new ReportEntity();
        r3.setTestId("t1");
        r3.setEvaluatorId(teammate.getId());
        r3.setEvaluatedId(user.getId());
        r3.setSkills(List.of(
                new SkillsTestModel(skillA.getId(), 2.0),
                new SkillsTestModel(skillB.getId(), 4.0)
        ));
        reportRepository.save(r3);
    }

    @Test
    void checkTests_shouldStartAndArchiveAndCompute_withAllThreeRoles() {
        // Запускаем планировщик
        daemonService.checkTests();

        // Проверка статусов
        var ts = testRepository.findById("t2").orElseThrow();
        assertThat(ts.getStatus()).isEqualTo(StatusTestEnum.STARTED);
        var td = testRepository.findById("t1").orElseThrow();
        assertThat(td.getStatus()).isEqualTo(StatusTestEnum.ARCHIVED);

        // Должны сохраниться 3 результата: self, expert, team
        var results = resultRepository.findAll();
        assertThat(results).hasSize(3);

        // IDs по email-юзерам
        Integer userId    = userRepository.findByEmail("111").orElseThrow().getId();
        Integer expertId  = userRepository.findByEmail("222").orElseThrow().getId();
        Integer teamId    = userRepository.findByEmail("333").orElseThrow().getId();
        String sep        = "!_!*!_!";

        // ==== 1) Self-result ====
        ResultEntity selfRes = results.stream()
                .filter(r -> r.getUserTestIndex().equals(userId + sep + "t1"))
                .findFirst().orElseThrow();
        // this = avg(4*2.5,2*2.5) = 7.5; cmd=0; exp=0; avg = avg(2.0,1.0)=1.5
        assertThat(selfRes.getThisResult()).isEqualTo(7.5);
        assertThat(selfRes.getCommandResult()).isEqualTo(0.0);
        assertThat(selfRes.getExpertResult()).isEqualTo(0.0);
        assertThat(selfRes.getAverageResult()).isEqualTo(1.5);
        assertThat(selfRes.getResults())
                .hasSize(2)
                .extracting(
                        SkillsResultModel::skillName,
                        SkillsResultModel::averageEvaluation,
                        SkillsResultModel::thisEvaluation,
                        SkillsResultModel::commandEvaluation,
                        SkillsResultModel::expertEvaluation
                ).containsExactlyInAnyOrder(
                        tuple("Skill A", 2.0, 10.0, 0.0, 0.0),
                        tuple("Skill B", 1.0,  5.0, 0.0, 0.0)
                );

        // ==== 2) Expert-result ====
        ResultEntity expRes = results.stream()
                .filter(r -> r.getUserTestIndex().equals(expertId + sep + "t1"))
                .findFirst().orElseThrow();
        // cmd avg = (3*2.5 +1*2.5)/2 = 5.0; this=0; exp=0; avg = avg(7.5*0.3,2.5*0.3)=1.5
        assertThat(expRes.getThisResult()).isEqualTo(0.0);
        assertThat(expRes.getCommandResult()).isEqualTo(5.0);
        assertThat(expRes.getExpertResult()).isEqualTo(0.0);
        assertThat(expRes.getAverageResult()).isEqualTo(1.5);
        assertThat(expRes.getResults())
                .hasSize(2)
                .extracting(
                        SkillsResultModel::skillName,
                        SkillsResultModel::averageEvaluation,
                        SkillsResultModel::thisEvaluation,
                        SkillsResultModel::commandEvaluation,
                        SkillsResultModel::expertEvaluation
                ).containsExactlyInAnyOrder(
                        tuple("Skill A", 2.25, 0.0, 7.5, 0.0),
                        tuple("Skill B", 0.75, 0.0, 2.5, 0.0)
                );

        // ==== 3) Team-result ====
        ResultEntity teamRes = results.stream()
                .filter(r -> r.getUserTestIndex().equals(teamId + sep + "t1"))
                .findFirst().orElseThrow();
        // cmd avg = (2*2.5 +4*2.5)/2 = 7.5; this=0; exp=0;
        // avg = avg(5.0*0.3,10.0*0.3)=avg(1.5,3.0)=2.25
        assertThat(teamRes.getThisResult()).isEqualTo(0.0);
        assertThat(teamRes.getCommandResult()).isEqualTo(7.5);
        assertThat(teamRes.getExpertResult()).isEqualTo(0.0);
        assertThat(teamRes.getAverageResult()).isEqualTo(2.25);
        assertThat(teamRes.getResults())
                .hasSize(2)
                .extracting(
                        SkillsResultModel::skillName,
                        SkillsResultModel::averageEvaluation,
                        SkillsResultModel::thisEvaluation,
                        SkillsResultModel::commandEvaluation,
                        SkillsResultModel::expertEvaluation
                ).containsExactlyInAnyOrder(
                        tuple("Skill A", 1.5, 0.0, 5.0, 0.0),
                        tuple("Skill B", 3.0, 0.0, 10.0, 0.0)
                );
    }
}
