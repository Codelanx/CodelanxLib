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

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * A class of random number generators that can be used instead of instantiating
 * new {@link Random} classes all about the program
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public final class RNG {

    static {
        Scheduler.runAsyncTaskRepeat(() -> {
            //nobody fuckin' with my secure random
            RNG.SECURE_RAND.setSeed(RNG.SECURE_RAND.generateSeed(RNG.THREAD_LOCAL().nextInt(30)));
        }, 0, TimeUnit.MINUTES.toSeconds(10));
    }

    private RNG() {}

    /**
     * A simple {@link Random} instance, should only be used on Bukkit's main
     * thread
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public static final Random RAND = new Random();

    /**
     * Represents a {@link SecureRandom} with a new, randomly generated seed of
     * a pseudo-random bitlength every 10 minutes.
     * 
     * @since 0.1.0
     * @version 0.1.0
     */
    public static final SecureRandom SECURE_RAND = new SecureRandom();

    /**
     * Returns the {@link ThreadLocalRandom} specific to the calling thread
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The {@link ThreadLocalRandom} for the current thread context
     */
    public static final ThreadLocalRandom THREAD_LOCAL() {
        return ThreadLocalRandom.current();
    }


}
