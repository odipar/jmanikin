package org.jmanikin.core;

/**
 * <p>A Message is a factory for building a Msg using the Builder pattern.</p>
 *
 * <p>Example implementation usage:</p>
 *
 * <pre>{@code
 * public Msg<W, I, O, E> msg(Environment<W, I, O, E> env) { return env.
 *      pre(() -> ...).
 *      app(() -> ...).
 *      eff(() -> ...).
 *      pst(() -> ...);
 * }
 * }</pre>
 *
 * <p>See also {@link org.jmanikin.example.Performance} for an example.</p>
 *
 * @param <W> the World Type
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface Message<W extends World<W>, I extends Id<O>, O, E> {
    /**
     * Builds a Msg, given an environment Environment
     *
     * @param env the environment Environment
     * @return the Msg
     */
    Msg<W, I, O, E> msg(Environment<W, I, O, E> env);
}