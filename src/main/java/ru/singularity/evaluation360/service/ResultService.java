package ru.singularity.evaluation360.service;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;

import ru.singularity.evaluation360.dto.result.ResultRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultResponseDTO;

import ru.singularity.evaluation360.entity.ReportEntity;
import ru.singularity.evaluation360.exeptions.DontFoundException;

import ru.singularity.evaluation360.exeptions.RepeatException;
import ru.singularity.evaluation360.log.annotation.LogEntryExit;
import ru.singularity.evaluation360.log.annotation.LogException;
import ru.singularity.evaluation360.mapper.ReportMapper;
import ru.singularity.evaluation360.mapper.ResultMapper;

import ru.singularity.evaluation360.repository.ReportRepository;
import ru.singularity.evaluation360.repository.ResultRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@ToString
public class ResultService {
    private final ResultRepository resultRepository;
    private final ReportRepository reportRepository;

    private final ReportMapper reportMapper;
    private final ResultMapper resultMapper;

    private final String splitter;

    @LogException
    @LogEntryExit
    public ResultResponseDTO getResultByIndex(String id, int userId) {
        return resultMapper.toResultResponseDTO(resultRepository.findByUserTestIndex(id + splitter + userId).
                orElseThrow(() -> new DontFoundException(String.format("Result with id %s not found", id))));
    }

    @LogException
    @LogEntryExit
    public void addResult(String testId, ResultRequestDTO result) {

        Optional<ReportEntity> report =
                reportRepository
                        .findByEvaluatedIdTestIdEvaluatorId(result.evaluatedId() + splitter + testId + splitter + result.evaluatorId());
        if (report.isPresent()) {
            throw new RepeatException("dont Repeat report");
        }
        reportRepository.save(reportMapper.toReportEntity(result, testId, splitter));
    }
}
