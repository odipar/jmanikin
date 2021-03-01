package org.jmanikin.core;

import java.util.function.Supplier;

/**
 * The Post-condition stage of building a Message.
 *
 * @param <W> the World Type
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface PostCondition<W extends World<W>, I extends Id<O>, O, E> {
    /**
     * Build the next Msg stage, given a lazy post-condition supplier.
     *
     * @param pst the supplier of the post-condition
     * @return the Msg stage
     */
    Msg<W, I, O, E> pst(Supplier<Boolean> pst);
}
