package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.ActivityBasicDTO;
import com.backend.gapfinder.dto.response.GapBasicDTO;
import com.backend.gapfinder.dto.response.MatchBasicDTO;
import com.backend.gapfinder.dto.response.MatchCompleteDTO;
import com.backend.gapfinder.dto.response.UserBasicDTO;
import com.backend.gapfinder.enums.MatchModeEnum;
import com.backend.gapfinder.model.ActivityModel;
import com.backend.gapfinder.model.MatchModel;
import com.backend.gapfinder.service.MatchService;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/matches")
public class MatchController {

    private final MatchService matchService;
    private final ModelMapper modelMapper;

    public MatchController(MatchService matchService, ModelMapper modelMapper) {
        this.matchService = matchService;
        this.modelMapper = modelMapper;
    }

    // Busca candidatos de match para un usuario, ordenados por compatibilidad
    // GET /matches/candidates?userId=1&mode=SAME_SEMESTER
    @GetMapping("/candidates")
    public List<Map<String, Object>> getCandidates(
            @RequestParam Long userId,
            @RequestParam MatchModeEnum mode) {
        List<MatchService.MatchCandidate> candidates = matchService.findMatchCandidates(userId, mode);

        return candidates.stream()
                .map(candidate -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("user", modelMapper.map(candidate.user(), UserBasicDTO.class));
                    item.put("activeGap", modelMapper.map(candidate.activeGap(), GapBasicDTO.class));
                    item.put("compatibility", candidate.compatibility());
                    item.put("commonInterests", candidate.commonInterests());
                    return item;
                })
                .toList();
    }

    // Envía una solicitud de match
    // POST /matches?requesterId=1&receiverId=2
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatchCompleteDTO sendRequest(@RequestParam Long requesterId, @RequestParam Long receiverId) {
        MatchModel match = matchService.sendRequest(requesterId, receiverId);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Acepta un match
    // PATCH /matches/{id}/accept?receiverId=2
    @PatchMapping("/{id}/accept")
    public MatchCompleteDTO accept(@PathVariable Long id, @RequestParam Long receiverId) {
        MatchModel match = matchService.acceptRequest(id, receiverId);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Rechaza un match
    // PATCH /matches/{id}/reject?receiverId=2
    @PatchMapping("/{id}/reject")
    public MatchCompleteDTO reject(@PathVariable Long id, @RequestParam Long receiverId) {
        MatchModel match = matchService.rejectRequest(id, receiverId);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }


    // Obtiene un match dado su id
    // GET /matches/{id}
    @GetMapping("/{id}")
    public MatchCompleteDTO getById(@PathVariable Long id) {
        MatchModel match = matchService.getById(id);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Obtiene el historial de matches de un usuario
    // GET /matches/history?userId=1
    @GetMapping("/history")
    public List<MatchBasicDTO> getHistory(@RequestParam Long userId) {
        List<MatchModel> matches = matchService.getHistoryByUser(userId);
        return modelMapper.map(matches, new TypeToken<List<MatchBasicDTO>>() {}.getType());
    }


    // Calcula el porcentaje de compatibilidad entre dos usuarios
    // GET /matches/compatibility?userAId=1&userBId=2&mode=INTERESTS_EFFORT
    @GetMapping("/compatibility")
    public double getCompatibility(
            @RequestParam Long userAId,
            @RequestParam Long userBId,
            @RequestParam MatchModeEnum mode) {
        return matchService.calculateCompatibility(userAId, userBId, mode);
    }
   
    // Se ELIMINA por completo:
    // @PatchMapping("/{id}/activity")
    // public MatchCompleteDTO chooseActivity(...)

    // Obtiene actividades elegibles para un match según su tiempo de solapamiento real
    // GET /matches/{id}/suggested-activities
    @GetMapping("/{id}/suggested-activities")
    public List<ActivityBasicDTO> getSuggestedActivities(@PathVariable Long id) {
        List<ActivityModel> activities = matchService.getSuggestedActivities(id);
        return modelMapper.map(activities, new TypeToken<List<ActivityBasicDTO>>() {}.getType());
    }

    // Registrar si el usuario repetiría su GAP con la otra persona del match
    // PATCH /matches/{id}/rematch?userId=1&wantsRematch=true
    @PatchMapping("/{id}/rematch")
    public MatchBasicDTO setRematchPreference(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam boolean wantsRematch) {

        MatchModel updated = matchService.setRematchPreference(id, userId, wantsRematch);
        return modelMapper.map(updated, MatchBasicDTO.class);
    }

}