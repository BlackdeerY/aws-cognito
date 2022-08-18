package io.blackdeer.cognito.user;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

//    @Transactional(propagation = Propagation.MANDATORY, readOnly = false)
//    public boolean insertAccount(@NonNull CognitoUser cognitoUser) {
//        assert (cognitoUser != null);
//        Account account = new Account(cognitoUser.getSub(), cognitoUser.getUsername(), cognitoUser.getEmail());
//        try {
//            Account saveAccount = accountRepository.save(account);
//            if (saveAccount.equals(account)) {
//                return true;
//            }
//            System.out.println("저장실패");
//            return false;
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            return false;
//        }
//    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Account getAccountOrNull(@NonNull JSONObject userinfo) {
        assert (userinfo != null);
        assert (userinfo.has("sub"));
        Optional<Account> accountOptional = accountRepository.findBySub(userinfo.getString("sub"));
        if (accountOptional.isPresent() == false) {
            return null;
        }
        return accountOptional.get();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Account getAccountOrNull(@NonNull String sub) {
        assert (sub != null);
        Optional<Account> accountOptional = accountRepository.findBySub(sub);
        if (accountOptional.isPresent() == false) {
            return null;
        }
        return accountOptional.get();
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = false)
    public boolean insertAccount(@NonNull JSONObject userinfo) {
        assert (userinfo != null);
        Account account = new Account(userinfo);
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
