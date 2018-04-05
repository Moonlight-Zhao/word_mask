package com.github.moonlight;

import com.github.moonlight.annotation.WordMask;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;


/**
 * word mask converter
 *
 * @author zhaozheng<zhaozheng90@ 163.com>
 * @Date 04/05/2018
 **/
@Slf4j
public class WorkMaskConverter {


    private static final String masker = "*";


    private WorkMaskConverter() {
    }

    public static <T extends Serializable> T maskConvert(T origin) {
        T clone = SerializationUtils.clone(origin);
        for (Field field : clone.getClass().getFields()) {
            WordMask annotation = field.getAnnotation(WordMask.class);
            if (annotation == null) {
                continue;
            }
            convertField(clone, field, annotation);
        }
        return clone;
    }


    private static <T extends Serializable> void convertField(T copy, Field field, WordMask wordMask) {
        makeAccessible(field);
        String input = null;
        String output=null;
        try {
            input = (String) field.get(copy);

            if (input == null) {
                return;
            }

            if (wordMask.customized()) {
                output= maskCustomization(input, wordMask);
                return;
            }
            switch (wordMask.maskType()) {
                case PHONENUMBER:
                    output=   maskPhoneNumber(input);
                    break;
                case IDNUMBER:
                    output = maskIdNumber(input);
                    break;
                case BANKNUMBER:
                    output=maskBankNumber(input);
                    break;
                case CHINESENAME:
                    output = maskChineseNumber(input);
                case DEFAULT:
                    output = maskDefault(input);
            }
            field.set(copy,output);
        } catch (IllegalAccessException e) {
            //nothing need to do;
        }
    }

    private static String maskDefault(String input) {
        return input.replaceAll("^(.)*(.)$", "$1******$2");
    }

    private static String maskChineseNumber(String input) {
        return input.replaceAll("^([\\u4e00-\\u9fa5])*([\\u4e00-\\u9fa5])$", "$1*$2");
    }


    private static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers())) {
            field.setAccessible(true);
        }
    }


    private static String maskPhoneNumber(String origin) {
        return origin.replaceAll("^(\\d{3})\\d{4}(\\d{4})$", "$1****$2");
    }

    private static String maskIdNumber(String origin) {
        return origin.replaceAll("^(\\d{4})\\d*(\\d{4}$", "$1******$2");
    }


    private static String maskBankNumber(String origin) {
        return origin.replaceAll("^(\\d{4})\\d*(\\d{4})$", "$1******$2");
    }


    private static String maskCustomization(String origin, WordMask wordMask) {
        int prefix = wordMask.prefix();
        int postfix = wordMask.postfix();
        if (prefix + postfix > origin.length()) {
            throw new IllegalArgumentException("the customized length of prefix and postfix is greater than input string length");
        }
        return origin.substring(0, prefix) + StringUtils.repeat(masker, wordMask.maskNum())
                + origin.substring(origin.length() - postfix, origin.length());
    }


    //todo test
    public static void main(String[] args) {

    }

}