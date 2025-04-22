package ru.singularity.evaluation360.service;

import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.singularity.evaluation360.repository.ReportRepository;
import ru.singularity.evaluation360.repository.ResultRepository;

@Service
@AllArgsConstructor
public class DaemonService {
    private final ReportRepository reportRepository;
    private final ResultRepository resultRepository;

    @Scheduled(fixedRate = 60000)
    public void createResults() {

    }
}
