package com.backend.gapfinder.services.google;

import com.backend.gapfinder.entities.classblock.ClassBlockBasicDTO;

import java.util.List;

public record GoogleImportResult(List<ClassBlockBasicDTO> created, int skippedDuplicates) {
}
