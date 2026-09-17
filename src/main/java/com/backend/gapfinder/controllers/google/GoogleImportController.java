package com.backend.gapfinder.controllers.google;

import com.backend.gapfinder.entities.user.UserEntity;
import com.backend.gapfinder.services.google.GoogleImportResult;
import com.backend.gapfinder.services.google.GoogleScheduleImportService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/google")
public class GoogleImportController {

    private final GoogleScheduleImportService importService;

    public GoogleImportController(GoogleScheduleImportService importService) {
        this.importService = importService;
    }

    @PostMapping("/import-schedule")
    public GoogleImportResult importSchedule(@AuthenticationPrincipal UserEntity user) throws Exception {
        return importService.importSchedule(user.getId());
    }
}
