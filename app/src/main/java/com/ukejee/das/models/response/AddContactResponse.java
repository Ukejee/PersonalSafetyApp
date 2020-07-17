package com.ukejee.das.models.response;

import com.ukejee.das.models.EmergencyContact;

/**
 * @author .: Ukeje Emeka
 * @email ..: ukejee3@gmail.com
 * @created : 6/15/20
 */
public class AddContactResponse {

    private EmergencyContact contact;
    private String message;

    public EmergencyContact getContact() {
        return contact;
    }

    public void setContact(EmergencyContact contact) {
        this.contact = contact;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
