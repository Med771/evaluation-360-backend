package ru.singularity.evaluation360.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.singularity.evaluation360.dto.result.ResultRequestDTO;
import ru.singularity.evaluation360.dto.result.ResultResponseDTO;

import ru.singularity.evaluation360.exeptions.DontFoundException;

import ru.singularity.evaluation360.mapper.ReportMapper;
import ru.singularity.evaluation360.mapper.ResultMapper;

import ru.singularity.evaluation360.repository.ReportRepository;
import ru.singularity.evaluation360.repository.ResultRepository;

@Service
@RequiredArgsConstructor
public class ResultService {
    private final ResultRepository resultRepository;
    private final ReportRepository reportRepository;

    private final ReportMapper reportMapper;
    private final ResultMapper resultMapper;

    public ResultResponseDTO getResultByID(String id) {
        return resultMapper.toResultResponseDTO(resultRepository.findById(id).
                orElseThrow(() -> new DontFoundException(String.format("Result with id %s not found", id))));
    }

    public void addResult(String testId, ResultRequestDTO result) {
        // TODO: update mapper for save
        reportRepository.save(reportMapper.toReportEntity(result));
    }
}
