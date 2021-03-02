package org.jmanikin.example.bank;

import org.jmanikin.core.*;
import org.jmanikin.message.LocalMessage;

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
    
    interface TransferMsg<W extends World<W>> extends LocalMessage<W, ID, Transfer, Void> { }
    
    class Book<W extends World<W>> implements TransferMsg<W> {
        public final AccountModule.ID from;
        public final AccountModule.ID to;
        public final Double amount;
        
        public Book(AccountModule.ID from, AccountModule.ID to, Double amount) {
            this.from = from;
            this.to = to;
            this.amount = amount;
        }
        
        @Override public Msg<W, ID, Transfer, Void> local() { return
            pre(() -> amount > 0.0 && from != to).
            app(() -> new Transfer(from, to, amount)).
            eff(() -> {
                        send(from, new AccountModule.Withdraw<>(amount));
                return  send(to, new AccountModule.Deposit<>(amount));
            }).
            pst(() -> obj(from).balance + obj(to).balance == old(from).balance + old(to).balance);
        }
    }
}