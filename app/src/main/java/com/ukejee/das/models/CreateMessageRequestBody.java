package com.ukejee.das.models;

/**
 * @author .: Ukeje Emeka
 * @email ..: ukejee3@gmail.com
 * @created : 6/16/20
 */
public class CreateMessageRequestBody {

    private String text;

    public CreateMessageRequestBody(String text){
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
