package org.jmanikin.world;

import org.jmanikin.core.*;
import java.util.HashMap;
import java.util.function.Supplier;
import org.jmanikin.test.WorldConformanceTest;

/**
 * <p>A SimpleWorld provides a (mutable!) World implementation that still conforms to Manikin's minimum semantics.</p>
 * <p>Because SimpleWorld is not immutable you cannot share it between multiple Threads.</p>
 *
 * <p><b>WARNING: NOT THREAD SAFE</b></p>
 */
public class SimpleWorld implements World<SimpleWorld> {
    private final HashMap<Object, Object> obj = new HashMap<>();
    private final HashMap<Object, Object> old = new HashMap<>();
    
    private static final boolean conformanceChecked = checkConformance();
    
    /**
     * Checks whether this World conforms to Manikin's semantics
     * 
     * @return conformance checked
     */
    private static boolean checkConformance() {
        if (!conformanceChecked) { WorldConformanceTest.check(new SimpleWorld()); }
        return true;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <O> Value<SimpleWorld, O> obj(Id<? extends O> id) {
        return new Value.ValueImpl<>(this, (O) obj.getOrDefault(id, id.init()));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <O> Value<SimpleWorld, O> old(Id<? extends O> id) {
        return new Value.ValueImpl<>(this, (O) old.getOrDefault(id, id.init()));
    }
    
    @Override
    public <I extends Id<O>, O, E> Value<SimpleWorld, E> send(I id, Message<I, O, E> message) {
        SimpleEnv<I, O, E> env = new SimpleEnv<I, O, E>(this, id);
        Msg<I, O, E> msg = message.msg(env);
        O oldObj = obj(id).value();
        
        if (!msg.pre().get()) throw new RuntimeException("Pre-condition failed");
        else {
            try {
                obj.put(id, msg.app().get());
    
                old.put(id, oldObj);
                E eff = msg.eff().get();
                old.put(id, oldObj);   // put it again because of recursive sends to self
    
                if (!msg.pst().get()) throw new RuntimeException("Post-condition failed");
                else return new Value.ValueImpl<>(env.world, eff);
            }
            catch (Exception e) {
                obj.put(id, oldObj);   // Rollback state
                throw e;
            }
        }
    }
    
    @Override public SimpleWorld init() { return new SimpleWorld(); }
    
    private static class SimpleEnv<I extends Id<O>, O, E> implements Environment<I, O, E>,
        PreCondition<I, O, E>, Apply<I, O, E>, Effect<I, O, E>, PostCondition<I, O, E>, Msg<I, O, E> {
        
        private final SimpleWorld world;
        private final I self;
        
        private Supplier<Boolean> _pre;
        private Supplier<O> _app;
        private Supplier<E> _eff;
        private Supplier<Boolean> _pst;
        
        public SimpleEnv(SimpleWorld world, I self) { this.world = world; this.self = self; }
        
        @Override public I self() { return self; }
        @Override public <O2> O2 obj(Id<? extends O2> id) { return world.obj(id).value(); }
        @Override public <O2> O2 old(Id<? extends O2> id) { return world.old(id).value(); }
        @Override public <I2 extends Id<O2>, O2, R2> R2 send(I2 id, Message<I2, O2, R2> msg) {
            return world.send(id, msg).value();
        }
        
        @Override public Apply<I, O, E> pre(Supplier<Boolean> pre) { _pre = pre; return this; }
        @Override public Effect<I, O, E> app(Supplier<O> app) { _app = app; return this; }
        @Override public PostCondition<I, O, E> eff(Supplier<E> eff) { _eff = eff; return this; }
        @Override public Msg<I, O, E> pst(Supplier<Boolean> pst) { _pst = pst; return this; }
        
        @Override public Supplier<Boolean> pre() { return _pre; }
        @Override public Supplier<O> app() { return _app; }
        @Override public Supplier<E> eff() { return _eff; }
        @Override public Supplier<Boolean> pst() { return _pst; }
    }
}
