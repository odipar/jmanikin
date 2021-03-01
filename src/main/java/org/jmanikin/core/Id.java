package org.jmanikin.core;

/**
 * An Id identifies Object O.
 *
 * @param <O> the Object type
 */
public interface Id<O> {
    O init();
}