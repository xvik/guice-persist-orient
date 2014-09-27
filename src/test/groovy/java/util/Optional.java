/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package java.util;


// COPY OF JDK8 class in order to be able to run tests on older jvm.

/**
 * A container object which may or may not contain a non-null value.
 * If a value is present, {@code isPresent()} will return {@code true} and
 * {@code get()} will return the value.
 *
 * <p>Additional methods that depend on the presence or absence of a contained
 * value are provided, such as {@link #orElse(java.lang.Object) orElse()}
 * (return a default value if value not present) and
 * {@link #ifPresent(java.util.function.Consumer) ifPresent()} (execute a block
 * of code if the value is present).
 *
 * <p>This is a <a href="../lang/doc-files/ValueBased.html">value-based</a>
 * class; use of identity-sensitive operations (including reference equality
 * ({@code ==}), identity hash code, or synchronization) on instances of
 * {@code Optional} may have unpredictable results and should be avoided.
 *
 * @since 1.8
 */
public final class Optional<T> {
    /**
     * Common instance for {@code empty()}.
     */
    private static final Optional<?> EMPTY = new Optional<Object>();

    /**
     * If non-null, the value; if null, indicates no value is present
     */
    private final T value;

    /**
     * Constructs an empty instance.
     *
     * @implNote Generally only one empty instance, {@link Optional#EMPTY},
     * should exist per VM.
     */
    private Optional() {
        this.value = null;
    }

    /**
     * Returns an empty {@code Optional} instance.  No value is present for this
     * Optional.
     *
     * @apiNote Though it may be tempting to do so, avoid testing if an object
     * is empty by comparing with {@code ==} against instances returned by
     * {@code Option.empty()}. There is no guarantee that it is a singleton.
     *
     * @param <T> Type of the non-existent value
     * @return an empty {@code Optional}
     */
    public static<T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }

    /**
     * Constructs an instance with the value present.
     *
     * @param value the non-null value to be present
     * @throws NullPointerException if value is null
     */
    private Optional(T value) {
        this.value = Objects.requireNonNull(value);
    }

    /**
     * Returns an {@code Optional} with the specified present non-null value.
     *
     * @param <T> the class of the value
     * @param value the value to be present, which must be non-null
     * @return an {@code Optional} with the value present
     * @throws NullPointerException if value is null
     */
    public static <T> Optional<T> of(T value) {
        return new Optional<T>(value);
    }

    /**
     * Returns an {@code Optional} describing the specified value, if non-null,
     * otherwise returns an empty {@code Optional}.
     *
     * @param <T> the class of the value
     * @param value the possibly-null value to describe
     * @return an {@code Optional} with a present value if the specified value
     * is non-null, otherwise an empty {@code Optional}
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? (Optional<T>)empty() : of(value);
    }

    /**
     * If a value is present in this {@code Optional}, returns the value,
     * otherwise throws {@code NoSuchElementException}.
     *
     * @return the non-null value held by this {@code Optional}
     * @throws NoSuchElementException if there is no value present
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }
}
