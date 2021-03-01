package org.jmanikin.core;

/**
 * <p>An Environment is the self scope that is provided/injected when evaluating a Msg.</p>
 * <p>All lookups and Messages should be dispatched via the Environment's World.</p>
 *
 * @param <W> the World Type
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface Environment<W extends World<W>, I extends Id<O>, O, E> extends PreCondition<W, I, O, E> {
    /**
     * Returns the world that all lookups and Messages are dispatched to
     *
     * @return the World
     */
    W world();
    
    /**
     * Returns the Object identifier that is in scope
     *
     * @return the Object identifier
     */
    I self();
    
    /**
     * Returns the current Object O
     *
     * @return the Object O
     */
    default O obj() {
        return obj(self());
    }
    
    /**
     * Returns the old Object O
     *
     * @return the Object O
     */
    default O old() {
        return old(self());
    }
    
    /**
     * Returns the current Object O2, given its id
     *
     * @param id   the Object identifier
     * @param <O2> the Object type
     * @return the current Object O2
     */
    <O2> O2 obj(Id<? extends O2> id);
    
    /**
     * Returns the old Object O2, given its id
     *
     * @param id   the Object identifier
     * @param <O2> the Object type
     * @return the current Object O2
     */
    <O2> O2 old(Id<? extends O2> id);
    
    /**
     * Sends a Message to Object O, given an Object identifier
     *
     * @param id   the Object identifier to send the Message to
     * @param msg  the message to send to the Object
     * @param <I2> the Id Type
     * @param <O2> the Object Type
     * @param <R2> the Reference Type
     * @return the effect R of the Message send
     */
    <I2 extends Id<O2>, O2, R2> R2 send(I2 id, Message<W, I2, O2, R2> msg);
}
