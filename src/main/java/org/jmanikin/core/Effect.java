package org.jmanikin.core;

import java.util.function.Supplier;

/**
 * <p>The Effect stage of building a Message.</p>
 *
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface Effect<I extends Id<O>, O, E> {
    /**
     * Build the next post-condition stage, given a lazy effect R supplier.
     *
     * @param eff the supplier of the post-condition R
     * @return the post-condition stage
     */
    PostCondition<I, O, E> eff(Supplier<E> eff);
}
