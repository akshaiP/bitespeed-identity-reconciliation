package com.bitespeed.Identity.service;

import com.bitespeed.Identity.dto.IdentifyRequestDTO;
import com.bitespeed.Identity.dto.IdentifyResponseDTO;
import com.bitespeed.Identity.model.Contact;
import com.bitespeed.Identity.model.LinkPrecedence;
import com.bitespeed.Identity.repository.ContactRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    @Transactional
    public IdentifyResponseDTO identify(IdentifyRequestDTO request) {
        String email = request.getEmail();
        String phone = request.getPhoneNumber();

        if (email == null && phone == null) {
            throw new IllegalArgumentException("At least one of email or phoneNumber must be provided.");
        }

        List<Contact> matchedContacts = contactRepository.findByEmailOrPhoneNumber(email, phone);

        if (matchedContacts.isEmpty()) {

            Contact newContact = Contact.builder()
                    .email(email)
                    .phoneNumber(phone)
                    .linkPrecedence(LinkPrecedence.PRIMARY)
                    .createdAt(LocalDateTime.now())
                    .build();

            Contact saved = contactRepository.save(newContact);

            return IdentifyResponseDTO.builder()
                    .contact(IdentifyResponseDTO.ContactInfo.builder()
                            .primaryContactId(saved.getId())
                            .emails(email != null ? List.of(email) : List.of())
                            .phoneNumbers(phone != null ? List.of(phone) : List.of())
                            .secondaryContactIds(List.of())
                            .build())
                    .build();
        }

        Contact primary = matchedContacts.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.PRIMARY)
                .min(Comparator.comparing(Contact::getCreatedAt))
                .orElseThrow(() -> new IllegalStateException("No primary contact found in matched set."));

        // Handle conflict: convert other primaries to secondary
        for (Contact contact : matchedContacts) {
            if (contact.getLinkPrecedence() == LinkPrecedence.PRIMARY && !contact.getId().equals(primary.getId())) {
                contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
                contact.setLinkedId(primary.getId());
                contact.setUpdatedAt(LocalDateTime.now());
                contactRepository.save(contact);
            }
        }

        boolean emailExists = email != null && matchedContacts.stream().anyMatch(c -> email.equals(c.getEmail()));
        boolean phoneExists = phone != null && matchedContacts.stream().anyMatch(c -> phone.equals(c.getPhoneNumber()));
        boolean newInfo = (email != null && !emailExists) || (phone != null && !phoneExists);

        Contact newSecondary = null;
        if (newInfo) {
            newSecondary = Contact.builder()
                    .email(email)
                    .phoneNumber(phone)
                    .linkedId(primary.getId())
                    .linkPrecedence(LinkPrecedence.SECONDARY)
                    .createdAt(LocalDateTime.now())
                    .build();

            contactRepository.save(newSecondary);
            matchedContacts.add(newSecondary);
        }

        // Group all linked contacts
        Set<Contact> allLinked = new HashSet<>(matchedContacts);
        List<Contact> secondaries = allLinked.stream()
                .filter(c -> c.getLinkPrecedence() == LinkPrecedence.SECONDARY)
                .collect(Collectors.toList());

        Set<String> allEmails = allLinked.stream()
                .map(Contact::getEmail)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Set<String> allPhones = allLinked.stream()
                .map(Contact::getPhoneNumber)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));
        List<Long> secondaryIds = secondaries.stream().map(Contact::getId).collect(Collectors.toList());

        return IdentifyResponseDTO.builder()
                .contact(IdentifyResponseDTO.ContactInfo.builder()
                        .primaryContactId(primary.getId())
                        .emails(new ArrayList<>(allEmails))
                        .phoneNumbers(new ArrayList<>(allPhones))
                        .secondaryContactIds(secondaryIds)
                        .build())
                .build();
    }
}
