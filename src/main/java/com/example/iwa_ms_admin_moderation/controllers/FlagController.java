package com.example.iwa_ms_admin_moderation.controllers;

import com.example.iwa_ms_admin_moderation.models.Flags;
import com.example.iwa_ms_admin_moderation.services.FlagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/flags")
public class FlagController {

    private final FlagService flagService;

    @Autowired
    public FlagController(FlagService flagService) {
        this.flagService = flagService;
    }

    // Endpoint pour créer un nouveau flag
    @PostMapping
    public ResponseEntity<Flags> createFlag(@RequestBody Flags flag) {
        Flags createdFlag = flagService.createFlag(flag);
        return new ResponseEntity<>(createdFlag, HttpStatus.CREATED);
    }

    // Endpoint pour récupérer tous les flags
    @GetMapping
    public ResponseEntity<List<Flags>> getAllFlags() {
        List<Flags> flags = flagService.getAllFlags();
        return ResponseEntity.ok(flags);
    }

    // Endpoint pour récupérer un flag par ID
    @GetMapping("/{flagId}")
    public ResponseEntity<Flags> getFlagById(@PathVariable Integer flagId) {
        Optional<Flags> flag = flagService.getFlagById(flagId);
        return flag.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Endpoint pour mettre à jour un flag
    @PutMapping("/{flagId}")
    public ResponseEntity<Flags> updateFlag(@PathVariable Integer flagId, @RequestBody Flags updatedFlag) {
        if (!flagService.getFlagById(flagId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        updatedFlag.setFlagId(flagId); // Assurer la mise à jour du bon flag
        Flags flag = flagService.updateFlag(updatedFlag);
        return ResponseEntity.ok(flag);
    }

    // Endpoint pour supprimer un flag par ID
    @DeleteMapping("/{flagId}")
    public ResponseEntity<Void> deleteFlag(@PathVariable Integer flagId) {
        if (!flagService.getFlagById(flagId).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        flagService.deleteFlag(flagId);
        return ResponseEntity.noContent().build();
    }

    // Endpoint pour récupérer les flags par statut
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Flags>> getFlagsByStatus(@PathVariable String status) {
        List<Flags> flags = flagService.getFlagsByStatus(status);
        return ResponseEntity.ok(flags);
    }
    // Endpoint pour récupérer les flags avec locationId non nul et status 'pending'
    @GetMapping("/locations")
    public ResponseEntity<List<Flags>> getFlagsByLocation() {
        List<Flags> flags = flagService.getFlagsByLocationAndStatus("pending");
        return ResponseEntity.ok(flags);
    }
    // Endpoint pour récupérer les flags avec commentId non nul et status 'pending'
    @GetMapping("/comments")
    public ResponseEntity<List<Flags>> getFlagsByComment() {
        List<Flags> flags = flagService.getFlagsByCommentAndStatus("pending");
        return ResponseEntity.ok(flags);
    }
    
}
