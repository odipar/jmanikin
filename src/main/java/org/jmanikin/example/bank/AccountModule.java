package org.jmanikin.example.bank;

import org.jmanikin.core.*;
import org.jmanikin.message.LocalMessage;

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
    
    interface AccountMsg<W extends World<W>> extends LocalMessage<W, ID, Account, Void> { }
    
    class Open<W extends World<W>> implements AccountMsg<W> {
        public final Double initial;
        public Open(Double initial) { this.initial = initial; }
    
        @Override public Msg<W, ID, Account, Void> local() { return
            pre(() -> true).
            app(() -> new Account(initial)).
            eff(() -> null).
            pst(() -> obj().balance == initial);
        }
    }
    
    class Deposit<W extends World<W>> implements AccountMsg<W> {
        public final Double amount;
        public Deposit(Double amount) { this.amount = amount; }
        
        @Override public Msg<W, ID, Account, Void> local() { return
            pre(() -> true).
            app(() -> new Account(obj().balance + amount)).
            eff(() -> null).
            pst(() -> obj().balance == old().balance + amount);
        }
    }
    
    class Withdraw<W extends World<W>> implements AccountMsg<W> {
        public final Double amount;
        public Withdraw(Double amount) { this.amount = amount; }
        
        @Override public Msg<W, ID, Account, Void> local() { return
            pre(() -> true).
            app(() -> new Account(obj().balance - amount)).
            eff(() -> null).
            pst(() -> obj().balance == old().balance - amount);
        }
    }
}
