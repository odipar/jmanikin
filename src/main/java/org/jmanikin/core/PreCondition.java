package org.jmanikin.core;

import java.util.function.Supplier;

/**
 * <p>The Pre-condition stage of building a Message.</p>
 *
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface PreCondition<I extends Id<O>, O, E> {
    /**
     * Build the next Apply stage, given a lazy pre-condition supplier.
     *
     * @param pre the supplier of the pre-condition
     * @return the Apply stage
     */
    Apply<I, O, E> pre(Supplier<Boolean> pre);
}
