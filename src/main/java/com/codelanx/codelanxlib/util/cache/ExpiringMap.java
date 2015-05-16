/*
 * Copyright 2014 Jonathan Halterman
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.codelanx.codelanxlib.util.cache;

import com.codelanx.codelanxlib.util.exception.Exceptions;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang.Validate;

/**
 * A thread-safe map that expires entries. Optional features include expiration
 * policies, variable entry settings, and expiration listeners.
 *
 * <p>
 * Entries are tracked by expiration time and expired by a single static
 * {@link Timer}.
 *
 * <p>
 * Expiration listeners will automatically be assigned to run in the context of
 * the Timer thread or in a separate thread based on their first timed duration.
 *
 * <p>
 * When variable expiration is disabled (default), put/remove operations are
 * constant O(n). When variable expiration is enabled, put/remove operations
 * impose an <i>O(log n)</i> cost.
 *
 * <p>
 * Example usages:
 *
 * <pre>
 * {@code
 * Map<String, Integer> map = ExpiringMap.create();
 * Map<String, Integer> map = ExpiringMap.builder().expiration(30, TimeUnit.SECONDS).build();
 * Map<String, Connection> map = ExpiringMap.builder()
 *   .expiration(10, TimeUnit.MINUTES)
 *   .entryLoader(new EntryLoader<String, Connection>() {
 *     public Connection load(String address) {
 *       return new Connection(address);
 *     }
 *   })
 *   .expirationListener(new ExpirationListener<String, Connection>() {
 *     public void expired(String key, Connection connection) {
 *       connection.close();
 *     }
 *   })
 *   .build();
 * }
 * </pre>
 *
 * @author Jonathan Halterman
 * @author 1Rogue Cleanup and updating for Java 8
 * @param <K> Key type
 * @param <V> Value type
 */
public class ExpiringMap<K, V> implements ConcurrentMap<K, V> {

    private static final ScheduledExecutorService expirer = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("ExpiringMap-Expirer"));
    private static final ThreadPoolExecutor listenerService = (ThreadPoolExecutor) Executors.newCachedThreadPool(new NamedThreadFactory("ExpiringMap-Listener-%s"));
    /**
     * Nanoseconds to wait for listener execution
     */
    private static final long LISTENER_EXECUTION_THRESHOLD = TimeUnit.MILLISECONDS.toNanos(100);

    private final AtomicLong expirationNanos;
    private final AtomicReference<ExpirationPolicy> expirationPolicy;
    private final Function<? super K, ? extends V> entryLoader;
    private final List<BiConsumer<? super K, ? super V>> expirationHandlers = new CopyOnWriteArrayList<>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();
    /**
     * Guarded by "readWriteLock"
     */
    private final EntryMap<K, V> entries;
    private final boolean variableExpiration;

    /**
     * Creates a new {@link ExpiringMap} from a {@link Builder} object
     * 
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param builder The {@link Builder} used for constructing this map
     */
    private ExpiringMap(Builder<K, V> builder) {
        this.variableExpiration = builder.variableExpiration;
        this.entries = this.variableExpiration ? new EntryTreeHashMap<>() : new EntryLinkedHashMap<>();
        if (!builder.expirationHandlers.isEmpty()) {
            this.expirationHandlers.addAll(builder.expirationHandlers);
        }
        this.expirationPolicy = new AtomicReference<>(builder.expirationPolicy);
        this.expirationNanos = new AtomicLong(TimeUnit.NANOSECONDS.convert(builder.duration, builder.timeUnit));
        this.entryLoader = builder.entryLoader;
    }

    /**
     * Returns a new {@link Builder} for this {@link ExpiringMap}
     * 
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param <K> The type of the keys
     * @param <V> The type of the values
     * @return New ExpiringMap builder
     */
    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    /**
     * Creates a new instance of ExpiringMap with ExpirationPolicy.CREATED and
     * expiration duration of 60 TimeUnit.SECONDS.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <K> The type of the keys for this map
     * @param <V> The type of the values for this map
     * @return A new {@link ExpiringMap}
     */
    @SuppressWarnings("unchecked")
    public static <K, V> ExpiringMap<K, V> create() {
        return new ExpiringMap<>((Builder<K, V>) ExpiringMap.builder());
    }

    /**
     * Adds a {@link BiConsumer} which will be applied to any entries upon
     * expiry
     * 
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param listener The {@link BiConsumer} to apply
     * @throws IllegalArgumentException If {@code listener} is null
     */
    public void addExpirationHandler(BiConsumer<K, V> listener) {
        Validate.notNull(listener);
        this.expirationHandlers.add(listener);
    }

    @Override
    public void clear() {
        this.writeLock.lock();
        try {
            this.entries.values().forEach(e -> e.cancel(false));
            this.entries.clear();
        } finally {
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean containsKey(Object key) {
        this.readLock.lock();
        try {
            return this.entries.containsKey(key);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public boolean containsValue(Object value) {
        this.readLock.lock();
        try {
            return this.entries.containsValue(value);
        } finally {
            this.readLock.unlock();
        }
    }

    /**
     * Note this operation manually maps internal expiring entries upon each
     * usage of this method. Look into using {@link #keySet()} if possible
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return A {@link Set} of entry objects for keys and values
     */
    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        this.readLock.lock();
        this.writeLock.lock();
        try {
            return this.entries.entrySet().stream().map(ent -> new Map.Entry<K, V>() {

                @Override
                public K getKey() { return ent.getKey(); }

                @Override
                public V getValue() { return ent.getValue().getValue(); }

                @Override
                public V setValue(V value) { return ent.getValue().setValue(value); }

            }).collect(Collectors.toSet());
        } finally {
            this.readLock.unlock();
            this.writeLock.unlock();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        this.readLock.lock();
        try {
            return this.entries.equals(((ExpiringMap<K, V>) obj).entries);
        } finally {
            this.readLock.unlock();
        }
    }

    @Override
    public V get(Object key) {
        ExpiringEntry<K, V> entry = null;

        this.readLock.lock();
        try {
            entry = this.entries.get(key);
        } finally {
            this.readLock.unlock();
        }

        if (entry == null) {
            if (this.entryLoader == null) {
                return null;
            }

            @SuppressWarnings("unchecked")
            K typedKey = (K) key;
            V value = this.entryLoader.apply(typedKey);
            put(typedKey, value);
            return value;
        } else if (entry.expirationPolicy.get() == ExpirationPolicy.ACCESSED) {
            this.resetEntry(entry, false);
        }

        return entry.getValue();
    }

    /**
     * Returns the map's default expiration duration in milliseconds.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The expiration duration (milliseconds)
     */
    public long getExpiration() {
        return TimeUnit.NANOSECONDS.toMillis(expirationNanos.get());
    }

    /**
     * Gets the expiration duration in milliseconds for the entry corresponding
     * to the given key. Returns -1 if there is no mapped key
     *
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param key The key to check the expiration for
     * @return The expiration duration in milliseconds
     */
    public long getExpiration(K key) {
        ExpiringEntry<K, V> entry = null;
        readLock.lock();
        try {
            entry = entries.get(key);
        } finally {
            readLock.unlock();
        }

        if (entry == null) {
            return -1;
        }

        return TimeUnit.NANOSECONDS.toMillis(entry.expirationNanos.get());
    }

    @Override
    public int hashCode() {
        readLock.lock();
        try {
            return entries.hashCode();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public boolean isEmpty() {
        readLock.lock();
        try {
            return entries.isEmpty();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public Set<K> keySet() {
        readLock.lock();
        try {
            return entries.keySet();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Puts {@code value} in the map for {@code key}. Resets the entry's
     * expiration unless an entry already exists for the same {@code key} and
     * {@code value}.
     * 
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param key to put value for
     * @param value to put for key
     * @return the old value
     * @throws IllegalArgumentException on null key
     */
    @Override
    public V put(K key, V value) {
        Validate.notNull(key);
        return this.putInternal(key, value, this.expirationPolicy.get(), this.expirationNanos.get());
    }

    /**
     * Inserts an entry to this map with a specific {@link ExpirationPolicy}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @see #put(Object, Object, ExpirationPolicy, long, TimeUnit)
     * @param key to put value for
     * @param value to put for key
     * @param expirationPolicy The {@link ExpirationPolicy} for this entry
     * @return the old value
     */
    public V put(K key, V value, ExpirationPolicy expirationPolicy) {
        return this.put(key, value, expirationPolicy, this.expirationNanos.get(), TimeUnit.NANOSECONDS);
    }

    /**
     * Puts {@code value} in the map for {@code key}. Resets the entry's
     * expiration unless an entry already exists for the same {@code key} and
     * {@code value}. Requires that variable expiration be enabled.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param key Key to put value for
     * @param value Value to put for key
     * @param expirationPolicy The {@link ExpirationPolicy} for this entry
     * @param duration the length of time after an entry is created that it
     *                 should be removed
     * @param timeUnit the unit that {@code duration} is expressed in
     * @return the old value
     * @throws UnsupportedOperationException If variable expiration is not
     * enabled
     * @throws IllegalArgumentException on null key or timeUnit
     */
    public V put(K key, V value, ExpirationPolicy expirationPolicy, long duration, TimeUnit timeUnit) {
        Exceptions.unsupportedOperation(this.variableExpiration, "Variable expiration is not enabled");
        Validate.notNull(key);
        Validate.notNull(timeUnit);

        return putInternal(key, value, expirationPolicy, TimeUnit.NANOSECONDS.convert(duration, timeUnit));
    }

    /**
     * Inserts an entry with a specific expiration time
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @see #put(Object, Object, ExpirationPolicy, long, TimeUnit)
     * @param key Key to use in mapping to a value
     * @param value Value being mapped to
     * @param duration The duration before this entry expires
     * @param timeUnit The unit of time for this {@code duration}
     * @return The old value, or {@code null} if none existed or it was null
     */
    public V put(K key, V value, long duration, TimeUnit timeUnit) {
        return put(key, value, this.expirationPolicy.get(), duration, timeUnit);
    }

    /**
     * Inserts all values from another map into this one
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @see #put(Object, Object)
     * @param map A {@link Map} to insert into this {@link ExpiringMap}
     */
    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        Validate.notNull(map);

        long expiration = expirationNanos.get();
        ExpirationPolicy expirationPolicy = this.expirationPolicy.get();

        writeLock.lock();
        try {
            for (Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
                putInternal(entry.getKey(), entry.getValue(), expirationPolicy, expiration);
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V putIfAbsent(K key, V value) {
        writeLock.lock();
        try {
            if (!entries.containsKey(key)) {
                return putInternal(key, value, expirationPolicy.get(), expirationNanos.get());
            } else {
                return entries.get(key).getValue();
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V remove(Object key) {
        ExpiringEntry<K, V> entry = null;

        writeLock.lock();
        try {
            entry = entries.remove(key);
        } finally {
            writeLock.unlock();
        }

        if (entry == null) {
            return null;
        }
        if (entry.cancel(false)) {
            scheduleEntry(entries.first());
        }

        return entry.getValue();
    }

    @Override
    public boolean remove(Object key, Object value) {
        writeLock.lock();
        try {
            ExpiringEntry<K, V> entry = entries.get(key);
            if (entry != null && entry.getValue().equals(value)) {
                entries.remove(key);
                if (entry.cancel(false)) {
                    this.scheduleEntry(entries.first());
                }
                return true;
            } else {
                return false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public V replace(K key, V value) {
        writeLock.lock();
        try {
            if (entries.containsKey(key)) {
                return putInternal(key, value, expirationPolicy.get(), expirationNanos.get());
            } else {
                return null;
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
        writeLock.lock();
        try {
            ExpiringEntry<K, V> entry = entries.get(key);
            if (entry != null && entry.getValue().equals(oldValue)) {
                putInternal(key, newValue, expirationPolicy.get(), expirationNanos.get());
                return true;
            } else {
                return false;
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Removes an expiration listener.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param listener The {@link BiConsumer} to remove
     */
    public void removeExpirationHandler(BiConsumer<K, V> listener) {
        Validate.notNull(listener);
        synchronized (this.expirationHandlers) {
            Iterator<BiConsumer<? super K, ? super V>> itr = this.expirationHandlers.iterator();
            while (itr.hasNext()) {
                if (listener.equals(itr.next())) {
                    itr.remove();
                    return;
                }
            }
        }
    }

    /**
     * Resets expiration for the entry corresponding to {@code key}.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param key to reset expiration for
     */
    public void resetExpiration(K key) {
        ExpiringEntry<K, V> entry = null;

        readLock.lock();
        try {
            entry = entries.get(key);
        } finally {
            readLock.unlock();
        }

        if (entry != null) {
            resetEntry(entry, false);
        }
    }

    /**
     * Sets the expiration duration for the entry corresponding to the given
     * key. Supported only if variable expiration is enabled.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param key Key to set expiration for
     * @param duration the length of time after an entry is created that it
     * should be removed
     * @param timeUnit the unit that {@code duration} is expressed in
     * @throws UnsupportedOperationException If variable expiration is not
     * enabled
     */
    public void setExpiration(K key, long duration, TimeUnit timeUnit) {
        if (!variableExpiration) {
            throw new UnsupportedOperationException("Variable expiration is not enabled");
        }

        writeLock.lock();
        try {
            ExpiringEntry<K, V> entry = entries.get(key);
            entry.expirationNanos.set(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
            resetEntry(entry, true);
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Updates the default map entry expiration. Supported only if variable
     * expiration is enabled.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param duration the length of time after an entry is created that it
     * should be removed
     * @param timeUnit the unit that {@code duration} is expressed in
     */
    public void setExpiration(long duration, TimeUnit timeUnit) {
        if (!variableExpiration) {
            throw new UnsupportedOperationException("Variable expiration is not enabled");
        }

        expirationNanos.set(TimeUnit.NANOSECONDS.convert(duration, timeUnit));
    }

    /**
     * Sets the global expiration policy for the map.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param expirationPolicy The new {@link ExpirationPolicy} for all entries
     */
    public void setExpirationPolicy(ExpirationPolicy expirationPolicy) {
        this.expirationPolicy.set(expirationPolicy);
    }

    /**
     * Sets the expiration policy for the entry corresponding to the given key.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param key to set policy for
     * @param expirationPolicy to set
     * @throws UnsupportedOperationException If variable expiration is not
     * enabled
     */
    public void setExpirationPolicy(K key, ExpirationPolicy expirationPolicy) {
        Exceptions.unsupportedOperation(this.variableExpiration, "Variable expiration is not enabled");

        ExpiringEntry<K, V> entry = null;
        readLock.lock();
        try {
            entry = entries.get(key);
        } finally {
            readLock.unlock();
        }

        if (entry != null) {
            entry.expirationPolicy.set(expirationPolicy);
        }
    }

    @Override
    public int size() {
        readLock.lock();
        try {
            return entries.size();
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public String toString() {
        readLock.lock();
        try {
            return entries.toString();
        } finally {
            readLock.unlock();
        }
    }

    /**
     * Note this method maps internal entries into values upon each specific
     * call. Look into using {@link #valuesIterator()} if possible
     *
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return a {@link Collection} of all values in this map
     */
    @Override
    public Collection<V> values() {
        return this.entries.values().stream().map(e -> e.getValue()).collect(Collectors.toList());
    }

    /**
     * Returns an iterator over the map values.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return a {@link Iterator} of values for this map
     * @throws ConcurrentModificationException if the map's size changes while
     * iterating, excluding calls to {@link Iterator#remove()}.
     */
    public Iterator<V> valuesIterator() {
        return new Iterator<V>() {
            private final Iterator<ExpiringEntry<K, V>> iterator = entries.valuesIterator();

            /**
             * {@inheritDoc}
             */
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public V next() {
                return iterator.next().getValue();
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }

    /**
     * Notifies expiration listeners that the given entry expired. Utilizes an
     * expiration policy to invoke the listener. If the listener's initial
     * execution exceeds LISTENER_EXECUTION_THRESHOLD then the listener will be
     * invoked within the context of {@code listenerService}, else it will be
     * invoked within the context of {@code timer}. Must not be called from
     * within a locked context.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param entry Entry to expire
     */
    void notifyHandlers(final ExpiringEntry<K, V> entry) {
        if (this.expirationHandlers.isEmpty()) {
            return;
        }
        this.expirationHandlers.forEach(e -> {
            try {
                e.accept(entry.getKey(), entry.getValue());
            } catch (Throwable T) {
                
            }
        });
    }

    /**
     * Puts the given key/value in storage, scheduling the new entry for
     * expiration if needed. If a previous value existed for the given key, it
     * is first cancelled and the entries reordered to reflect the new
     * expiration.
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    V putInternal(K key, V value, ExpirationPolicy expirationPolicy, long expirationNanos) {
        writeLock.lock();
        try {
            ExpiringEntry<K, V> entry = entries.get(key);
            V oldValue = null;

            if (entry == null) {
                entry = new ExpiringEntry<K, V>(key, value, variableExpiration ? new AtomicReference<ExpirationPolicy>(
                        expirationPolicy) : this.expirationPolicy, variableExpiration ? new AtomicLong(expirationNanos)
                                : this.expirationNanos);
                entries.put(key, entry);
                if (entries.size() == 1 || entries.first().equals(entry)) {
                    scheduleEntry(entry);
                }
            } else {
                oldValue = entry.getValue();
                if ((oldValue == null && value == null) || (oldValue != null && oldValue.equals(value))) {
                    return value;
                }

                entry.setValue(value);
                resetEntry(entry, false);
            }

            return oldValue;
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Resets the given entry's schedule canceling any existing scheduled
     * expiration and reordering the entry in the internal map. Schedules the
     * next entry in the map if the given {@code entry} was scheduled or if
     * {@code scheduleNext} is true.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param entry to reset
     * @param scheduleFirstEntry whether the first entry should be automatically
     * scheduled
     */
    void resetEntry(ExpiringEntry<K, V> entry, boolean scheduleFirstEntry) {
        writeLock.lock();
        try {
            boolean scheduled = entry.cancel(true);
            entries.reorder(entry);

            if (scheduled || scheduleFirstEntry) {
                scheduleEntry(entries.first());
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Schedules an entry for expiration. Guards against concurrent
     * schedule/schedule, cancel/schedule and schedule/cancel calls.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param entry Entry to schedule
     */
    void scheduleEntry(ExpiringEntry<K, V> entry) {
        if (entry == null || entry.scheduled) {
            return;
        }

        Runnable runnable = null;
        synchronized (entry) {
            if (entry.scheduled) {
                return;
            }

            final WeakReference<ExpiringEntry<K, V>> entryReference = new WeakReference<ExpiringEntry<K, V>>(entry);
            runnable = new Runnable() {
                @Override
                public void run() {
                    ExpiringEntry<K, V> entry = entryReference.get();

                    writeLock.lock();
                    try {
                        if (entry != null && entry.scheduled) {
                            entries.remove(entry.key);
                            notifyHandlers(entry);
                        }

                        try {
                            // Expires entries and schedules the next entry
                            Iterator<ExpiringEntry<K, V>> iterator = entries.valuesIterator();
                            boolean schedulePending = true;

                            while (iterator.hasNext() && schedulePending) {
                                ExpiringEntry<K, V> nextEntry = iterator.next();
                                if (nextEntry.expectedExpiration.get() <= System.nanoTime()) {
                                    iterator.remove();
                                    notifyHandlers(nextEntry);
                                } else {
                                    scheduleEntry(nextEntry);
                                    schedulePending = false;
                                }
                            }
                        } catch (NoSuchElementException ignored) {
                        }
                    } finally {
                        writeLock.unlock();
                    }
                }
            };

            Future<?> entryFuture = expirer.schedule(runnable, entry.expectedExpiration.get() - System.nanoTime(),
                    TimeUnit.NANOSECONDS);
            entry.schedule(entryFuture);
        }
    }
    
    private static class NamedThreadFactory implements ThreadFactory {

        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String nameFormat;

        /**
         * Creates a thread factory that names threads according to the
         * {@code nameFormat} by supplying a single argument to the format
         * representing the thread number.
         *
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param nameFormat The format for this {@link ThreadFactory}
         */
        public NamedThreadFactory(String nameFormat) {
            this.nameFormat = nameFormat;
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, String.format(nameFormat, threadNumber.getAndIncrement()));
        }
    }

    /**
     * Builder object for an {@link ExpiringMap}. Defaults to using
     * {@link ExpirationPolicy#ACCESSED} and an expiration time of 5
     * {@link TimeUnit#MINUTES}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <K> The key type
     * @param <V> The value type
     */
    public static final class Builder<K, V> {

        private ExpirationPolicy expirationPolicy = ExpirationPolicy.ACCESSED;
        private final List<BiConsumer<K, V>> expirationHandlers = new ArrayList<>();
        private Function<K, V> entryLoader;
        private long duration = 5;
        private TimeUnit timeUnit = TimeUnit.MINUTES;
        private boolean variableExpiration;

        /**
         * Creates a new Builder object.
         */
        private Builder() {
        }

        /**
         * Builds and returns an expiring map.
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param <K1> The key type
         * @param <V1> The value type
         * @return a new {@link ExpiringMap} object
         */
        @SuppressWarnings("unchecked")
        public <K1 extends K, V1 extends V> ExpiringMap<K1, V1> build() {
          return new ExpiringMap<>((Builder<K1, V1>) this);
        }

        /**
         * Sets the default map entry expiration.
         * 
         * @since 0.1.0
         * @version 0.1.0
         *
         * @param duration the length of time after an entry is created that it
         * should be removed
         * @param timeUnit the unit that {@code duration} is expressed in
         * @return This builder object (chained)
         */
        public Builder<K, V> expiration(long duration, TimeUnit timeUnit) {
            this.duration = duration;
            this.timeUnit = timeUnit;
            return this;
        }

        /**
         * Sets the {@link Function} that is called when an entry is expired
         * and should be re-initialized
         * 
         * @since 0.1.0
         * @version 0.1.0
         *
         * @param <K1> The type of the keys to deal with
         * @param <V1> The type of the values to deal with
         * @param loader to set
         * @return This builder object (chained)
         */
        @SuppressWarnings("unchecked")
        public <K1 extends K, V1 extends V> Builder<K1, V1> entryLoader(Function<? super K1, ? super V1> loader) {
            this.entryLoader = (Function<K, V>) loader;
            return (Builder<K1, V1>) this;
        }

        /**
         * Sets the {@link BiConsumer} to apply to expired entries
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @param <K1> The type of the keys to deal with
         * @param <V1> The type of the values to deal with
         * @param expired A {@link BiConsumer} that is applied to entries upon
         *                expiration
         * @return This builder object (chained)
         */
        public <K1 extends K, V1 extends V> Builder<K1, V1> onExpiry(BiConsumer<? super K1, ? super V1> expired) { //PECS
            this.expirationHandlers.add((BiConsumer<K, V>) expired);
            return (Builder<K1, V1>) this;
        }

        /**
         * Sets the map entry expiration policy.
         * 
         * @since 0.1.0
         * @version 0.1.0
         *
         * @param expirationPolicy The {@link ExpirationPolicy} to set
         * @return This builder object (chained)
         */
        public Builder<K, V> expirationPolicy(ExpirationPolicy expirationPolicy) {
            this.expirationPolicy = expirationPolicy;
            return this;
        }

        /**
         * Allows for map entries to have individual expirations and for
         * expirations to be changed.
         * 
         * @since 0.1.0
         * @version 0.1.0
         * 
         * @return This builder object (chained)
         */
        public Builder<K, V> variableExpiration() {
            this.variableExpiration = true;
            return this;
        }

    }

    /**
     * Map entry expiration policy.
     */
    public enum ExpirationPolicy {

        /**
         * Expires entries based on when they were last accessed
         */
        ACCESSED,
        /**
         * Expires entries based on when they were created
         */
        CREATED;
    }

    /**
     * Entry map definition.
     *
     * @since 0.1.0
     * @version 0.1.0
     */
    interface EntryMap<K, V> extends Map<K, ExpiringEntry<K, V>> {

        /**
         * Returns the first entry in the map or null if the map is empty.
         */
        ExpiringEntry<K, V> first();

        /**
         * Reorders the given entry in the map.
         *
         * @param entry to reorder
         */
        void reorder(ExpiringEntry<K, V> entry);

        /**
         * Returns a values iterator.
         */
        Iterator<ExpiringEntry<K, V>> valuesIterator();

    }

    /**
     * Entry LinkedHashMap implementation.
     */
    static class EntryLinkedHashMap<K, V> extends LinkedHashMap<K, ExpiringEntry<K, V>> implements EntryMap<K, V> {

        private static final long serialVersionUID = 1L;

        @Override
        public ExpiringEntry<K, V> first() {
            return isEmpty() ? null : values().iterator().next();
        }

        @Override
        public void reorder(ExpiringEntry<K, V> value) {
            remove(value.key);
            put(value.key, value);
        }

        @Override
        public Iterator<ExpiringEntry<K, V>> valuesIterator() {
            return values().iterator();
        }
    }

    /**
     * Entry TreeHashMap implementation.
     */
    static class EntryTreeHashMap<K, V> extends HashMap<K, ExpiringEntry<K, V>> implements EntryMap<K, V> {

        private static final long serialVersionUID = 1L;
        SortedSet<ExpiringEntry<K, V>> sortedSet = new TreeSet<>();

        @Override
        public void clear() {
            super.clear();
            sortedSet.clear();
        }

        @Override
        public ExpiringEntry<K, V> first() {
            return sortedSet.isEmpty() ? null : sortedSet.first();
        }

        @Override
        public ExpiringEntry<K, V> put(K key, ExpiringEntry<K, V> value) {
            sortedSet.add(value);
            return super.put(key, value);
        }

        @Override
        public ExpiringEntry<K, V> remove(Object key) {
            ExpiringEntry<K, V> entry = super.remove(key);
            if (entry != null) {
                sortedSet.remove(entry);
            }
            return entry;
        }

        @Override
        public void reorder(ExpiringEntry<K, V> value) {
            sortedSet.remove(value);
            sortedSet.add(value);
        }

        @Override
        public Iterator<ExpiringEntry<K, V>> valuesIterator() {
            return new Iterator<ExpiringEntry<K, V>>() {
                private final Iterator<ExpiringEntry<K, V>> iterator = sortedSet.iterator();
                private ExpiringEntry<K, V> next;

                @Override
                public boolean hasNext() {
                    return iterator.hasNext();
                }

                @Override
                public ExpiringEntry<K, V> next() {
                    next = iterator.next();
                    return next;
                }

                @Override
                public void remove() {
                    EntryTreeHashMap.super.remove(next.key);
                    iterator.remove();
                }
            };
        }
    }


    /**
     * Expiring map entry implementation.
     */
    static class ExpiringEntry<K, V> implements Comparable<ExpiringEntry<K, V>> {

        final AtomicLong expirationNanos;
        /**
         * Epoch time at which the entry is expected to expire
         */
        final AtomicLong expectedExpiration;
        final AtomicReference<ExpirationPolicy> expirationPolicy;
        final K key;
        /**
         * Guarded by "this"
         */
        volatile Future<?> entryFuture;
        /**
         * Guarded by "this"
         */
        V value;
        /**
         * Guarded by "this"
         */
        volatile boolean scheduled;

        /**
         * Creates a new ExpiringEntry object.
         *
         * @param key for the entry
         * @param value for the entry
         * @param expirationPolicy for the entry
         * @param expirationNanos for the entry
         */
        ExpiringEntry(K key, V value, AtomicReference<ExpirationPolicy> expirationPolicy, AtomicLong expirationNanos) {
            this.key = key;
            this.value = value;
            this.expirationPolicy = expirationPolicy;
            this.expirationNanos = expirationNanos;
            this.expectedExpiration = new AtomicLong();
            resetExpiration();
        }

        @Override
        public int compareTo(ExpiringEntry<K, V> other) {
            if (key.equals(other.key)) {
                return 0;
            }
            return expectedExpiration.get() < other.expectedExpiration.get() ? -1 : 1;
        }

        @Override
        public boolean equals(Object pOther) {
            return key.equals(((ExpiringEntry<?, ?>) pOther).key);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @Override
        public String toString() {
            return value.toString();
        }

        public K getKey() {
            return this.key;
        }

        /**
         * Marks the entry as canceled and resets the expiration if
         * {@code resetExpiration} is true.
         *
         * @param resetExpiration whether the entry's expiration should be reset
         * @return true if the entry was scheduled
         */
        synchronized boolean cancel(boolean resetExpiration) {
            boolean result = scheduled;
            if (entryFuture != null) {
                entryFuture.cancel(false);
            }

            entryFuture = null;
            scheduled = false;

            if (resetExpiration) {
                resetExpiration();
            }
            return result;
        }

        /**
         * Gets the entry value.
         */
        synchronized V getValue() {
            return value;
        }

        /**
         * Resets the entry's expected expiration.
         */
        void resetExpiration() {
            expectedExpiration.set(expirationNanos.get() + System.nanoTime());
        }

        /**
         * Marks the entry as scheduled.
         */
        synchronized void schedule(Future<?> entryFuture) {
            this.entryFuture = entryFuture;
            scheduled = true;
        }

        /**
         * Sets the entry value.
         */
        synchronized V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
