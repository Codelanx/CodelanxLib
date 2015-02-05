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
package com.codelanx.codelanxlib.example;

import com.codelanx.codelanxlib.config.Config;
import com.codelanx.codelanxlib.data.FileDataType;
import com.codelanx.codelanxlib.data.types.Yaml;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Class description for {@link ExampleConfig}
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public enum ExampleConfig implements Config<ExampleConfig> {

    TEST_STRING("test.string", "Hello World!"),
    TEST_DOUBLE("test.double", 3.14),
    TEST_INT("test.int", 42),
    TEST_LIST("test.list", new ArrayList<>()),
    TEST_MAP("test.map", new HashMap<>()); //Potentially bad if someone stores references from default return value
    ;

    private static Yaml yaml;
    private final String path;
    private final Object def;

    private ExampleConfig(String path, Object def) {
        this.path = path;
        this.def = def;
    }

    @Override
    public String getPath() {
        return this.path;
    }

    @Override
    public Object getDefault() {
        return this.def;
    }

    @Override
    public Yaml getConfig() {
        if (ExampleConfig.yaml == null) {
            ExampleConfig.yaml = this.init(Yaml.class);
        }
        return ExampleConfig.yaml;
    }

    /**
     * Facade example, testing {@link Config#retrieve(FileDataType, Config)}
     * return value
     * 
     * @param t A {@link FileDataType} to retrieve this config value from
     * @return A config value that can be used to retrieve values from
     */
    public Config<?> fromOther(FileDataType t) {
        return Config.retrieve(t, this);
    }

}
