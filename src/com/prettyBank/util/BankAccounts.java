package com.prettyBank.util;

import com.prettyBank.models.BankAccount;

import java.util.HashMap;
import java.util.Map;

public class BankAccounts {
    private static Map<Integer, BankAccount> accounts;
    public static void populateAccounts() {
        accounts = new HashMap<Integer, BankAccount>(3);
        accounts.put(1, new BankAccount(1));
        accounts.put(2, new BankAccount(2));
        accounts.put(3, new BankAccount(3));
    }

    public static BankAccount getAccount(int id){
        return accounts.get(id);
    }
    public static void printAllAccounts() {
        System.out.println("\n\n(((((  ALL ACCOUNT DETAILS  )))))");
        accounts.entrySet()
                .stream()
                .forEach((each)->System.out.println("Acc:" + each.getKey() + " Bal:" + each.getValue().getBalance()));

//        for(BankAccount each: accounts)
//            System.out.println("Acc:" + BankAccounts.getAccount(id).accNum + " Bal:" + BankAccounts.getAccount(id).bankBal.getResourceVal_bankbalance());
    }
}
