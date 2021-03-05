package org.jmanikin.world;

import org.jmanikin.core.*;

import java.util.function.Supplier;

public interface DefaultWorld {
    /**
     * <p>Default Environment implementation.</p>
     *
     * @param <W> the World Type
     * @param <I> the Id Type
     * @param <O> the Object Type
     * @param <E> the Effect Type
     */
    class DefaultEnv<W extends World<W>, I extends Id<O>, O, E> implements Environment<W, I, O, E>,
        PreCondition<W, I, O, E>, Apply<W, I, O, E>, Effect<W, I, O, E>, PostCondition<W, I, O, E>, Msg<W, I, O, E> {
        
        private W world;
        private final I self;
        
        private Supplier<Boolean> _pre;
        private Supplier<O> _app;
        private Supplier<E> _eff;
        private Supplier<Boolean> _pst;
        
        public DefaultEnv(W world, I self) { this.world = world; this.self = self; }
        
        @Override public W world() { return world; }
        @Override public I self() { return self; }
        @Override public <O2> O2 obj(Id<? extends O2> id) { return eval(() -> world().obj(id)); }
        @Override public <O2> O2 old(Id<? extends O2> id) { return eval(() -> world().old(id)); }
        @Override public <I2 extends Id<O2>, O2, R2> R2 send(I2 id, Message<W, I2, O2, R2> msg) {
            return eval(() -> world().send(id, msg));
        }
        
        @Override public Apply<W, I, O, E> pre(Supplier<Boolean> pre) { _pre = pre;return this; }
        @Override public Effect<W, I, O, E> app(Supplier<O> app) { _app = app; return this; }
        @Override public PostCondition<W, I, O, E> eff(Supplier<E> eff) { _eff = eff;return this; }
        @Override public Msg<W, I, O, E> pst(Supplier<Boolean> pst) { _pst = pst; return this; }
        @Override public Supplier<Boolean> pre() { return _pre; }
        @Override public Supplier<O> app() { return _app; }
        @Override public Supplier<E> eff() { return _eff; }
        @Override public Supplier<Boolean> pst() { return _pst; }
        
        /**
         * Utility method: lazily evaluate f in the World context (threading through the Value/World)
         *
         * @param f   the Supplier of X
         * @param <X> Any X Type
         * @return the result of evaluating f
         */
        private <X> X eval(Supplier<Value<W, X>> f) { Value<W, X> r = f.get(); world = r.world; return r.value; }
    }
}
