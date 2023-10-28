package com.bankapp.utils;

import java.time.Year;

public class AccountUtils {

    public static String ACCOUNT_EXITS_CODE = "001";
    public static final String ACCOUNT_EXITS_MESSAGE = "This user already has an account created!";

    public static final String  ACCOUNT_CREATION_SUCCESS = "002";
    public static final String ACCOUNT_CREATION_MESSAGE = "Account successfully created!";

    public static final String ACCOUNT_NOT_EXIST_CODE = "003";
    public static final String ACCOUNT_NOT_EXIST_MESSAGE = "User with the provided Account Number does not Exist ";

    public static final String ACCOUNT_FOUND_CODE = "004";
    public static final String ACCOUNT_FOUND_SUCCESS = "User Account Found ";

    public static final String ACCOUNT_CREDITED_SUCCESS = "005";

    public static final String ACCOUNT_CREDITED_SUCCESS_MESSAGE = "User account have been credited";

    public static final String INSUFFICIENT_BALANCE_CODE = "006";

    public static final String INSUFFICIENT_BALANCE_MESSAGE = "Insufficient Balance";

    public static final String ACCOUNT_DEBITED_SUCCESS_CODE = "007";

    public static final String ACCOUNT_DEBITED_SUCCESS_MESSAGE = "Account has been successfully debited";

    public static final String TRANSFER_SUCCESSFUL_CODE = "008";

    public static final String TRANSFER_SUCCESSFUL_MESSAGE = "Transfer successful";

    public static final String UPDATE_USER_DETAIL_SUCCESS_CODE = "204";

    public static final String UPDATE_USER_DETAIL_SUCCESS_MESSAGE = "Update Successful";

    public static final String SIGN_IN_ATTEMPT_SUBJECT = "SIGN IN ATTEMPT";
    public static final String SIGN_IN_SUCCESS_MESSAGE = "You Successfully signed in into your account, If you you are awere of this action, kindly ignore this message";

    public static final String SIGN_IN_UNSUCCESSFUL_MESSAGE =  "Sorry Sign In Attempt Failed";

    public static final String LOGIN_SUCCESSFUL = "Login Successful";


    public static String generateAccountNumber(){
        //account number format = 2023 + randomSixDigits
        //SixDigits are numbers between 100,000 and 999,000
        Year currentYear = Year.now();
        int min = 100000;
        int max = 9999999;

        int randNumber = (int) Math.floor(Math.random() * (max - min + 1) + min);
        // convert current year and randomNumber to string, then concatenate;

        String year = String.valueOf(currentYear);
        String randomNumber = String.valueOf(randNumber);
        StringBuilder accountNumber = new StringBuilder();


        return accountNumber.append(year).append(randomNumber).toString();
    }

}
