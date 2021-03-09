package org.jmanikin.core;

/**
 * <p>A Value correlates a value V with a World W that produced value V.</p>
 *
 * @param <W> the World type
 * @param <V> the Value type
 */
public interface Value<W extends World<W>, V> extends World<W> {
    
    W world();
    V value();
    
    /**
     * <p>Default implementation.</p>
     *
     * @param <W> the World type
     * @param <V> the Value type
     */
    final class ValueImpl<W extends World<W>, V> implements Value<W, V> {
        final private W world;
        final private V value;
        
        public ValueImpl(W w, V v) { world = w; value = v; }
    
        @Override public W world() { return world; }
        @Override public V value() { return value; }
        @Override public W init() { return world.init(); }
        @Override public <O> Value<W, O> obj(Id<? extends O> id) { return world.obj(id); }
        @Override public <O> Value<W, O> old(Id<? extends O> id) { return world.old(id); }
        @Override public <I extends Id<O>, O, E> Value<W, E> send(I id, Message<I, O, E> msg) {
            return world.send(id, msg);
        }
    }
}
