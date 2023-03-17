package com.example.demo.service;

import com.example.demo.constants.Constants;
import com.example.demo.model.Contact;
import com.example.demo.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Slf4j
@Service

//@SessionScope
//@RequestScope
//@ApplicationScope
public class ContactService {


    @Autowired
    private ContactRepository contactRepository;


    public boolean saveMessageDetails(Contact contact) {
        boolean isSaved = false;
        contact.setStatus(Constants.OPEN);

        Contact savedContact = contactRepository.save(contact);

        if (null != savedContact && savedContact.getContactId() > 0) {
            isSaved = true;
        }
        return isSaved;
    }

    public Page<Contact> findMsgsWithOpenStatus(int pageNum, String sortField, String sortDir) {
        int pageSize = 5;

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize,
                sortDir.equals("asc") ? Sort.by(sortField).ascending() : Sort.by(sortField).descending());
//        Page<Contact> msgPage = contactRepository.findByStatus(Constants.OPEN, pageable);
        Page<Contact> msgPage = contactRepository.findOpenMsgs(Constants.OPEN, pageable);

        return msgPage;
    }

    public boolean updateMsgStatus(int contactId) {
        boolean isUpdated = false;

//        Optional<Contact> contact = contactRepository.findById(contactId);
//        contact.ifPresent(contact1 -> {
//            contact1.setStatus(Constants.CLOSE);
//
//        });


//        Contact updatedContact = contactRepository.save(contact.get());


//        if (null != updatedContact && updatedContact.getUpdatedBy() != null) {
//            isUpdated = true;
//        }
//        int rows = contactRepository.updateStatusById(Constants.CLOSE, contactId);
        int rows = contactRepository.updateMsgStatus(Constants.CLOSE, contactId);
        if (rows > 0) {
            isUpdated = true;
        }
        return isUpdated;
    }
}
