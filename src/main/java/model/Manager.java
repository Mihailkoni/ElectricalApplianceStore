package model;

public class Manager {
    private String login;
    private String email;

    public Manager(String login, String email) {
        this.login = login;
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "login='" + login + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}