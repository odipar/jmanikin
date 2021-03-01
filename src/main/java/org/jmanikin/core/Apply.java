package org.jmanikin.core;

import java.util.function.Supplier;

/**
 * The Apply stage of building a Message.
 *
 * @param <W> the World Type
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface Apply<W extends World<W>, I extends Id<O>, O, E> {
    /**
     * Build the next Effect stage, given a lazy Object O supplier.
     *
     * @param app the supplier of the new Object O
     * @return the Effect stage
     */
    Effect<W, I, O, E> app(Supplier<O> app);
}