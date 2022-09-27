/*
 * Copyright (c) 2020 WildFireChat. All rights reserved.
 */

package com.tmmtmm.sdk.message.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ContentTag {
    int type() default 0;

//    TmPersistFlag flag() default TmPersistFlag.No_Persist;
}
