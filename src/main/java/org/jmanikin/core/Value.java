package org.jmanikin.core;

/**
 * A Value is a (world W, value V) pair.
 *
 * @param <W> the World type
 * @param <V> the Value type
 */
public final class Value<W extends World<W>, V> implements World<W> {
    final public W world;
    final public V value;
    
    public Value(W w, V v) {
        world = w;
        value = v;
    }
    
    @Override
    public <O> Value<W, O> obj(Id<? extends O> id) {
        return world.obj(id);
    }
    
    @Override
    public <O> Value<W, O> old(Id<? extends O> id) {
        return world.old(id);
    }
    
    @Override
    public <I extends Id<O>, O, E> Value<W, E> send(I id, Message<W, I, O, E> msg) {
        return world.send(id, msg);
    }
}