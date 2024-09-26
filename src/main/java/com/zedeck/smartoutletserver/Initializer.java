package com.zedeck.smartoutletserver;

import com.zedeck.smartoutletserver.model.UserAccount;
import com.zedeck.smartoutletserver.repository.UserAccountRepository;
import com.zedeck.smartoutletserver.utils.UserType;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class Initializer implements ApplicationRunner {

    private final Logger logger = LoggerFactory.getLogger(Initializer.class);
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {

            UserAccount userAccount;

            Optional<UserAccount> optionalUserAccount = userAccountRepository.findFirstByUsername("admin@smartOutlet.ega.tz");

            if (optionalUserAccount.isEmpty()) {
                userAccount = new UserAccount();

                logger.info("=============  CREATING DEFAULT USER ================");

                userAccount.setUsername("admin@smartOutlet.ega.tz");
                userAccount.setFullname("Super Admin");
                userAccount.setUserType(String.valueOf(UserType.SUPER_ADMIN));
                userAccount.setPassword(passwordEncoder.encode("eg@2024")); // Call encode() on the injected PasswordEncoder
                userAccount.setEnabled(true);

                userAccountRepository.save(userAccount);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
