package com.backend.gapfinder.dto.response;

import lombok.Data;

@Data
public class BuildingBasicDTO {
	private Long id;
	private String name;
	private double latitude;
	private double longitude;
	private double radiusMeters;
}
