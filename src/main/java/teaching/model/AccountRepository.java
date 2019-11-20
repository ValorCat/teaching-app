package teaching.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    Account findFirstByUsername(String username);
    Account findFirstByUsernameAndPassword(String username, String password);

    default Account create(String username, String password, String role) {
        Account account = new Account(username, password, role);
        save(account);
        return account;
    }

}
