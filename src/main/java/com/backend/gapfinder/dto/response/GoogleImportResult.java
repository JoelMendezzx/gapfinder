package com.backend.gapfinder.dto.response;

import java.util.List;

public record GoogleImportResult(List<ClassBlockBasicDTO> created, int skippedDuplicates) {
}
