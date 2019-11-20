package teaching.model;

import javax.persistence.*;

@Entity
@Table(name = "account", schema = "teaching-app")
public class Account {

    @Id private String username;
    private String password;
    private String role;

    public Account() {}

    public Account(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String toString() {
        return String.format("Account(%s,%s,%s)", username, password, role);
    }

}
