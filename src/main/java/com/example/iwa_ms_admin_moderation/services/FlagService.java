package com.example.iwa_ms_admin_moderation.services;

import com.example.iwa_ms_admin_moderation.models.Flags;
import com.example.iwa_ms_admin_moderation.repositories.FlagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FlagService {

    private final FlagRepository flagRepository;
    private final RestTemplate restTemplate;

    @Autowired
    public FlagService(FlagRepository flagRepository, RestTemplate restTemplate) {
        this.flagRepository = flagRepository;
        this.restTemplate = restTemplate;
    }

    // Créer un nouveau flag
    public Flags createFlag(Flags flag) {
        // Enregistrer le flag dans la base de données
        Flags savedFlag = flagRepository.save(flag);

        // Appeler le service de notifications
        createFlaggedNotification(flag);

        return savedFlag;
    }

    // Récupérer tous les flags
    public List<Flags> getAllFlags() {
        return flagRepository.findAll();
    }

    // Récupérer un flag par ID
    public Optional<Flags> getFlagById(Integer flagId) {
        return flagRepository.findById(flagId);
    }

    // Mettre à jour un flag
    public Flags updateFlag(Flags flag) {
        return flagRepository.save(flag);
    }

    // Supprimer un flag par ID
    public void deleteFlag(Integer flagId) {
        flagRepository.deleteById(flagId);
    }

    // Récupérer les flags par statut
    public List<Flags> getFlagsByStatus(String status) {
        return flagRepository.findByStatus(status);
    }

    // Appeler le service de notifications
    private void createFlaggedNotification(Flags flag) {
        if (flag.getLocationId() == null && flag.getCommentId() == null) {
            throw new IllegalArgumentException("Location ID et Comment ID ne peuvent pas être tous les deux nuls");
        }

        Map<String, Object> requestBody;
        String notificationUrl = "http://host.docker.internal:8085/notifications/create/flagged";

        try {
            if (flag.getLocationId() != null) {
                requestBody = Map.of(
                        "locationId", flag.getLocationId(),
                        "reason", flag.getReason()
                );
            } else {
                requestBody = Map.of(
                        "commentId", flag.getCommentId(),
                        "reason", flag.getReason()
                );
            }

            // Appel à la route POST du ms_notification
            restTemplate.postForEntity(notificationUrl, requestBody, Void.class);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'appel au service de notifications : " + e.getMessage());
        }
    }

    // Récupérer les flags avec locationId non nul et status 'pending'
    public List<Flags> getFlagsByLocationAndStatus(String status) {
        return flagRepository.findByLocationIdNotNullAndStatus(status);
    }

    // Récupérer les flags avec commentId non nul et status 'pending'
    public List<Flags> getFlagsByCommentAndStatus(String status) {
        return flagRepository.findByCommentIdNotNullAndStatus(status);
    }
}
