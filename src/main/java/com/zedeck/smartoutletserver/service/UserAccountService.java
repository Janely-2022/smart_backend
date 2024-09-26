package com.zedeck.smartoutletserver.service;


import com.zedeck.smartoutletserver.dto.UserAccountDto;
import com.zedeck.smartoutletserver.model.UserAccount;
import com.zedeck.smartoutletserver.utils.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserAccountService {
    Response<UserAccount> createUpdateUser(UserAccountDto userAccountDto);

    Response<UserAccount> deleteUserAccount(String uuid);

    Response<UserAccount> getUserByUuid(String uuid);

    Page<UserAccount> getCustomers(Pageable pageable);

    Page<UserAccount> getAllUsers(Pageable pageable);

    Page<UserAccount>  getOfficials (Pageable pageable);

//    Page<UserAccount>  getVendors (Pageable pageable);

    Page<UserAccount> getVendors(Pageable pageable);

//    Response<UserAccount>  updateYourBio(BioDto bioDto);

}
