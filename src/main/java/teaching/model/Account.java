package teaching.model;

import javax.persistence.*;

@Entity
@Table(name = "account", schema = "teaching-app")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String username;
    private String password;
    private String role;

    public int getId() {
        return id;
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
        return String.format("Account(%d,%s,%s)", id, username, password);
    }

}
