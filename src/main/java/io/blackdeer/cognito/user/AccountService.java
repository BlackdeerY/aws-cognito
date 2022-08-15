package io.blackdeer.cognito.user;

import io.blackdeer.cognito.aws.CognitoUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean insertAccount(@NonNull CognitoUser cognitoUser) {
        assert (cognitoUser != null);
        Account account = new Account(cognitoUser.getSub(), cognitoUser.getUsername(), cognitoUser.getEmail());
        try {
            Account saveAccount = accountRepository.save(account);
            if (saveAccount.equals(account)) {
                return true;
            }
            System.out.println("저장실패");
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return false;
        }
    }
}
