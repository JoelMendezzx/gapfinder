package com.backend.gapfinder.entities.message;

import com.backend.gapfinder.entities.match.MatchBasicDTO;
import com.backend.gapfinder.entities.opentable.OpenTableBasicDTO;
import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;

@Data
public class MessageCompleteDTO extends MessageBasicDTO {
    private UserBasicDTO sender;
    private MatchBasicDTO match;
    private OpenTableBasicDTO openTable;
}
