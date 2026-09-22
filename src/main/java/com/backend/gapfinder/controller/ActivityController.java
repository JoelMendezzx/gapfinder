package com.backend.gapfinder.controller;

import com.backend.gapfinder.dto.response.ActivityBasicDTO;
import com.backend.gapfinder.dto.response.ActivityCompleteDTO;
import com.backend.gapfinder.enums.ActivityEffortEnum;
import com.backend.gapfinder.model.ActivityModel;
import com.backend.gapfinder.service.ActivityService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {

    private final ActivityService activityService;
    private final ModelMapper modelMapper;

    public ActivityController(ActivityService activityService, ModelMapper modelMapper) {
        this.activityService = activityService;
        this.modelMapper = modelMapper;
    }

    // POST /activities?interestId=1
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityCompleteDTO createActivity(
            @RequestParam Long interestId,
            @RequestBody ActivityBasicDTO activityDTO) {
        ActivityModel activity = modelMapper.map(activityDTO, ActivityModel.class);
        ActivityModel created = activityService.create(activity, interestId);
        return modelMapper.map(created, ActivityCompleteDTO.class);
    }

    // GET /activities
    @GetMapping
    public List<ActivityCompleteDTO> getAllActivities() {
        List<ActivityModel> activities = activityService.getAll();
        return modelMapper.map(activities, new TypeToken<List<ActivityCompleteDTO>>() {}.getType());
    }

    // GET /activities/{id}
    @GetMapping("/{id}")
    public ActivityCompleteDTO getActivityById(@org.springframework.web.bind.annotation.PathVariable Long id) {
        ActivityModel activity = activityService.getById(id);
        return modelMapper.map(activity, ActivityCompleteDTO.class);
    }

    // GET /activities/filter?durationMinutes=60&effort=ACTIVE&interestId=1
    @GetMapping("/filter")
    public List<ActivityCompleteDTO> findByFilters(
            @RequestParam Integer durationMinutes,
            @RequestParam ActivityEffortEnum effort,
            @RequestParam Long interestId) {
        List<ActivityModel> activities = activityService.findByFilters(durationMinutes, effort, interestId);
        return modelMapper.map(activities, new TypeToken<List<ActivityCompleteDTO>>() {}.getType());
    }
}