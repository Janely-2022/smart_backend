package com.zedeck.smartoutletserver.controller;

import com.zedeck.smartoutletserver.dto.ChangePasswordDto;
import com.zedeck.smartoutletserver.dto.LoginDto;
import com.zedeck.smartoutletserver.dto.LoginResponseDto;
import com.zedeck.smartoutletserver.dto.UserAccountDto;
import com.zedeck.smartoutletserver.model.UserAccount;
import com.zedeck.smartoutletserver.service.AuthService;
import com.zedeck.smartoutletserver.utils.Response;
import jakarta.websocket.server.PathParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    private Logger logger = LoggerFactory.getLogger(AuthController.class);


    @PostMapping(path = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> login(@RequestBody LoginDto loginDto){
        Response<LoginResponseDto> response = authService.login(loginDto);
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(path = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register (@RequestBody UserAccountDto userAccountDto) {
        Response stringResponse = authService.registerUser(userAccountDto);
        return ResponseEntity.ok().body(stringResponse);
    }

    @GetMapping(path = "/me")
    public ResponseEntity<?> getProfile(){
        Response<UserAccount> response =  authService.getLoggedUser();
        return ResponseEntity.ok().body(response);
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordDto changePasswordDto){
        Response<UserAccount> response =  authService.updatePassword(changePasswordDto);
        return ResponseEntity.ok().body(response);
    }


//    @GetMapping(path = "/activate-account")
//    public ResponseEntity<?> activateAccount(@RequestParam(value = "code")String code) {
//        Response<String> stringResponse = authService.activateAccount(code);
//        return ResponseEntity.ok()
//                .body(stringResponse);
//    }

//
    @GetMapping(path = "/code/request-new")
    public ResponseEntity<?> requestNew(@PathParam(value = "phone")String phone){
        Response<String> stringResponse = authService.requestNewOtp(phone);
        return ResponseEntity.ok()
                .body(stringResponse);
    }

//    @PostMapping("/send-message")
//    public  ResponseEntity<?> sendMessage(@RequestBody MessageRequestDto requestDto){
//
//        Response<ResponseData> response =  bulkSmsIntegration.sendMessage(requestDto);
//
//        return ResponseEntity.ok().body(response);
//    }


}

