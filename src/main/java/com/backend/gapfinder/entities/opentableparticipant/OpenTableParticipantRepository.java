package com.backend.gapfinder.entities.opentableparticipant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OpenTableParticipantRepository
        extends JpaRepository<OpenTableParticipantEntity, Long> {

    Optional<OpenTableParticipantEntity>
    findByOpenTableIdAndUserId(
            Long openTableId,
            Long userId
    );

    List<OpenTableParticipantEntity>
    findByOpenTableId(Long openTableId);

    // BQ 7: respuestas a planes abiertos, por estado de RSVP.
    //
    // Se excluye al creador de la mesa: entra automaticamente con RSVP = IN
    // al crearla, y contarlo como una invitacion aceptada inflaria el
    // resultado de los planes abiertos frente a las invitaciones directas.
    @Query("""
        SELECT p.rsvp, COUNT(p.id)
        FROM OpenTableParticipantEntity p
        WHERE p.user.id <> p.openTable.creator.id
        GROUP BY p.rsvp
    """)
    List<Object[]> countByRsvpExcludingCreator();
}
