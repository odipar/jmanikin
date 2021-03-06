![Manikin](docs/manikin.jpg)
# Manikin
Manikin is an embedded Domain Specific Language (DSL) that implements 'Worldly' Objects - Objects that participate and interact in the scope of Worlds.
With Manikin, you can guard Object states with pre- and post- Conditions, and optionally track all stateful Effects and dispatched Messages.

Manikin is heavily inspired by the [Eiffel](https://www.eiffel.com) programming language, [Software Transactional Memory](https://en.wikipedia.org/wiki/Software_transactional_memory) and [Worlds](http://www.vpri.org/pdf/tr2011001_final_worlds.pdf) that have similar goals.

### Message dispatch through Worlds
A Message is dispatched to an Object via a World, which is then functionally updated and passed through.
Because Worlds can track all intermediate and previous Object states, it is very easy to rollback state in case of failure, or to retry work after conflicts. 

### Immutability
All Objects, Messages and Worlds should be [immutable](https://softwareengineering.stackexchange.com/questions/148108/why-is-global-state-so-evil): they always create new states based on old states. 
The core API is carefully designed for this exact purpose: don't share mutable state!

For the Purists out there: Manikin enables Purely Functional Object Orientated Programming (PFOOP 🙃) without resorting to [Monads](https://zio.dev).
(Monadic programming in Java is very verbose, as Java lacks convenient flatMap syntax).

### Concurrent and Distributed
Manikin can also be configured to run on top of multi-threaded, concurrent or distributed Worlds - backed by databases such as [CockroachDB](https://www.cockroachlabs.com) - with strong [Serializability](https://en.wikipedia.org/wiki/Serializability) guarantees.  
                                                           
### Syntax and Types
You can succinctly specify Objects, Identities, Messages, Conditions and Effects with Manikin *and* statically type them (making heavy use of Java Generics).

Additionally, Manikin reduces the amount of Java boilerplate code to the absolute minimum by providing a fluent builder pattern to specify Messages.
Java boilerplate can be reduced even more with [Records](https://cr.openjdk.java.net/~briangoetz/amber/datum.html) or project [Lombok](https://www.baeldung.com/intro-to-project-lombok).
                                                             
### Java, Scala and Kotlin                                
The core abstract API is developed in Java 1.8 and has NO dependencies. There are also Scala and Kotlin versions available that are build on top of the core Java API but require less boilerplate.
  
### Testing
Conformance tests can be found at [org.jmanikin.test](https://github.com/odipar/jmanikin/tree/master/src/main/java/org/jmanikin/test). You can call these tests from your favourite test library.

### Why?
If you like [higher order state](https://www.cs.utexas.edu/~wcook/Drafts/2009/essay.pdf), but shy away from mutable shared state, you should try Manikin!

### Bank Example
Here is a minimal Java example to get a feel of Manikin. 
Please also have a look at the [Scala](https://github.com/odipar/smanikin) and [Kotlin](https://github.com/odipar/kmanikin) version. 

```java
public class SimpleTransfer {
    public static void main(String[] args) {
        AccountModule.ID a1 = new AccountModule.ID("A1");
        AccountModule.ID a2 = new AccountModule.ID("A2");
        TransferModule.ID t1 = new TransferModule.ID(1L);
        
        Value<SimpleWorld, Void> result = new SimpleWorld().
            send(a1, new AccountModule.Open(50.0)).
            send(a2, new AccountModule.Open(80.0)).
            send(t1, new TransferModule.Book(a1, a2, 30.0));
        
        System.out.println(result.obj(a1).value().balance); // 20.0
        System.out.println(result.obj(a2).value().balance); // 110.0
    }
}
```

```java
public interface AccountModule {
    class ID implements Id<Account> {
        public final String id;
        public ID(String id) { this.id = id; }
        @Override public Account init() { return new Account(0.0); }
    }
    
    class Account {
        public final Double balance;
        public Account(Double balance) { this.balance = balance; }
    }
    
    interface AccountMsg extends LocalMessage<ID, Account, Void> { }
    
    class Open implements AccountMsg {
        public final Double initial;
        public Open(Double initial) { this.initial = initial; }
        
        @Override public Msg<ID, Account, Void> local() { return
            pre(() -> initial >= 0.0).
            app(() -> new Account(initial)).
            eff(() -> null).
            pst(() -> obj().balance == initial);
        }
    }
    
    class Deposit implements AccountMsg {
        public final Double amount;
        public Deposit(Double amount) { this.amount = amount; }
        
        @Override public Msg<ID, Account, Void> local() { return
            pre(() -> amount > 0.0).
            app(() -> new Account(obj().balance + amount)).
            eff(() -> null).
            pst(() -> obj().balance == old().balance + amount);
        }
    }
    
    class Withdraw implements AccountMsg {
        public final Double amount;
        public Withdraw(Double amount) { this.amount = amount; }
        
        @Override public Msg<ID, Account, Void> local() { return
            pre(() -> amount > 0.0 && obj().balance >= amount).
            app(() -> new Account(obj().balance - amount)).
            eff(() -> null).
            pst(() -> obj().balance == old().balance - amount);
        }
    }
}
```

```java
public interface TransferModule {
    class ID implements Id<Transfer> {
        public final Long id;
        public ID(Long id) {
            this.id = id;
        }
        @Override public Transfer init() {
            return new Transfer(null, null, 0.0);
        }
    }
    
    class Transfer {
        public final AccountModule.ID from;
        public final AccountModule.ID to;
        public final Double amount;
        
        public Transfer(AccountModule.ID from, AccountModule.ID to, Double amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
    }
    
    interface TransferMsg extends LocalMessage<ID, Transfer, Void> { }
    
    class Book implements TransferMsg {
        public final AccountModule.ID from;
        public final AccountModule.ID to;
        public final Double amount;
        
        public Book(AccountModule.ID from, AccountModule.ID to, Double amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
        
        @Override public Msg<ID, Transfer, Void> local() { return
            pre(() -> amount > 0.0 && from != to).
            app(() -> new Transfer(from, to, amount)).
            eff(() -> {
                send(from, new AccountModule.Withdraw(amount));
                return  send(to, new AccountModule.Deposit(amount));
            }).
            pst(() -> obj(from).balance + obj(to).balance == old(from).balance + old(to).balance);
        }
    }
}
```
