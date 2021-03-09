package org.jmanikin.test;

import org.jmanikin.core.*;
import org.jmanikin.message.LocalMessage;

public interface TestModule {
    class CId implements Id<CObject> {
        public final int id;
        public CId(int id) { this.id = id; };
        public CObject init() { return new CObject(0); }
    }
    
    class CObject {
        public final int member;
        public CObject(int member) { this.member = member; }
    }
    
    interface CMsg<E> extends Message<CId, CObject, E> { }
    interface LMsg<E> extends LocalMessage<CId, CObject, E> { }
    
    class CopyId implements CMsg<Void> {
        @Override public Msg<CId, CObject, Void> msg(Environment<CId, CObject, Void> e) { return e.
            pre(() -> true).
            app(() -> new CObject(e.self().id)).
            eff(() -> null).
            pst(() -> e.obj().member == e.self().id);
        }
    }
    
    class LocalCopyId implements LMsg<Void> {
        @Override public Msg<CId, CObject, Void> local() { return
            pre(() -> true).
                app(() -> new CObject(self().id)).
                eff(() -> null).
                pst(() -> obj().member == self().id);
        }
    }
    
    class SetMember implements CMsg<Integer> {
        public final int member;
        public SetMember(int member) { this.member = member; }
        
        @Override public Msg<CId, CObject, Integer> msg(Environment<CId, CObject, Integer> e) { return e.
            pre(() -> true).
            app(() -> new CObject(member)).
            eff(() -> member).
            pst(() -> e.obj().member == member);
        }
    }
    
    class LocalSetMember implements LMsg<Integer> {
        public final int member;
        public LocalSetMember(int member) { this.member = member; }
        
        @Override public Msg<CId, CObject, Integer> local() { return
            pre(() -> true).
                app(() -> new CObject(member)).
                eff(() -> member).
                pst(() -> obj().member == member);
        }
    }
    
    class SendSetMember implements CMsg<Integer> {
        public final int member;
        public final CId other;
        
        public SendSetMember(int member, CId other) { this.member = member; this.other = other; }
        
        @Override public Msg<CId, CObject, Integer> msg(Environment<CId, CObject, Integer> e) { return e.
            pre(() -> other != null).
            app(() -> new CObject(member)).
            eff(() -> e.send(other, new SetMember(member))).
            pst(() -> e.obj(other).member == e.obj().member);
        }
    }
    
    class LocalSendSetMember implements LMsg<Integer> {
        public final int member;
        public final CId other;
        
        public LocalSendSetMember(int member, CId other) { this.member = member; this.other = other; }
        
        @Override public Msg<CId, CObject, Integer> local() { return
            pre(() -> other != null).
            app(() -> new CObject(member)).
            eff(() -> send(other, new SetMember(member))).
            pst(() -> obj(other).member == obj().member);
        }
    }
    
    class ThrowPstException implements LMsg<Void> {
        public ThrowPstException() { }
        
        @Override public Msg<CId, CObject, Void> local() { return
            pre(() -> true).
                app(() -> new CObject(1000)).
                eff(() -> null).
                pst(() -> false);
        }
    }
}
