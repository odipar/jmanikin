package org.jmanikin.core;

/**
 * <p>A World is a 'memory' for (old) Objects and the entry point for sending Messages to Objects.</p>
 * <p>But specific Worlds can do much more than that. They could:</p>
 *
 * <ul>
 * <li>Log all Messages sent
 * <li>Capture the history of Object states
 * <li>Synchronize state between concurrent Worlds
 * <li>Capture <b><i>runtime</i></b> dependencies between Object states and Messages
 * <li>etc, etc
 * </ul>
 * <p>See {@link org.jmanikin.world.SimpleWorld} for an example implementation.</p>
 *
 * @param <W> The concrete World type
 */
public interface World<W extends World<W>> {
    /**
     * Returns the current Object O value, given an id.
     *
     * @param id  the Object Id
     * @param <O> the Object type
     * @return the current Object O, wrapped in a Value
     */
    <O> Value<W, O> obj(Id<? extends O> id);
    
    /**
     * Returns the old Object O value, given an id.
     *
     * @param id  the Object Id
     * @param <O> the Object type
     * @return the current Object O, wrapped in a Value
     */
    <O> Value<W, O> old(Id<? extends O> id);
    
    /**
     * Returns the effect Value E, after sending a Message to Object O, given an Object identifier.
     *
     * @param id  the Object identifier to send the Message to
     * @param msg the message to send to the Object
     * @param <I> the Id Type
     * @param <O> the Object Type
     * @param <E> the Reference Type
     * @return the effect of the Message send, wrapped in a Value
     */
    <I extends Id<O>, O, E> Value<W, E> send(I id, Message<W, I, O, E> msg);
    
    /**
     * Returns a pristine new World of type W
     *
     * @return a pristine new World
     */
    W init();
}