package org.jmanikin.core;

/**
 * <p>An Id is a factory for (a pristine) Object O which it subsequently identifies.</p>
 *
 * @param <O> the Object type
 */
public interface Id<O> {
    /**
     * Returns the pristine Object O
     *
     * @return the pristine Object O
     */
    O init();
}