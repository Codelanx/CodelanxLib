package com.codelanx.codelanxlib.config;

import com.codelanx.commons.util.exception.Exceptions;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.annotation.Annotation;
import java.util.Map;

/**
 * Created by Rogue on 11/6/2015.
 */
public class Configs {

    /**
     * Returns the relevant {@link JavaPlugin} that is specified by a
     * class-level {@link PluginClass} annotation if it is loaded, otherwise
     * {@code null}
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param clazz The {@link Class} that holds the {@link Annotation}
     * @return The relevant {@link JavaPlugin}, or {@code null} if not found
     */
    public static JavaPlugin getPlugin(Class<?> clazz) {
        PluginClass pc = clazz.getAnnotation(PluginClass.class);
        Exceptions.illegalState(pc != null, "'" + clazz.getName() + "' is missing PluginClass annotation");
        return JavaPlugin.getPlugin(pc.value());
    }

    /**
     * Returns a {@link Map} representative of the passed Object that represents
     * a section of a YAML file. This method neglects the implementation of the
     * section (whether it be {@link ConfigurationSection} or just a
     * {@link Map}), and returns the appropriate value.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param o The object to interpret
     * @return A {@link Map} representing the section
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getConfigSectionValue(Object o) {
        if (o == null) {
            return null;
        }
        Map<String, Object> map;
        if (o instanceof ConfigurationSection) {
            map = ((ConfigurationSection) o).getValues(false);
        } else if (o instanceof Map) {
            map = (Map<String, Object>) o;
        } else {
            return null;
        }
        return map;
    }

}
