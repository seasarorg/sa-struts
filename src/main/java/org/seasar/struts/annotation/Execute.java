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

import org.seasar.struts.enums.SaveType;

/**
 * Actionの実行メソッドを指定するためのアノテーションです。
 * 
 * @author higa
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface Execute {

    /**
     * バリデータを呼び出すかどうかです。
     * 
     */
    boolean validator() default true;

    /**
     * 検証メソッドです。
     * 
     */
    String validate() default "";

    /**
     * 検証エラー時の遷移先です。
     * 
     */
    String input() default "";

    /**
     * エラーメッセージの保存場所です。
     * 
     */
    SaveType saveErrors() default SaveType.REQUEST;

    /**
     * URLパターンです。
     * 
     */
    String urlPattern() default "";

    /**
     * ロールです。複数指定する場合はカンマ(,)で区切ります。
     * 
     */
    String roles() default "";

    /**
     * trueの場合、バリデータや検証メソッドで検証エラーがあるとそこで検証がとまります。
     * falseの場合、検証エラーがあっても後続の検証を続行します。 どちらの場合も検証エラーがあると実行メソッドは呼び出されません。
     * 
     */
    boolean stopOnValidationError() default true;

    /**
     * trueの場合、実行メソッドが正常終了したときにセッションからアクションフォームを削除します。
     * 
     */
    boolean removeActionForm() default false;

    /**
     * リセットメソッド名です。 デフォルトはresetです。
     * 
     */
    String reset() default "reset";

    /**
     * 正常終了時に遷移先にリダイレクトするかどうかです。
     * 
     */
    boolean redirect() default false;
}