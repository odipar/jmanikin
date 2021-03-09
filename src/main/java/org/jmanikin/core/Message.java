package org.jmanikin.core;

/**
 * <p>A Message is a factory for building a Msg using a fluent Builder.</p>
 *
 * <p>Example implementation usage:</p>
 *
 * <pre>{@code
 * public Msg<I, O, E> msg(Environment<I, O, E> env) { return env.
 *      pre(() -> ...).
 *      app(() -> ...).
 *      eff(() -> ...).
 *      pst(() -> ...);
 * }
 * }</pre>
 *
 * <p>See also {@link org.jmanikin.example.Performance} for an example.</p>
 *
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface Message<I extends Id<O>, O, E> {
    /**
     * Builds a Msg, given an Environment
     *
     * @param env the environment
     * @return the Msg
     */
    Msg<I, O, E> msg(Environment<I, O, E> env);
}