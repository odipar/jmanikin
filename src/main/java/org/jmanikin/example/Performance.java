package org.jmanikin.example;

import org.jmanikin.core.*;
import org.jmanikin.world.SimpleWorld;

import java.util.function.Supplier;

public class Performance {
    static class ID implements Id<Counter> {
        public Counter init() {
            return new Counter(0);
        }
    }
    
    static class Counter {
        public final long count;
        
        public Counter(long count) {
            this.count = count;
        }
    }
    
    static class Increase<W extends World<W>> implements Message<W, ID, Counter, Void> {
        public Msg<W, ID, Counter, Void> msg(Environment<W, ID, Counter, Void> e) {
            return e.
                pre(() -> true).
                app(() -> new Counter(e.obj().count + 1)).
                eff(() -> null).
                pst(() -> e.obj().count == e.old().count + 1);
        }
    }
    
    public static void main(String[] args) {
        time(() -> {
            SimpleWorld world = new SimpleWorld();
            ID id = new ID();
            Increase<SimpleWorld> msg = new Increase<>();
            
            int x = 100000000;
            
            for (int i = 0; i < x; i++) {
                world = world.send(id, msg).world;
                if ((i % (x / 10)) == 0) {
                    System.out.println("i: " + i);
                }
            }
            
            return null;
        });
    }
    
    static void time(Supplier<Void> block) {
        double t0 = (double) System.currentTimeMillis();
        block.get();
        double t1 = (double) System.currentTimeMillis();
        System.out.println("elapsed time: " + (t1 - t0) + " ms");
    }
}
