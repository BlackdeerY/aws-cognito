package io.blackdeer.cognito.account;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findBySub(String sub);
    Optional<Account> findByUsername(String username);    // Cognito에서 username은 unique를 보장하진 않지만, idp를 통해 unique가 보장되며, idp만 사용하는 형태라서.
    List<Account> findAllByEmailEquals(String email);
}
