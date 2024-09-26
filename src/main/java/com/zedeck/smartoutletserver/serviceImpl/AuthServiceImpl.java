package com.zedeck.smartoutletserver.serviceImpl;

import com.zedeck.smartoutletserver.dto.ChangePasswordDto;
import com.zedeck.smartoutletserver.dto.LoginDto;
import com.zedeck.smartoutletserver.dto.LoginResponseDto;
import com.zedeck.smartoutletserver.dto.UserAccountDto;
import com.zedeck.smartoutletserver.jwt.JWTUtils;
import com.zedeck.smartoutletserver.model.UserAccount;
import com.zedeck.smartoutletserver.repository.UserAccountRepository;
import com.zedeck.smartoutletserver.service.AuthService;
import com.zedeck.smartoutletserver.utils.Response;
import com.zedeck.smartoutletserver.utils.ResponseCode;
import com.zedeck.smartoutletserver.utils.UserType;
import com.zedeck.smartoutletserver.utils.userextractor.LoggedUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private UserAccountRepository accountRepository;




    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private LoggedUser loggedUser;

    @Autowired
    private JWTUtils jwtUtils;



    @Override
    public Response<LoginResponseDto> login(LoginDto loginDto) {
        try {
            log.info("LOGIN CREDENTIALS : " , loginDto);

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwtToken = jwtUtils.generateJwtToken(authentication);
            String refreshToken = UUID.randomUUID().toString();

            Optional<UserAccount> accountOptional = accountRepository.findFirstByUsername(authentication.getName());

            if(accountOptional.isEmpty())
                return new Response<>(true, ResponseCode.FAIL,"Invalid login credentials");

            UserAccount userAccount = accountOptional.get();
            if(!userAccount.getActive()) {
                log.info("ACCOUNT NOT ACTIVATED");
                return new Response<>(true, ResponseCode.BAD_REQUEST, "Please activate your account first");
            }
            else {
                return getLoginResponseResponse(accountOptional, jwtToken, refreshToken);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.FAIL,"Invalid login credentials");
    }

    @Override
    public Response<UserAccount> registerUser(UserAccountDto userAccountDto) {
        try {


            if (userAccountDto.getFullname() == null) {
                return new Response<>(true, ResponseCode.FAIL,"fullname can not be null");
            }

            if (userAccountDto.getUsername() == null || userAccountDto.getPassword() == null) {
                return new Response<>(true, ResponseCode.FAIL,"Invalid username or password");
            }

            if (userAccountDto.getFullname().isBlank()) {
                return new Response<>(true, ResponseCode.FAIL,"Fill in fullname");
            }

            if (userAccountDto.getUsername().isBlank() || userAccountDto.getPassword().isBlank()) {
                return new Response<>(true, ResponseCode.FAIL,"Username & Password must not be empty");
            }

            if (!isValidEmail(userAccountDto.getUsername()))
                return new Response<>(true, ResponseCode.INVALID_REQUEST, null, "Please enter a valid email");


            Optional<UserAccount> firstByUsername = accountRepository.findFirstByUsername(userAccountDto.getUsername());
            if (firstByUsername.isPresent())
                return new Response<>(true, ResponseCode.DUPLICATE_EMAIL, "Duplicate, email already in use");

            UserAccount account = new UserAccount();

            if(userAccountDto.getUserRole() == "" || userAccountDto.getUserRole() == null ){
                account.setUserType(UserType.CUSTOMER.toString());
            }
            else {
                account.setUserType(userAccountDto.getUserRole());
            }


            account.setUsername(userAccountDto.getUsername());
            account.setFullname(userAccountDto.getFullname());
            account.setPassword(passwordEncoder.encode(userAccountDto.getPassword().trim()));
            account.setActive(false);
            account.setUserType(UserType.CUSTOMER.toString());

                account.setLastOtpSentAt(LocalDateTime.now());
                accountRepository.save(account);
                return new Response<>(false, ResponseCode.SUCCESS, account, "Account registered successfully");

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.FAIL, null, "Failed to register account unknown error occurred");
    }

    @Override
    public Response<LoginResponseDto> revokeToken(String refreshToken) {
        try {

            Optional<UserAccount> accountOptional = accountRepository.findFirstByRefreshToken(refreshToken);
            if (accountOptional.isEmpty()){
                return new Response<>(true, ResponseCode.FAIL, null, null, "Invalid refresh token");
            }


            return getLoginResponseResponse(accountOptional, "", "");

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.FAIL, null, null, "Failed to refresh token");
    }

    @Override
    public Response<Boolean> forgetPassword(String email) {
        return null;
    }

    @Override
    public Response<UserAccount> getLoggedUser() {
        try {
            UserAccount user = loggedUser.getUser();
            if (user == null)
                return new Response<>(true, ResponseCode.UNAUTHORIZED, "Unauthorized");
            return new Response<>(false, ResponseCode.SUCCESS, user, "Success");
        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true, ResponseCode.FAIL, "Operation Unsuccessful");
    }

    @Override
    public Response<UserAccount> updatePassword(ChangePasswordDto changePasswordDto) {
        try {
            UserAccount user = loggedUser.getUser();

            if(user ==  null){
                return new Response<>(true,ResponseCode.UNAUTHORIZED, "UNAUTHORIZED");
            }

            if(!passwordEncoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword()))
                return new Response<>(true,ResponseCode.FAIL,"INVALID PASSWORD");

            if(changePasswordDto.getNewPassword().isEmpty() || changePasswordDto.getNewPassword().length() < 6)
                return  new Response<>(true,ResponseCode.FAIL,"Password must atleast  contains 6 characters");

            if(!Objects.equals(changePasswordDto.getNewPassword(), changePasswordDto.getConfirmPassword()))
                return  new Response<>(true,ResponseCode.FAIL,"New password and confirm password do not match");

            user.setPassword(passwordEncoder.encode(changePasswordDto.getNewPassword().trim()));

            UserAccount account =  accountRepository.save(user);

            return new Response<>(false,ResponseCode.SUCCESS,"Password updated successful");

        }catch (Exception e){
            e.printStackTrace();
        }
        return new Response<>(true,ResponseCode.FAIL,"OPERATION FAILED");
    }


    @Override
    public Response<String> requestNewOtp(String phone) {


        return null;
    }






    @NotNull
    private Response<LoginResponseDto> getLoginResponseResponse(Optional<UserAccount> accountOptional, String jwtToken, String refreshToken) {
        if (accountOptional.isPresent()){

            UserAccount account = accountOptional.get();
            account.setRefreshToken(refreshToken);
            account.setRefreshTokenCreatedAt(LocalDateTime.now());
            accountRepository.save(account);
            LoginResponseDto response = new LoginResponseDto(
                    jwtToken,
                    refreshToken,
                    "Bearer",
                    account.getUsername(),
                    account.getUserType(),
                    account.getFullname()
            );

            return new Response<>(false, ResponseCode.SUCCESS, response, null, "Login successful");

        }

        return new Response<>(true, ResponseCode.FAIL, "Failed to login");
    }



    private boolean isValidEmail(String emailStr) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    private boolean isValidPhoneNumber(String number) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("(^(([2]{1}[5]{2})|([0]{1}))[1-9]{2}[0-9]{7}$)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(number);
        return matcher.find();
    }

    private HttpServletRequest getAuthorizationHeader() {
        jakarta.servlet.http.HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        return (HttpServletRequest) request;
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }

}
