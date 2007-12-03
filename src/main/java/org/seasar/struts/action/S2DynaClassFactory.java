/*
 * Copyright 2004-2007 the Seasar Foundation and the Others.
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
package org.seasar.struts.action;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.DynaClass;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.impl.BeanDescImpl;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

/**
 * 動的クラスを生成するクラスです。
 * 
 * @author higa
 * 
 */
public final class S2DynaClassFactory {

    private static volatile boolean initialized;

    private static Map<String, S2DynaClass> dynaClassCache = new ConcurrentHashMap<String, S2DynaClass>(
            256);

    static {
        initialize();
    }

    private S2DynaClassFactory() {
    }

    /**
     * {@link BeanDesc}を返します。
     * 
     * @param clazz
     * @return {@link BeanDesc}
     */
    public static S2DynaClass getBynaClass(Class clazz) {
        if (!initialized) {
            initialize();
        }
        DynaClass dynaClass = dynaClassCache.get(clazz.getName());
        if (beanDesc == null) {
            beanDesc = new BeanDescImpl(clazz);
            beanDescCache.put(clazz, beanDesc);
        }
        return beanDesc;
    }

    /**
     * 初期化を行ないます。
     */
    public static void initialize() {
        DisposableUtil.add(new Disposable() {
            public void dispose() {
                clear();
            }
        });
        initialized = true;
    }

    /**
     * キャッシュをクリアします。
     */
    public static void clear() {
        beanDescCache.clear();
        initialized = false;
    }
}
