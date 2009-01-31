/*
 * Copyright 2004-2009 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.struts.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 整数が指定した範囲内かどうかを検証するためのアノテーションです。
 * 
 * @author higa
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
@Validator("intRange")
public @interface IntRange {

    /**
     * 最小値です。
     * 
     */
    int min();

    /**
     * 最大値です。
     * 
     */
    int max();

    /**
     * メッセージです。
     * 
     */
    Msg msg() default @Msg(key = "errors.range");

    /**
     * メッセージの最初の引数です。
     * 
     */
    Arg arg0() default @Arg(key = "");

    /**
     * メッセージの二番目の引数です。
     * 
     */
    Arg arg1() default @Arg(key = "${var:min}", resource = false);

    /**
     * メッセージの三番目の引数です。
     * 
     */
    Arg arg2() default @Arg(key = "${var:max}", resource = false);

    /**
     * 検証の対象となるメソッド名を指定します。 複数ある場合はカンマで区切ります。
     * 
     */
    String target() default "";
}