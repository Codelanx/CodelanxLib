/*
 * Copyright (C) 2015 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2015 or as published
 * by a later date. You may not provide the source files or provide a means
 * of running the software outside of those licensed to use it.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the Creative Commons BY-NC-ND license
 * long with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.util;

import com.codelanx.codelanxlib.annotation.PluginClass;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class description for {@link ReflectionUtil}
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public final class ReflectionUtil {

    private ReflectionUtil() {
    }

    public static JavaPlugin getPlugin(Class<?> clazz) {
        PluginClass pc = clazz.getAnnotation(PluginClass.class);
        if (pc == null) {
            return null;
        }
        return JavaPlugin.getPlugin(pc.value());
    }

    public static boolean hasAnnotation(AnnotatedElement target, Class<? extends Annotation> check) {
        return target.getAnnotation(check) != null;
    }

    public boolean isLoadedPackage(String... packages) {
        try {
            for (String pkg : packages) {
                Class.forName(pkg);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Returns whether or not the current context was called from a class
     * (instance or otherwise) that is passed to this method. This method can
     * match a regex pattern for multiple classes. Note anonymous classes have
     * an empty name.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param regex The regex to check against the calling class
     * @return {@code true} if accessed from a class that matches the regex
     */
    public static boolean accessedFrom(String regex) {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        if (elems.length < 4) {
            return false;
        }
        return elems[3].getClass().getName().matches(regex);
    }

    /**
     * Returns whether or not the current context was called from a class
     * (instance or otherwise) that is passed to this method. Note anonymous
     * classes have an empty name.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param clazz The class to check
     * @return {@code true} if accessed from this class
     */
    public static boolean accessedFrom(Class clazz) {
        StackTraceElement[] elems = Thread.currentThread().getStackTrace();
        if (elems.length < 4) {
            return false;
        }
        return elems[3].getClass().equals(clazz); 
   }

}
