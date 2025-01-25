package com.example.sfera_education.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.sfera_education.entity.Contact;
import com.example.sfera_education.entity.District;
import com.example.sfera_education.payload.ApiResponse;
import com.example.sfera_education.payload.ContactDTO;
import com.example.sfera_education.payload.ResponseError;
import com.example.sfera_education.payload.res.ResContact;
import com.example.sfera_education.repository.ContactRepository;
import com.example.sfera_education.repository.DistrictRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final DistrictRepository districtRepository;

    public ApiResponse saveContact(ResContact resContact) {
        District district = districtRepository.findById(resContact.getDistrictId()).orElse(null);
        if (district == null) {
            return new ApiResponse(ResponseError.NOTFOUND("District "));
        }

        Contact contact = Contact.builder()
                .street(resContact.getStreet())
                .district(district)
                .build();
        contactRepository.save(contact);
        return new ApiResponse("Contact saved");
    }


    public ApiResponse getAllContacts() {
        List<Contact> contacts = contactRepository.findAll();
        if (contacts.isEmpty()) {
            return new ApiResponse(ResponseError.NOTFOUND("Contact"));
        }

        List<ContactDTO> list = contacts.stream().map(this::contactDTO).toList();

        return new ApiResponse(list);
    }


    public ApiResponse getContactById(Integer id) {
        return contactRepository.findById(id)
                .map(contact -> new ApiResponse(contactDTO(contact)))
                .orElseGet(() -> new ApiResponse(ResponseError.NOTFOUND("Contact")));
    }


    public ApiResponse updateContact(Integer contactId, ResContact resContact) {
        Contact contact = contactRepository.findById(contactId).orElse(null);
        if (contact == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Contact"));
        }

        District district = districtRepository.findById(resContact.getDistrictId()).orElse(null);
        if (district == null) {
            return new ApiResponse(ResponseError.NOTFOUND("District"));
        }

        contact.setId(contactId);
        contact.setStreet(resContact.getStreet());
        contact.setDistrict(district);
        contactRepository.save(contact);
        return new ApiResponse("Contact updated");
    }


    public ApiResponse deleteContact(Integer contactId) {
        Contact contact = contactRepository.findById(contactId).orElse(null);
        if (contact == null) {
            return new ApiResponse(ResponseError.NOTFOUND("Contact"));
        }

        contactRepository.delete(contact);
        return new ApiResponse("Contact deleted");
    }


    public ContactDTO contactDTO(Contact contact) {
        return ContactDTO.builder()
                .id(contact.getId())
                .street(contact.getStreet())
                .districtName(contact.getDistrict().getName())
                .build();
    }
}
