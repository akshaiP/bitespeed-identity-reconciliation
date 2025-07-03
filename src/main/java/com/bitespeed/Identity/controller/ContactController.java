package com.bitespeed.Identity.controller;

import com.bitespeed.Identity.dto.IdentifyRequestDTO;
import com.bitespeed.Identity.dto.IdentifyResponseDTO;
import com.bitespeed.Identity.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/identify")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<?> identify(@RequestBody IdentifyRequestDTO request) {
        try {
            IdentifyResponseDTO response = contactService.identify(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Something went wrong", "details", ex.getMessage()));
        }
    }
}
