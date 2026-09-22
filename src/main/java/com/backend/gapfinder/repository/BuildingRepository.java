package com.backend.gapfinder.repository;

import com.backend.gapfinder.model.BuildingModel;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<BuildingModel, Long> {

    Optional<BuildingModel> findByName(String name);

    // Determina en qué edificio se encuentra el usuario: de los edificios cuyo
    // radio alcanza el punto dado, retorna el más cercano al centro
    @Query(value = """
            SELECT b.* FROM buildings b 
            WHERE ST_DWithin(b.location, :userLocation, b.radius_meters)
            ORDER BY ST_Distance(b.location, :userLocation)
            LIMIT 1
            """, nativeQuery = true)
    Optional<BuildingModel> findBuildingAtUserLocation(@Param("userLocation") Point userLocation);
}