package org.jmanikin.core;

import java.util.function.Supplier;

/**
 * The final Msg stage of building a Message.
 *
 * @param <W> the World Type
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface Msg<W extends World<W>, I extends Id<O>, O, E> {
    /**
     * Returns the lazy pre-condition supplier that was build by the builder.
     *
     * @return the pre-condition supplier
     */
    Supplier<Boolean> pre();
    
    /**
     * Returns the lazy Object O supplier that was build by the builder.
     *
     * @return the Object O supplier
     */
    Supplier<O> app();
    
    /**
     * Returns the lazy Effect E supplier that was build by the builder.
     *
     * @return the Effect E supplier
     */
    Supplier<E> eff();
    
    /**
     * Returns the lazy post-condition supplier that was build by the builder.
     *
     * @return the post-condition supplier
     */
    Supplier<Boolean> pst();
}