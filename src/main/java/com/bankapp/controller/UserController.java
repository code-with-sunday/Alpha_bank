package com.bankapp.controller;

import com.bankapp.dto.request.*;
import com.bankapp.dto.response.BankResponse;
import com.bankapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User Account Management APIs")
public class UserController {

    @Autowired
    UserService userService;

    @Operation(
            summary = "Create New User Account",
            description =  "Creating a new user and assigning an account ID"
    )
    @ApiResponse(
            responseCode = "201",
            description = "Http Status 201 CREATED"
    )
    @PostMapping("/create")
    public ResponseEntity<BankResponse> createAccount(@RequestBody UserRequest userRequest){
       BankResponse response = userService.createAccount(userRequest);
        return new ResponseEntity<>( response, HttpStatus.CREATED);
    }


    @Operation(
            summary = "Balance Enquiry",
            description =  "Check user balance account"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Http Status 200 SUCCESS"
    )
    @GetMapping("balanceEnquiry")
    public BankResponse balanceEnquiry(@RequestBody EnquiryRequest request){
        return userService.balanceEnquiry(request);
    }

    @GetMapping("nameEnquiry")
    public String nameEnquiry(@RequestBody EnquiryRequest request){
        return userService.nameEnquiry(request);
    }

    @PostMapping("credit")
    public BankResponse creditAccount(@RequestBody CreditDebitRequest request){
        return userService.creditAccount(request);
    }

    @PostMapping("debit")
    public BankResponse debitAccount(@RequestBody CreditDebitRequest request){
        return userService.debitAccount(request);
    }

    @PostMapping("transfer")
    public BankResponse transferRequest(@RequestBody TransferRequest request){
        return userService.transferRequest(request);
    }

    @PostMapping("updateUserDetails")
    public BankResponse updateUserDetails(@RequestBody UserRequest userRequest, Long id){
        return  userService.updateUserDetails(userRequest,id);
    }

    @PostMapping("allUser")
    public List<UserRequest> getAllUser(){
        return userService.getAllUser();
    }

    @PostMapping("/deleteUser")
    public BankResponse deleteUserInfo(@RequestBody UserRequest userRequest, String email){
        return userService.deleteUserInfo(userRequest,email);
    }

    @PostMapping("/login")
    public BankResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }
}
