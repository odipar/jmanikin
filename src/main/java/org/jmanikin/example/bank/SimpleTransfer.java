package org.jmanikin.example.bank;

import org.jmanikin.world.SimpleWorld;
import org.jmanikin.core.Value;

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