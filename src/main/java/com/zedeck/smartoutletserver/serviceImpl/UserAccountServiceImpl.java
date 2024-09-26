package com.zedeck.smartoutletserver.serviceImpl;


import com.zedeck.smartoutletserver.dto.UserAccountDto;
import com.zedeck.smartoutletserver.model.UserAccount;
import com.zedeck.smartoutletserver.repository.UserAccountRepository;
import com.zedeck.smartoutletserver.service.UserAccountService;
import com.zedeck.smartoutletserver.utils.Response;
import com.zedeck.smartoutletserver.utils.ResponseCode;
import com.zedeck.smartoutletserver.utils.UserType;
import com.zedeck.smartoutletserver.utils.userextractor.LoggedUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    private Logger logger = LoggerFactory.getLogger(UserAccountServiceImpl.class);

    @Autowired
    private UserAccountRepository userAccountRepository;
    private UserAccount userAccount;

    @Autowired
    private LoggedUser loggedUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Response<UserAccount> createUpdateUser(UserAccountDto userAccountDto) {
        try {
            UserAccount user =  loggedUser.getUser();

            if(user  == null){
                logger.info("UNAUTHORIZED USER TRYING TO CREATE USER");
                return new Response<>(true, ResponseCode.UNAUTHORIZED, "Full authentication is required");
            }

            Optional<UserAccount> accountOptional =  userAccountRepository.findFirstByUsername(userAccountDto.getUsername());

            if(accountOptional.isPresent()){
                return new Response<>(true,ResponseCode.DUPLICATE_EMAIL,"Username already exist");
            }


            UserAccount userAccount1 =  new UserAccount();

            if(userAccountDto.getFullname() == null){
                return new Response<>(true,ResponseCode.NULL_ARGUMENT, "Fullname can not be null");
            }

            if(userAccountDto.getUsername() == null){
                return new Response<>(true,ResponseCode.NULL_ARGUMENT,"Username can not be empty");
            }


            if(!userAccountDto.getFullname().isBlank() && !Objects.equals(userAccountDto.getFullname(),userAccount1.getFullname()))
                userAccount1.setFullname(userAccountDto.getFullname());

            if(!userAccountDto.getUsername().isBlank() && !Objects.equals(userAccountDto.getUsername(), userAccount1.getUsername()))
                userAccount1.setUsername(userAccountDto.getUsername());

            if(userAccountDto.getUserRole() == null){
                userAccount1.setUserType(String.valueOf(UserType.CUSTOMER));
            }
            else if(userAccountDto.getUserRole().equalsIgnoreCase(UserType.ADMIN.name()))
                userAccount1.setUserType(String.valueOf(UserType.ADMIN));
            else if (userAccountDto.getUserRole().equalsIgnoreCase(UserType.SUPER_ADMIN.name()))
                userAccount1.setUserType(String.valueOf(UserType.SUPER_ADMIN));
            else if (userAccountDto.getUserRole().equalsIgnoreCase(UserType.SELLER.name()))
                userAccount1.setUserType(String.valueOf(UserType.SELLER));
            else if(userAccountDto.getUserRole().equalsIgnoreCase(UserType.VENDOR.name()))
                userAccount1.setUserType(String.valueOf(UserType.VENDOR));
            else userAccount1.setUserType(String.valueOf(UserType.CUSTOMER));

            if(userAccount1.getPassword() == null){
                userAccount1.setPassword(passwordEncoder.encode(userAccountDto.getFullname().trim().toUpperCase().trim()));
            }

            UserAccount userAccount2 =  userAccountRepository.save(userAccount1);

            return new Response<>(false, ResponseCode.SUCCESS,userAccount2,"User created successfully");

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return new Response<>(true,ResponseCode.FAIL,"Failed to create user");
    }

    @Override
    public Response<UserAccount> deleteUserAccount(String uuid) {
        try {
             UserAccount user = loggedUser.getUser();

             if(user == null){
                 logger.info("UNAUTHORIZED USER TRYING TO DELETE USER");
                 return new Response<>(true,ResponseCode.UNAUTHORIZED, "Full authentication ir required");
             }

             Optional<UserAccount> accountOptional = userAccountRepository.findFirstByUuid(uuid);

             if(accountOptional.isEmpty())
                 return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"No record found");

             userAccountRepository.delete(accountOptional.get());

             return new Response<>(false, ResponseCode.SUCCESS,"User deleted successfully");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.FAIL,"Failed to create user");
    }

    @Override
    public Response<UserAccount> getUserByUuid(String uuid) {
        try {
            Optional<UserAccount> optionalUserAccount = userAccountRepository.findFirstByUuid(uuid);
            if(optionalUserAccount.isPresent()) {
                return new Response<>(false, ResponseCode.SUCCESS, optionalUserAccount.get(), "User found");
            }
            else {
                return new Response<>(true,ResponseCode.NO_RECORD_FOUND,"User not  found");
            }

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(false,ResponseCode.FAIL,"Operation failed");
    }

    @Override
    public Page<UserAccount> getCustomers(Pageable pageable) {
        try {
             return userAccountRepository.findAllByUserType(String.valueOf(UserType.CUSTOMER),pageable);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<UserAccount> getAllUsers(Pageable pageable) {
        try {
            return userAccountRepository.findAllByDeletedFalse(pageable);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return new PageImpl<>(new ArrayList<>());
    }

    @Override
    public Page<UserAccount> getOfficials(Pageable pageable) {
       try{
           Page<UserAccount> accountPage = userAccountRepository.findAllByUserTypeNot(String.valueOf(UserType.CUSTOMER), pageable);
           return accountPage;
       }
       catch (Exception e){
           e.printStackTrace();
           return new PageImpl<>(new ArrayList<>());
       }

    }

    @Override
    public Page<UserAccount> getVendors(Pageable pageable) {
        try{
            Page<UserAccount> accountPage = userAccountRepository.findAllByUserType(String.valueOf(UserType.VENDOR), pageable);
            return accountPage;
        }
        catch (Exception e){
            e.printStackTrace();
            return new PageImpl<>(new ArrayList<>());
        }

    }



}
