package org.jmanikin.test;

import org.jmanikin.core.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface WorldTest<W extends World<W>> extends TestModule, ConformanceResult {
    
    /* TODO: Add more conformance tests! */
    default List<WorldConformanceError<W>> check(W w) {
        List<WorldConformanceError<W>> result = new ArrayList<>();
        
        result.addAll(checkObj(w.init()));
        result.addAll(checkOld(w.init()));
        result.addAll(checkDeterminism(w.init()));
        result.addAll(checkSend(w.init()));
        result.addAll(checkRollback(w.init()));
    
        return result;
    }
    
    default void error(String error) { throw new RuntimeException(error); }
    
    default List<WorldConformanceError<W>> checkObj(W world) {
        try {
            CId a = new CId(1);
            CId b = new CId(2);
            
            world = world.
                send(a, new CopyId<>()).
                send(b, new LocalCopyId<>()).
                world;
            
            if (world.obj(a).value.member != 1) error("obj(a).value.member should be 1");
            if (world.obj(b).value.member != 2) error("obj(b).value.member should be 2");
        }
        catch (Exception e) {
            return Collections.singletonList(new WorldConformanceError<W>(e, new ObjProblem(), world));
        }
        return Collections.emptyList();
    }
    
    default List<WorldConformanceError<W>> checkOld(W world) {
        try {
            CId a = new CId(1);
            CId b = new CId(2);
            
            world = world.
                send(a, new SetMember<>(10)).
                send(a, new SetMember<>(20)).
                world;
            
            if (world.old(a).value.member != 10)  error("old(a).value.member should be 10");
            if (world.obj(a).value.member != 20)  error("obj(a).value.member should be 20");
    
    
            world = world.
                send(b, new SetMember<>(100)).
                send(b, new SetMember<>(200)).
                world;
    
            if (world.old(b).value.member != 100) error("old(b).value.member should be 100");
            if (world.obj(b).value.member != 200) error("obj(b).value.member should be 200");
        }
        catch (Exception e) {
            return Collections.singletonList(new WorldConformanceError<W>(e, new OldProblem(), world));
        }
        return Collections.emptyList();
    }
    
    default List<WorldConformanceError<W>> checkSend(W world) {
        CId a = new CId(1);
        CId b = new CId(2);
        
        try {
             world.
                send(a, new SendSetMember<>(10, b)).
                send(b, new LocalSendSetMember<>(20, a));
        }
        catch (Exception e) {
            return Collections.singletonList(new WorldConformanceError<W>(e, new SendProblem(), world));
        }
        return Collections.emptyList();
    }
    
    default List<WorldConformanceError<W>> checkDeterminism(W world) {
        CId a = new CId(1);
        CId b = new CId(2);
        CId c = new CId(3);
        CId d = new CId(4);
        
        try {
            for (int i = 0; i < 100; i++) {
                world = world.init().
                    send(a, new CopyId<>()).
                    send(b, new LocalCopyId<>()).
                    send(c, new SetMember<>(20)).
                    send(d, new LocalSetMember<>(200)).
                    world;
        
                if (world.obj(a).value.member != 1)   error("obj(a).value.member should be 1");
                if (world.obj(b).value.member != 2)   error("obj(b).value.member should be 2");
                if (world.obj(c).value.member != 20)  error("obj(c).value.member should be 20");
                if (world.obj(d).value.member != 200) error("obj(d).value.member should be 200");
            }
        }
        catch (Exception e) {
            return Collections.singletonList(new WorldConformanceError<W>(e, new DeterminismProblem(), world));
        }
        return Collections.emptyList();
    }
    
    default List<WorldConformanceError<W>> checkRollback(W world) {
        try {
            CId a = new CId(1);
            CId b = new CId(2);
            
            try { world.send(a, new ThrowPstException<>()); }
            catch (Exception e) { if (world.obj(a).value.member != 0) error("obj(a).value.member should be 0"); }
    
            try { world.send(b, new ThrowPstException<>()); }
            catch (Exception e) { if (world.obj(a).value.member != 0) error("obj(b).value.member should be 0"); }
        }
        catch (Exception e) {
            return Collections.singletonList(new WorldConformanceError<W>(e, new RollbackProblem(), world));
        }
        return Collections.emptyList();
    }
}