package com.backend.gapfinder.entities.group;

import com.backend.gapfinder.entities.user.UserBasicDTO;
import com.backend.gapfinder.entities.opentable.OpenTableBasicDTO;
import lombok.Data;

import java.util.List;

@Data
public class GroupCompleteDTO extends GroupBasicDTO {
    private UserBasicDTO creator;
    private List<UserBasicDTO> members;
    private List<OpenTableBasicDTO> openTables;
}
