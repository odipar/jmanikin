package org.jmanikin.message;

import org.jmanikin.core.*;

import java.util.function.Supplier;

/**
 * <p>A LocalMessage wraps the Environment in a LocalEnv, and temporarily stores the LocalEnv in a ThreadLocal variable.</p>
 * <p>In turn, the LocalEnv can be referenced in the scope of a LocalMessage.</p>
 * <p>A LocalMessage IS A Environment.</p>
 *
 * <p>Example implementation usage:</p>
 *
 * <pre>{@code
 * public Msg<W, I, O, E> local() { return
 *      pre(() -> ...).
 *      app(() -> ...).
 *      eff(() -> ...).
 *      pst(() -> ...);
 * }
 * }</pre>
 *
 * <p>See also example {@link org.jmanikin.example.bank.AccountModule} for an example.</p>
 *
 * @param <W> the World Type
 * @param <I> the Id Type
 * @param <O> the Object Type
 * @param <E> the Effect Type
 */
public interface LocalMessage<W extends World<W>, I extends Id<O>, O, E>
    extends Message<W, I, O, E>, Environment<W, I, O, E> {
    
    ThreadLocal<Object> localEnv = new ThreadLocal<>();
    
    @SuppressWarnings("unchecked")
    default LocalEnvironment<W, I, O, E> env() {
        return (LocalEnvironment<W, I, O, E>) localEnv.get();
    }
    
    /**
     * Returns the Msg that has been build
     *
     * @return the Msg that has been build
     */
    Msg<W, I, O, E> local();
    
    @Override default Msg<W, I, O, E> msg(Environment<W, I, O, E> e) {
        localEnv.set(new LocalEnvironment<>(e));
        return local();
    }
    
    @Override default W world() { return env().world(); }
    @Override default I self() { return env().self(); }
    @Override default O obj() { return env().obj(); }
    @Override default O old() { return env().old(); }
    @Override default <O2> O2 obj(Id<? extends O2> id) { return env().obj(id); }
    @Override default <O2> O2 old(Id<? extends O2> id) { return env().old(id); }
    @Override default Apply<W, I, O, E> pre(Supplier<Boolean> pre) { return env().pre(pre); }
    @Override default <I2 extends Id<O2>, O2, R2> R2 send(I2 id, Message<W, I2, O2, R2> msg) {
        return env().send(id, msg);
    }
    
    class LocalEnvironment<W extends World<W>, I extends Id<O>, O, E> implements Environment<W, I, O, E>,
        PreCondition<W, I, O, E>, Apply<W, I, O, E>, Effect<W, I, O, E>, PostCondition<W, I, O, E>, Msg<W, I, O, E> {
        
        final private Environment<W, I, O, E> env;
        private Supplier<Boolean> _pre;
        private Supplier<O> _app;
        private Supplier<E> _eff;
        private Supplier<Boolean> _pst;
        
        public LocalEnvironment(Environment<W, I, O, E> env) {
            this.env = env;
        }
        
        @Override public W world() { return env.world(); }
        @Override public I self() { return env.self(); }
        @Override public O obj() { return env.obj(); }
        @Override public O old() { return env.old(); }
        @Override public <O2> O2 obj(Id<? extends O2> id) { return env.obj(id); }
        @Override public <O2> O2 old(Id<? extends O2> id) { return env.old(id); }
        @Override public <I2 extends Id<O2>, O2, R2> R2 send(I2 id, Message<W, I2, O2, R2> msg) {
            return env.send(id, msg);
        }
        
        @Override public Apply<W, I, O, E> pre(Supplier<Boolean> pre) { _pre = injectEnv(pre) ; return this; }
        @Override public Effect<W, I, O, E> app(Supplier<O> app) { _app = injectEnv(app); return this; }
        @Override public PostCondition<W, I, O, E> eff(Supplier<E> eff) { _eff = injectEnv(eff); return this; }
        @Override public Msg<W, I, O, E> pst(Supplier<Boolean> pst) { _pst = injectEnv(pst); return this; }
        
        @Override public Supplier<Boolean> pre() { return _pre; }
        @Override public Supplier<O> app() { return _app; }
        @Override public Supplier<E> eff() { return _eff; }
        @Override public Supplier<Boolean> pst() { return _pst; }
        
        private <X> Supplier<X> injectEnv(Supplier<X> s) { return () -> { localEnv.set(this); return s.get(); }; }
    }
}