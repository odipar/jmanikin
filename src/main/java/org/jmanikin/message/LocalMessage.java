package org.jmanikin.message;

import org.jmanikin.core.*;
import java.util.function.Supplier;

/**
 * <p>A LocalMessage wraps the Environment in a LocalEnv, and temporarily stores the LocalEnv in a ThreadLocal variable.</p>
 * <p>In turn, the LocalEnv can be referenced in the scope of a LocalMessage: a LocalMessage IS A Environment!</p>
 *
 * <p>Example implementation usage:</p>
 *
 * <pre>{@code
 * public Msg<I, O, E> local() { return
 *      pre(() -> ...).
 *      app(() -> ...).
 *      eff(() -> ...).
 *      pst(() -> ...);
 * }
 * }</pre>
 *
 * <p>See also example {@link org.jmanikin.example.bank.AccountModule} for an example.</p>
 *
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface LocalMessage<I extends Id<O>, O, E>
    extends Message<I, O, E>, Environment<I, O, E> {
    
    ThreadLocal<Object> localEnv = new ThreadLocal<>();
    
    @SuppressWarnings("unchecked")
    default LocalEnvironment<I, O, E> env() {
        return (LocalEnvironment<I, O, E>) localEnv.get();
    }
    
    /**
     * Returns the Msg that has been build.
     *
     * @return the Msg that has been build
     */
    Msg<I, O, E> local();
    
    @Override default Msg<I, O, E> msg(Environment<I, O, E> e) {
        localEnv.set(new LocalEnvironment<>(e));
        return local();
    }
    
    //@Override default Environment<I, O, E> snapshot() { return env().snapshot(); }
    @Override default I self() { return env().self(); }
    @Override default O obj() { return env().obj(); }
    @Override default O old() { return env().old(); }
    @Override default <O2> O2 obj(Id<? extends O2> id) { return env().obj(id); }
    @Override default <O2> O2 old(Id<? extends O2> id) { return env().old(id); }
    @Override default Apply<I, O, E> pre(Supplier<Boolean> pre) { return env().pre(pre); }
    @Override default <I2 extends Id<O2>, O2, R2> R2 send(I2 id, Message<I2, O2, R2> msg) {
        return env().send(id, msg);
    }
    
    /**
     * LocalEnvironment implementation that sets the ThreadLocal variable at each Stage.
     *
     * @param <I> the Id Type
     * @param <O> the Object Type
     * @param <E> the Effect Type
     */
    class LocalEnvironment<I extends Id<O>, O, E> implements Environment<I, O, E>,
        PreCondition<I, O, E>, Apply<I, O, E>, Effect<I, O, E>, PostCondition<I, O, E>, Msg<I, O, E> {
        
        final private Environment<I, O, E> env;
        private Supplier<Boolean> _pre;
        private Supplier<O> _app;
        private Supplier<E> _eff;
        private Supplier<Boolean> _pst;
        
        public LocalEnvironment(Environment<I, O, E> env) {
            this.env = env;
        }
        
        //@Override public Environment<I, O, E> snapshot() { return new LocalEnvironment<I, O, E>(env.snapshot()); }
        
        @Override public I self() { return env.self(); }
        @Override public O obj() { return env.obj(); }
        @Override public O old() { return env.old(); }
        @Override public <O2> O2 obj(Id<? extends O2> id) { return env.obj(id); }
        @Override public <O2> O2 old(Id<? extends O2> id) { return env.old(id); }
        @Override public <I2 extends Id<O2>, O2, R2> R2 send(I2 id, Message<I2, O2, R2> msg) {
            try
                { localEnv.set(this); R2 result = env.send(id, msg); localEnv.set(this); return result; }
            catch (Exception e)
                { localEnv.set(this); throw e; }
        }
        
        @Override public Apply<I, O, E> pre(Supplier<Boolean> pre) { _pre = injectEnv(pre) ; return this; }
        @Override public Effect<I, O, E> app(Supplier<O> app) { _app = injectEnv(app); return this; }
        @Override public PostCondition<I, O, E> eff(Supplier<E> eff) { _eff = injectEnv(eff); return this; }
        @Override public Msg<I, O, E> pst(Supplier<Boolean> pst) { _pst = injectEnv(pst); return this; }
        
        @Override public Supplier<Boolean> pre() { return _pre; }
        @Override public Supplier<O> app() { return _app; }
        @Override public Supplier<E> eff() { return _eff; }
        @Override public Supplier<Boolean> pst() { return _pst; }
        
        private <X> Supplier<X> injectEnv(Supplier<X> s) {
            return () -> { localEnv.set(this); X get = s.get(); localEnv.set(this); return get; };
        }
    }
}