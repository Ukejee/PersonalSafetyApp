package com.ukejee.das.models.response;

import com.ukejee.das.models.EmergencyContact;

import java.util.List;

/**
 * @author .: Ukeje Emeka
 * @email ..: ukejee3@gmail.com
 * @created : 6/17/20
 */
public class GetContactsResponse {

    private List<EmergencyContact> contacts;
    private String message;

    public List<EmergencyContact> getContacts() {
        return contacts;
    }

    public void setContacts(List<EmergencyContact> contacts) {
        this.contacts = contacts;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
