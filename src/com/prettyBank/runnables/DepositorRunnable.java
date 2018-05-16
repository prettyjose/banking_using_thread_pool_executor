package com.prettyBank.runnables;

import com.prettyBank.models.BankAccount;
import com.prettyBank.util.BankAccounts;

import java.util.concurrent.atomic.AtomicInteger;

public class DepositorRunnable implements Runnable {
    private BankAccount account;
    private AtomicInteger depositAmt;
    public DepositorRunnable(BankAccount acc, int amt){
        account = acc;
        depositAmt= new AtomicInteger(amt);
    }
    public void setAccount(int accountNumber){
        account = BankAccounts.getAccount(accountNumber);
    }
    public  void setDepositAmt(int amt) {
        depositAmt = new AtomicInteger(amt);
    }
    public void run() {
        account.deposit(depositAmt);
    }
}
