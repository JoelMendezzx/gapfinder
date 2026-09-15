package com.backend.gapfinder.entities.rating;

import com.backend.gapfinder.entities.match.MatchBasicDTO;
import com.backend.gapfinder.entities.opentable.OpenTableBasicDTO;
import com.backend.gapfinder.entities.user.UserBasicDTO;
import lombok.Data;


@Data
public class RatingCompleteDTO extends RatingBasicDTO {

    private UserBasicDTO rater;
    private UserBasicDTO ratedUser;
    private MatchBasicDTO match;
    private OpenTableBasicDTO openTable;
}
