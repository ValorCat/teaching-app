package teaching.model;

import teaching.Application;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Entity
@Table(name = "account", schema = Application.DB_SCHEMA)
public class Account {

    @Id private String username;
    private byte[] password;
    private String role;
    private LocalDateTime createTime;
    private LocalDateTime lastLoginTime;

    public Account() {}

    public Account(String username, String password, String role) {
        this.username = username;
        this.password = hashPassword(password);
        this.role = role;
        this.createTime = LocalDateTime.now();
        this.lastLoginTime = createTime;
    }

    public String getUsername() {
        return username;
    }

    public byte[] getPassword() {
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
        return String.format("Account(%s,%s)", username, role);
    }

    public static byte[] hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(password.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new HashingException(e);
        }
    }

}
