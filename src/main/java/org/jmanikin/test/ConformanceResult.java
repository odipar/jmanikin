package org.jmanikin.test;

import org.jmanikin.core.World;

public interface ConformanceResult {
    class WorldConformanceError<W extends World<W>> {
        public final Exception exception;
        public final ConformanceErrorType errorType;
        public final W world;
        
        public WorldConformanceError(Exception exception, ConformanceErrorType errorType, W world) {
            this.exception = exception; this.errorType = errorType ; this.world = world;
        }
    
        @Override public String toString() {
          return "WorldConformanceError(" + errorType + ", " + exception + ", " + world + ")";
        }
    }
    
    abstract class ConformanceErrorType {
        abstract String reason();
        @Override public String toString() { return reason(); }
    }
    
    final class OldProblem extends ConformanceErrorType {
        @Override public String reason() { return "Old issue"; }
    }
    
    final class ObjProblem extends ConformanceErrorType {
        @Override public String reason() { return "Obj issue"; }
    }
    
    final class SendProblem extends ConformanceErrorType {
        @Override public String reason() { return "Send issue"; }
    }
    
    final class RollbackProblem extends ConformanceErrorType {
        @Override public String reason() { return "Rollback issue"; }
    }
    
    final class DeterminismProblem extends ConformanceErrorType {
        @Override public String reason() { return "Determinism issue"; }
    }
}
