package com.github.moonlight.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WordMask {

    /**
     * is customized or not,
     * if false, word mask according to maskType
     * else according customization containing prefix length, mask numbner and postfix length
     */
    boolean customized() default false;

    /**
     * mask type
     */
    MaskType maskType() default MaskType.DEFAULT;


    int prefix() default 0;


    int maskNum() default 0;


    int postfix() default 0;

    enum MaskType {
        CHINESENAME, IDNUMBER, PHONENUMBER, BANKNUMBER, DEFAULT
    }


}
