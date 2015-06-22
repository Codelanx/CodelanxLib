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
package com.codelanx.codelanxlib.util.ref;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;

/**
 * Holds a reference to an object, using either a strong reference, or any
 * of the provided references in Java's provided references.
 *
 * @since 0.2.0
 * @author 1Rogue
 * @version 0.2.0
 * 
 * @param <T> The type of the reference
 */
public class FlexReference<T> {

    /** Holds the object or reference relevant to the object */
    private final ReferenceHolder<? extends T> holder;

    /**
     * Holds the passed in object and creates a reference if necessary
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param object The object to hold a reference to
     * @param type The type of reference to use
     */
    public FlexReference(T object, ReferenceType type) {
        this(object, type, null);
    }

    /**
     * Holds the passed in object and creates a reference if necessary
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param object The object to hold a reference to
     * @param type The type of reference to use
     * @param queue A backed {@link ReferenceQueue} for any constructed ref
     */
    public FlexReference(T object, ReferenceType type, ReferenceQueue<? extends T> queue) {
        this.holder = type.convert(object, queue);
    }

    /**
     * Retrieves the current value of the reference
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @return The current value, or {@code null} if the object has been GC'd
     */
    public T get() {
        return this.holder.get();
    }

    /**
     * Returns whether or not the object is enqueued. If the object is a strong
     * reference, then this method always returns {@code false}
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @see Reference#isEnqueued() 
     * @return {@code true} if the object is not a strong reference and the
     *         reference is enqueued
     */
    public boolean isEnqueued() {
        return this.holder.isEnqueued();
    }

    /**
     * Attempts to insert the reference into the {@link ReferenceQueue}
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @see Reference#enqueue() 
     * @return {@code true} if the object is not a strong reference and the
     *         reference is successfully enqueued for the first time
     */
    public boolean enqueue() {
        return this.holder.enqueue();
    }
    
    static class ReferenceHolder<T> {
        
        private T in;
        private final Reference<? extends T> ref;
        
        protected ReferenceHolder(T in) {
            this.in = in;
            this.ref = null;
        }
        
        protected ReferenceHolder(Reference<? extends T> ref) {
            this.ref = ref;
            this.in = null;
        }
        
        public T get() {
            if (this.in == null) {
                if (this.ref == null) {
                    return null;
                }
                return this.ref.get();
            }
            return this.in;
        }
        
        public void clear() {
            if (this.in == null) {
                if (this.ref != null) {
                    this.ref.clear();
                }
                return;
            }
            this.in = null;
        }
        
        public boolean isEnqueued() {
            if (this.ref != null) {
                return this.ref.isEnqueued();
            }
            return false;
        }
        
        public boolean enqueue() {
            if (this.ref != null) {
                return this.ref.enqueue();
            }
            return false;
        }
    }

}
