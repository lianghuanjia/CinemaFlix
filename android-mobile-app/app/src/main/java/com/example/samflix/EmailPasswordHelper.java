package com.example.samflix;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailPasswordHelper {

    //case -11
    public static boolean invalidEmailFormat(String email){
        String validEmailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        if(email == null){
            return true;
        }
        Pattern pattern = Pattern.compile(validEmailRegex);
        Matcher matcher = pattern.matcher(email);
        if(!matcher.matches()){
            return true;
        }
        else{
            return false;
        }
    }

    //case -10
    public static boolean invalidEmailLen(String email){
        char[] emailChar = email.toCharArray();
        System.out.println(emailChar.length);
        if(emailChar.length > 50){
            return true;
        }else{
            return false;
        }
    }

    //case 12
    public static boolean invalidPasswordLenReq(char[] password){
        if(password.length < 7 || password.length > 16){
            return true;
        }return false;
    }

    //case 13
    public static boolean passwordCharReqMatches(char[] password){
        String Symbol = "!\"#$%&'()*+,-./:;<=>?@[\\]^_´`{|}~";
        boolean lowerCase = false;
        boolean digit = false;
        boolean upperCase = false;
        boolean specialSymbol = false;

        for(char character: password){
            if(Character.isDigit(character)==true){
                digit = true;
            }else if(Character.isUpperCase(character) == true){
                upperCase = true;
            }else if(Character.isLowerCase(character)==true){
                lowerCase = true;
            }else if(Symbol.indexOf(character) >= 0){
                specialSymbol = true;
            }
        }
        if(lowerCase == true && upperCase == true && digit == true && specialSymbol==true){
            return true;
        }return false;
    }

    //we need restful call for case 16: email already in use
    //Or can we access our database on openlab to sql the email directly??



}
