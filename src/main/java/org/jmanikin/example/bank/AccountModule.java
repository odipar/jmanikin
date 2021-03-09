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
