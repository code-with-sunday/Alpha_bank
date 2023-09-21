package com.bankapp.service.serviceImpl;

import com.bankapp.dto.*;
import com.bankapp.entity.User;
import com.bankapp.repository.UserRepository;
import com.bankapp.service.EmailService;
import com.bankapp.service.UserService;
import com.bankapp.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    EmailService emailService;

    @Autowired
    UserRepository userRepository;
    //Before creation of account, validate if user already has an account
    @Override
    public BankResponse createAccount(UserRequest userRequest) {

        //the process of creating an account is a process of instantiating a new user

        if (userRepository.existsByEmail(userRequest.getEmail())){
            BankResponse response = BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_EXITS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_EXITS_MESSAGE)
                    .accountInfo(null)
                    .build();

            return response;
        }
        User newUser = User.builder()
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .otherName(userRequest.getOtherName())
                .gender(userRequest.getGender())
                .address(userRequest.getAddress())
                .stateOfOrigin(userRequest.getStateOfOrigin())
                .accountNumber(AccountUtils.generateAccountNumber())
                .email(userRequest.getEmail())
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .build();

        User savedUser = userRepository.save(newUser);

        // send email alert
        EmailDetails emailDetails =EmailDetails.builder()
                .recipient(savedUser.getEmail())
                .subject("ACCOUNT CREATION")
                .messageBody("Congratulations, your account have been successfully created. \n your account details  : \n" +
                        savedUser.getFirstName() +" " + savedUser.getLastName() + " " + savedUser.getOtherName() + " " + savedUser.getAccountNumber())
                .build();
        emailService.sendEmailAlert(emailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREATION_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREATION_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(savedUser.getAccountBalance())
                        .accountNumber(savedUser.getAccountNumber())
                        .accountName(savedUser.getFirstName() + " " + savedUser.getFirstName()
                        + " " + savedUser.getLastName()+ " " + savedUser.getOtherName())
                        .build())
                .build();

    }

    @Override
    public BankResponse balanceEnquiry(EnquiryRequest request) {
            //check if the provided accountNumber exist in db
            boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

            if (!isAccountExist){
                return BankResponse.builder()
                        .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                        .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                        .accountInfo(null)
                        .build();
            }

            User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_FOUND_CODE)
                .responseMessage(AccountUtils.ACCOUNT_FOUND_SUCCESS)
                .accountInfo(AccountInfo.builder()
                        .accountBalance(foundUser.getAccountBalance())
                        .accountNumber(request.getAccountNumber())
                        .accountName(foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName())
                        .build())
                .build();


    }

    @Override
    public String nameEnquiry(EnquiryRequest request) {
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());

        if (!isAccountExist){
            return AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE;
        }
        User foundUser = userRepository.findByAccountNumber(request.getAccountNumber());
        return  foundUser.getFirstName() + " " + foundUser.getLastName() + " " + foundUser.getOtherName();
    }

    @Override
    public BankResponse creditAccount(CreditDebitRequest request) {
        //check if the account exit
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToCredit = userRepository.findByAccountNumber(request.getAccountNumber());

        //update the account balance on the mysql table
        userToCredit.setAccountBalance(userToCredit.getAccountBalance().add(request.getAmount()));

        EmailDetails creditEmailDetails = EmailDetails.builder()
                .recipient(userToCredit.getEmail())
                .subject("DEBIT ALERT")
                .messageBody("Your Account has been debited. \n your account details  : \n" +
                        userToCredit.getFirstName() +" " + userToCredit.getLastName() + " " + userToCredit.getOtherName() + " " + userToCredit.getAccountNumber())
                .build();
        emailService.creditEmailAlert(creditEmailDetails);

        //save update in database
        userRepository.save(userToCredit);

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_CREDITED_SUCCESS)
                .responseMessage(AccountUtils.ACCOUNT_CREDITED_SUCCESS_MESSAGE)
                .accountInfo(AccountInfo.builder()
                        .accountName(userToCredit.getFirstName() + " " + userToCredit.getLastName()+ " "+userToCredit.getOtherName())
                        .accountBalance(userToCredit.getAccountBalance())
                        .accountNumber(userToCredit.getAccountNumber())
                        .build())
                .build();
    }

    @Override
    public BankResponse debitAccount(CreditDebitRequest request) {

        //check if the account exists
        //check if the amount to withdraw is not more than the current account balance
        boolean isAccountExist = userRepository.existsByAccountNumber(request.getAccountNumber());
        if (!isAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        User userToDebit = userRepository.findByAccountNumber(request.getAccountNumber());
        //convert BigDecimal to BidInteger
        BigInteger availableBalance = userToDebit.getAccountBalance().toBigInteger();
        BigInteger debitAmount = request.getAmount().toBigInteger();

        if (availableBalance.intValue() < debitAmount.intValue()){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }

        else {
            userToDebit.setAccountBalance(userToDebit.getAccountBalance().subtract(request.getAmount()));
            userRepository.save(userToDebit);

            EmailDetails debitemailDetails = EmailDetails.builder()
                    .recipient(userToDebit.getEmail())
                    .subject("DEBIT ALERT")
                    .messageBody("Your Account has been debited. \n your account details  : \n" +
                            userToDebit.getFirstName() +" " + userToDebit.getLastName() + " " + userToDebit.getOtherName() + " " + userToDebit.getAccountNumber())
                    .build();
            emailService.debitEmailAlert(debitemailDetails);

            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_DEBITED_SUCCESS_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_DEBITED_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(userToDebit.getFirstName() + " " + userToDebit.getLastName()+ " "+userToDebit.getOtherName())
                            .accountBalance(userToDebit.getAccountBalance())
                            .accountNumber(request.getAccountNumber())
                            .build())
                    .build();


        }
    }

    @Override
    public BankResponse transferRequest(TransferRequest request) {
        //get the account to debit(check if it exist)
        //check if the amount I am debiting is not more than the current amount
        //debit the account
        //Get the account to credit
        //Credit the account


        boolean isDestinationAccountExist = userRepository.existsByAccountNumber(request.getDestinationAccountNumber());

        if (!isDestinationAccountExist){
            return BankResponse.builder()
                    .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                    .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                    .accountInfo(null)
                    .build();
        }



        User sourceAccountUser = userRepository.findByAccountNumber(request.getSourceAccountNumber());
        if (request.getAmount().compareTo(sourceAccountUser.getAccountBalance()) > 0){
            return BankResponse.builder()
                    .responseCode(AccountUtils.INSUFFICIENT_BALANCE_CODE)
                    .responseMessage(AccountUtils.INSUFFICIENT_BALANCE_MESSAGE)
                    .accountInfo(null)
                    .build();
        }


        sourceAccountUser.setAccountBalance(sourceAccountUser.getAccountBalance().subtract(request.getAmount()));
        userRepository.save(sourceAccountUser);

        EmailDetails transferRequestFromSenderDebitEmailDetails = EmailDetails.builder()
                .recipient(sourceAccountUser.getEmail())
                .subject("DEBIT ALERT")
                .messageBody("Your Account has been debited. \n your Account Balance  : \n" +
                        sourceAccountUser.getFirstName() +" " + sourceAccountUser.getLastName() + " " + sourceAccountUser.getOtherName() + " " + sourceAccountUser.getAccountNumber())
                .build();
        emailService.debitEmailAlert(transferRequestFromSenderDebitEmailDetails);



        User destinationAccountUser = userRepository.findByAccountNumber(request.getDestinationAccountNumber());

        destinationAccountUser.setAccountBalance(destinationAccountUser.getAccountBalance().add(request.getAmount()));
        userRepository.save(destinationAccountUser);

        EmailDetails transferRequestToRecieverCreditEmailDetails = EmailDetails.builder()
                .recipient(destinationAccountUser.getEmail())
                .subject("DEBIT ALERT")
                .messageBody("Your Account has been credited. \n your account details  : \n" +
                        destinationAccountUser.getFirstName() +" " + destinationAccountUser.getLastName() + " " + destinationAccountUser.getOtherName() + " " + destinationAccountUser.getAccountNumber()
                        +"\n"+ request.getAmount()
                        + "\n" + "from" + sourceAccountUser.getFirstName() + " " + sourceAccountUser.getLastName()+ " " + sourceAccountUser.getOtherName())
                .build();
        emailService.creditEmailAlert(transferRequestToRecieverCreditEmailDetails);

        return BankResponse.builder()
                .responseCode(AccountUtils.TRANSFER_SUCCESSFUL_CODE)
                .responseMessage(AccountUtils.TRANSFER_SUCCESSFUL_MESSAGE)
                .accountInfo(null)
                .build();
    }
}
