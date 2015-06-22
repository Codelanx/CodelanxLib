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

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.function.BiFunction;

/**
 * An enum representing the different types of references, as well as allowing
 * a passed object to be converted to a reference of the relevant type
 *
 * @since 0.2.0
 * @author 1Rogue
 * @version 0.2.0
 */
public enum ReferenceType {

    STRONG(null),
    @SuppressWarnings("rawtypes")
    WEAK((o, queue) -> new WeakReference(o, queue)),
    @SuppressWarnings("rawtypes")
    SOFT((o, queue) -> new SoftReference(o, queue)),
    @SuppressWarnings("rawtypes")
    PHANTOM((o, queue) -> new PhantomReference(o, queue));

    private final BiFunction<? super Object, ? super ReferenceQueue<?>, ? extends Reference<?>> converter;

    private ReferenceType(BiFunction<? super Object, ? super ReferenceQueue<?>, ? extends Reference<?>> converter) {
        this.converter = converter;
    }

    /**
     * Converts the passed-in object to a reference of this constant's type
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param <T> The type of the object
     * @param in The object to hold a reference for
     * @return A {@link FlexReference} to the relevant object
     */
    public <T> FlexReference<? extends T> toSafeReference(T in) {
        return this.toSafeReference(in, null);
    }

    /**
     * Converts the passed-in object to a reference of this constant's type
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param <T> The type of the object
     * @param in The object to hold a reference for
     * @return A {@link Reference} to the relevant object.
     *         <strong>Note however:</strong> Any strong references will be
     *         a wrapped {@link WeakReference}, and caution should be taken to
     *         not pass in the reference to anything which expects a
     *         {@link WeakReference}
     */
    public <T> Reference<? extends T> toUnsafeReference(T in) {
        return this.toUnsafeReference(in, null);
    }

    /**
     * Converts the passed-in object to a reference of this constant's type
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param <T> The type of the object
     * @param in The object to hold a reference for
     * @param queue A {@link ReferenceQueue} to register with the object
     * @return A {@link FlexReference} to the relevant object
     */
    public <T> FlexReference<? extends T> toSafeReference(T in, ReferenceQueue<? extends T> queue) {
        return new FlexReference<>(in, this, queue);
    }

    /**
     * Converts the passed-in object to a reference of this constant's type
     * 
     * @since 0.2.0
     * @version 0.2.0
     * 
     * @param <T> The type of the object
     * @param in The object to hold a reference for
     * @return A {@link Reference} to the relevant object.
     * @param queue A {@link ReferenceQueue} to register with the object
     *         <strong>Note however:</strong> Any strong references will be
     *         a wrapped {@link WeakReference}, and caution should be taken to
     *         not pass in the reference to anything which expects a
     *         {@link WeakReference}
     */
    public <T> Reference<? extends T> toUnsafeReference(T in, ReferenceQueue<? extends T> queue) {
        if (this == STRONG) {
            return new UnsafeStrongReference<>(in, queue);
        }
        return (Reference<T>) this.converter.apply(in, queue);
    }

    <T> FlexReference.ReferenceHolder<? extends T> convert(T in, ReferenceQueue<? extends T> queue) {
        if (this == STRONG) {
            return new FlexReference.ReferenceHolder<>(in);
        }
        return new FlexReference.ReferenceHolder<>(this.toUnsafeReference(in, queue));
    }

    private static class UnsafeStrongReference<T> extends WeakReference<T> {

        private T referent;

        UnsafeStrongReference(T referent) {
            super(null);
            this.referent = referent;
        }

        UnsafeStrongReference(T referent, ReferenceQueue<? extends T> queue) {
            super(null, null);
            this.referent = referent;
        }

        @Override
        public T get() {
            return referent;
        }

        @Override
        public void clear() {
            this.referent = null;
        }

        @Override
        public boolean isEnqueued() {
            return false;
        }

        @Override
        public boolean enqueue() {
            return false;
        }

    }
}
