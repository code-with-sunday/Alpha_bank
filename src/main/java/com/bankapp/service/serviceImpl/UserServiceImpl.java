package com.bankapp.service.serviceImpl;

import com.bankapp.config.JwtTokenProvider;
import com.bankapp.dto.request.*;
import com.bankapp.dto.response.BankResponse;
import com.bankapp.entity.Role;
import com.bankapp.entity.User;
import com.bankapp.repository.UserRepository;
import com.bankapp.service.EmailService;
import com.bankapp.service.TransactionService;
import com.bankapp.service.UserService;
import com.bankapp.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class UserServiceImpl implements UserService {

    final PasswordEncoder passwordEncoder;
    final TransactionService transactionService;

    final EmailService emailService;

    final UserRepository userRepository;

    final AuthenticationManager authenticationManager;

    final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserServiceImpl(PasswordEncoder passwordEncoder, TransactionService transactionService,
                           EmailService emailService, UserRepository userRepository, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.transactionService = transactionService;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

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
                .password(passwordEncoder.encode(userRequest.getPassword()))
                .accountBalance(BigDecimal.ZERO)
                .phoneNumber(userRequest.getPhoneNumber())
                .alternativePhoneNumber(userRequest.getAlternativePhoneNumber())
                .status("ACTIVE")
                .role(Role.valueOf("ROLE_ADMIN"))
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

        //save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(userToCredit.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

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

            //save transaction
            TransactionDto transactionDto = TransactionDto.builder()
                    .accountNumber(userToDebit.getAccountNumber())
                    .transactionType("DEBIT")
                    .amount(request.getAmount())
                    .build();
            transactionService.saveTransaction(transactionDto);

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
        // the account


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

        //save transaction
        TransactionDto transactionDto = TransactionDto.builder()
                .accountNumber(destinationAccountUser.getAccountNumber())
                .transactionType("CREDIT")
                .amount(request.getAmount())
                .build();
        transactionService.saveTransaction(transactionDto);

        EmailDetails transferRequestToRecieverCreditEmailDetails = EmailDetails.builder()
                .recipient(destinationAccountUser.getEmail())
                .subject("CREDIT ALERT")
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

    @Override
    public BankResponse updateUserDetails(UserRequest userRequest, Long id) {

        Optional<User> checkDetails = userRepository.findById(id);

        if(checkDetails.isPresent()){
            // You need to Extract the User object from Optional
            User user = checkDetails.get();
            user.setEmail(userRequest.getEmail());
            user.setGender(userRequest.getGender());
            user.setAlternativePhoneNumber(userRequest.getAlternativePhoneNumber());
            user.setLastName(userRequest.getLastName());
            user.setFirstName(userRequest.getFirstName());
            user.setOtherName(userRequest.getOtherName());
            user.setStateOfOrigin(userRequest.getStateOfOrigin());
            user.setPhoneNumber(userRequest.getPhoneNumber());
            user.setRole(userRequest.getRole());

            userRepository.save(user);

            EmailDetails emailDetails = EmailDetails.builder()
                    .recipient(user.getEmail())
                    .subject("UPDATE SUCCESSFUL")
                    .messageBody("Your Account has been successfully updated. \n your account details  : \n" +
                            user.getFirstName() +" " + user.getLastName() + " " + user.getOtherName() + " " + user.getAccountNumber()
                    )
                    .build();
            emailService.sendEmailAlert(emailDetails);

            return BankResponse.builder()
                    .responseCode(AccountUtils.UPDATE_USER_DETAIL_SUCCESS_CODE)
                    .responseMessage(AccountUtils.UPDATE_USER_DETAIL_SUCCESS_MESSAGE)
                    .accountInfo(AccountInfo.builder()
                            .accountName(user.getFirstName())
                            .accountName(user.getLastName())
                            .accountNumber(user.getAccountNumber())
                            .build())
                    .build();


        }

        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                .accountInfo(null)
                .build();
    }

    @Override
    public List<UserRequest> getAllUser() {

        List<User> allUsers = userRepository.findAll();

        List<UserRequest> userList = allUsers.stream()
                .map(entireUser -> new UserRequest(entireUser.getId(),entireUser.getFirstName(),entireUser.getLastName(),
                        entireUser.getOtherName(), entireUser.getAddress(), entireUser.getGender(),
                        entireUser.getPassword(),entireUser.getAlternativePhoneNumber(),entireUser.getAccountNumber(),entireUser.getStateOfOrigin(),
                        entireUser.getPhoneNumber(),entireUser.getRole()))
                .collect(Collectors.toList());

        return userList;
    }

    @Override
    public BankResponse deleteUserInfo(UserRequest userRequest, String email) {
        Optional<User> userToDelete = userRepository.findByEmail(email);

        if (userToDelete.isPresent()){
            User user = userToDelete.get();
            userRepository.delete(user);
        }
        return BankResponse.builder()
                .responseCode(AccountUtils.ACCOUNT_NOT_EXIST_CODE)
                .responseMessage(AccountUtils.ACCOUNT_NOT_EXIST_MESSAGE)
                .accountInfo(null)
                .build();
    }

    public BankResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(),loginDto.getPassword()));

        EmailDetails loginAlert = EmailDetails.builder()
                .subject(AccountUtils.SIGN_IN_ATTEMPT_SUBJECT)
                .recipient(loginDto.getEmail())
                .messageBody(AccountUtils.SIGN_IN_SUCCESS_MESSAGE)
                .build();

        emailService.sendEmailAlert(loginAlert);

        return BankResponse.builder()
                .responseCode(AccountUtils.LOGIN_SUCCESSFUL)
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
    }
}
