package org.jmanikin.test;

import org.jmanikin.core.World;
import org.jmanikin.world.SimpleWorld;

import java.util.List;

public class WorldConformanceTest {
    public static <W extends World<W>> boolean check(W world) {
        List<ConformanceResult.WorldConformanceError<W>> errors = new WorldConformanceText<W>().check(world);
        
        for (Object error : errors) { System.err.println("ERROR: " + error); }
        
        return errors.isEmpty();
    }
    
    public static void main(String[] args) { check(new SimpleWorld()); }
    
    static class WorldConformanceText<W extends World<W>> implements WorldTest<W> {}
}
