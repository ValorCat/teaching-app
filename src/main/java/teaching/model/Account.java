package teaching.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "account", schema = "teaching-app")
public class Account {

    @Id private String username;
    private String password;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public LocalDateTime getLastLoginTime() {
        return lastLoginTime;
    }

    public void updateLastLoginTime() {
        this.lastLoginTime = LocalDateTime.now();
    }

    public String toString() {
        return String.format("Account(%s,%s,%s)", username, password, role);
    }

}
