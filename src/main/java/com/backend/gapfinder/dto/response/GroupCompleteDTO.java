package com.backend.gapfinder.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GroupCompleteDTO extends GroupBasicDTO {
    private UserBasicDTO creator;
    private List<UserBasicDTO> members;
}
