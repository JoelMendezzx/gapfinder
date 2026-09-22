package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class MessageCompleteDTO extends MessageBasicDTO {
    private UserBasicDTO sender;
    private MatchBasicDTO match;
    private OpenTableBasicDTO openTable;
}
