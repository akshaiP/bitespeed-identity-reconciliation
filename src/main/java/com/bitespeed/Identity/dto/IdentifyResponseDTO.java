package com.bitespeed.Identity.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IdentifyResponseDTO {
    private ContactInfo contact;

    @Data
    @Builder
    public static class ContactInfo {
        private Long primaryContactId;
        private List<String> emails;
        private List<String> phoneNumbers;
        private List<Long> secondaryContactIds;
    }
}
