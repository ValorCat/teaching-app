package teaching.model;

import javax.persistence.*;

@Entity
@Table(name = "account", schema = "teaching-app")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int accountId;

    private String username;
    private String password;

    public int getAccountId() {
        return accountId;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String toString() {
        return String.format("Account(%d,%s,%s)", accountId, username, password);
    }

}
