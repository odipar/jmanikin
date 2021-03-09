package org.jmanikin.core;

import java.util.function.Supplier;

/**
 * <p>The Post-condition stage of building a Message.</p>
 *
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface PostCondition<I extends Id<O>, O, E> {
    /**
     * Build the next Msg stage, given a lazy post-condition supplier.
     *
     * @param pst the supplier of the post-condition
     * @return the Msg stage
     */
    Msg<I, O, E> pst(Supplier<Boolean> pst);
}
