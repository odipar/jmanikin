package org.jmanikin.core;

import java.util.function.Supplier;

/**
 * The Pre-condition stage of building a Message.
 *
 * @param <W> the World Type
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface PreCondition<W extends World<W>, I extends Id<O>, O, E> {
    /**
     * Build the next Apply stage, given a lazy pre-condition supplier.
     *
     * @param pre the supplier of the pre-condition
     * @return the Apply stage
     */
    Apply<W, I, O, E> pre(Supplier<Boolean> pre);
}
