package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Optional<Account> findByUsername(String username);
    Optional<Account> findByUsernameAndPassword(String username, byte[] password);

    default Account create(String username, String password, String role) {
        return save(new Account(username, password, role));
    }

    default void updateLastLoginTime(Account account) {
        account.updateLastLoginTime();
        save(account);
    }

}
