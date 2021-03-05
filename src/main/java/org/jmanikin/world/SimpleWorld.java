package org.jmanikin.world;

import org.jmanikin.core.*;
import org.jmanikin.test.WorldConformanceTest;

import java.util.HashMap;

/**
 * <p>A SimpleWorld provides the minimum (mutable!) World implementation that still adheres to Manikin's semantics.</p>
 * <p>Because SimpleWorld is not immutable you cannot share it between multiple Threads.</p>
 *
 * <p><b>WARNING: NOT THREAD SAFE</b></p>
 */
public class SimpleWorld implements World<SimpleWorld>, DefaultWorld {
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
        return new Value<>(this, (O) obj.getOrDefault(id, id.init()));
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <O> Value<SimpleWorld, O> old(Id<? extends O> id) {
        return new Value<>(this, (O) old.getOrDefault(id, id.init()));
    }
    
    @Override
    public <I extends Id<O>, O, E> Value<SimpleWorld, E> send(I id, Message<SimpleWorld, I, O, E> message) {
        DefaultEnv<SimpleWorld, I, O, E> env = new DefaultEnv<SimpleWorld, I, O, E>(this, id);
        Msg<SimpleWorld, I, O, E> msg = message.msg(env);
        O oldObj = obj(id).value;
        
        if (!msg.pre().get()) throw new RuntimeException("Pre-condition failed");
        else {
            try {
                obj.put(id, msg.app().get());
    
                old.put(id, oldObj);
                E eff = msg.eff().get();
                old.put(id, oldObj);   // put it again because of recursive sends to self
    
                if (!msg.pst().get()) throw new RuntimeException("Post-condition failed");
                else return new Value<>(env.world(), eff);
            }
            catch (Exception e) {
                obj.put(id, oldObj);   // Rollback state
                throw e;
            }
        }
    }
    
    @Override public SimpleWorld init() { return new SimpleWorld(); }
}
