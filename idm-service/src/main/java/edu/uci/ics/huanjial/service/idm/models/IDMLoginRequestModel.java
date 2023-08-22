package edu.uci.ics.huanjial.service.idm.models;

public class IDMLoginRequestModel {
    private String email;
    private char[] password;

    public IDMLoginRequestModel() {
    }

    public IDMLoginRequestModel(String email, char[] password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
