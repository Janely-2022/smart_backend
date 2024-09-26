package com.zedeck.smartoutletserver.service;


import com.zedeck.smartoutletserver.dto.ChangePasswordDto;
import com.zedeck.smartoutletserver.dto.LoginDto;
import com.zedeck.smartoutletserver.dto.LoginResponseDto;
import com.zedeck.smartoutletserver.dto.UserAccountDto;
import com.zedeck.smartoutletserver.model.UserAccount;
import com.zedeck.smartoutletserver.utils.Response;

public interface AuthService {

    Response<LoginResponseDto> login(LoginDto loginDto);

    Response registerUser(UserAccountDto userAccountDto);
    Response<LoginResponseDto> revokeToken(String refreshToken);
    Response<Boolean> forgetPassword(String email);
    Response<UserAccount> getLoggedUser();
    Response<UserAccount> updatePassword(ChangePasswordDto changePasswordDto);
//    Response<String>  activateAccount(String code);
    Response<String> requestNewOtp(String phone);

}

