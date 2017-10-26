package org.eclipse.yasson.adapters.model;

import javax.json.bind.annotation.JsonbTypeAdapter;

public class Author {
    @JsonbTypeAdapter(FirstNameAdapter.class)
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
