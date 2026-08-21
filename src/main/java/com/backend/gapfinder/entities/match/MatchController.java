package com.backend.gapfinder.entities.match;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.backend.gapfinder.entities.user.UserBasicDTO;
import com.backend.gapfinder.entities.activity.ActivityBasicDTO;
import com.backend.gapfinder.entities.activity.ActivityEntity;
import com.backend.gapfinder.entities.gap.GapBasicDTO;

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
    // GET /matches/candidates?userId=1
    @GetMapping("/candidates")
    public List<Map<String, Object>> getCandidates(@RequestParam Long userId) {
        List<MatchService.MatchCandidate> candidates = matchService.findMatchCandidates(userId);

        return candidates.stream()
                .map(candidate -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("user", modelMapper.map(candidate.user(), UserBasicDTO.class));
                    item.put("activeGap", modelMapper.map(candidate.activeGap(), GapBasicDTO.class));
                    item.put("compatibility", candidate.compatibility());
                    return item;
                })
                .toList();
    }

    // Envía una solicitud de match
    // POST /matches?requesterId=1&receiverId=2
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MatchCompleteDTO sendRequest(@RequestParam Long requesterId, @RequestParam Long receiverId) {
        MatchEntity match = matchService.sendRequest(requesterId, receiverId);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Acepta un match
    // PATCH /matches/{id}/accept?receiverId=2
    @PatchMapping("/{id}/accept")
    public MatchCompleteDTO accept(@PathVariable Long id, @RequestParam Long receiverId) {
        MatchEntity match = matchService.acceptRequest(id, receiverId);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Rechaza un match
    // PATCH /matches/{id}/reject?receiverId=2
    @PatchMapping("/{id}/reject")
    public MatchCompleteDTO reject(@PathVariable Long id, @RequestParam Long receiverId) {
        MatchEntity match = matchService.rejectRequest(id, receiverId);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Cancela una solicitud de match
    // PATCH /matches/{id}/cancel?requesterId=1
    @PatchMapping("/{id}/cancel")
    public MatchCompleteDTO cancel(@PathVariable Long id, @RequestParam Long requesterId) {
        MatchEntity match = matchService.cancelRequest(id, requesterId);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Obtiene un match dado su id
    // GET /matches/{id}
    @GetMapping("/{id}")
    public MatchCompleteDTO getById(@PathVariable Long id) {
        MatchEntity match = matchService.getById(id);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Obtiene el historial de matches de un usuario
    // GET /matches/history?userId=1
    @GetMapping("/history")
    public List<MatchBasicDTO> getHistory(@RequestParam Long userId) {
        List<MatchEntity> matches = matchService.getHistoryByUser(userId);
        return modelMapper.map(matches, new TypeToken<List<MatchBasicDTO>>() {}.getType());
    }

    // Un participante del match propone una actividad
    // PATCH /matches/{id}/activity?userId=1&activityId=5
    @PatchMapping("/{id}/activity")
    public MatchCompleteDTO chooseActivity(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam Long activityId) {
        MatchEntity match = matchService.chooseActivity(id, userId, activityId);
        return modelMapper.map(match, MatchCompleteDTO.class);
    }

    // Calcula el porcentaje de compatibilidad entre dos usuarios
    // GET /matches/compatibility?userAId=1&userBId=2
    @GetMapping("/compatibility")
    public double getCompatibility(@RequestParam Long userAId, @RequestParam Long userBId) {
        return matchService.calculateCompatibility(userAId, userBId);
    }
   
    // Obtiene actividades sugeridas para un match según el tiempo disponible
    // GET /matches/{id}/suggested-activities?availableMinutes=60
    @GetMapping("/{id}/suggested-activities")
    public List<ActivityBasicDTO> getSuggestedActivities(
            @PathVariable Long id,
            @RequestParam Integer availableMinutes) {
        List<ActivityEntity> activities = matchService.getSuggestedActivities(id, availableMinutes);
        return modelMapper.map(activities, new TypeToken<List<ActivityBasicDTO>>() {}.getType());
    }

    // Registrar si el usuario repetiría su GAP con la otra persona del match
    // PATCH /matches/{id}/rematch?userId=1&wantsRematch=true
    @PatchMapping("/{id}/rematch")
    public MatchBasicDTO setRematchPreference(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam boolean wantsRematch) {

        MatchEntity updated = matchService.setRematchPreference(id, userId, wantsRematch);
        return modelMapper.map(updated, MatchBasicDTO.class);
    }

}