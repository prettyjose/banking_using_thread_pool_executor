package com.prettyBank.runnables;

import com.prettyBank.models.BankAccount;

import java.util.concurrent.atomic.AtomicInteger;

public class WithdrawerRunnable implements Runnable {
    private BankAccount account;
    private AtomicInteger withdrawalAmt;
    public WithdrawerRunnable(BankAccount acc, int amt){
        account = acc;
        withdrawalAmt= new AtomicInteger(amt);
    }

    public void run() {
        if(account.getBalance() < withdrawalAmt.get()){
            System.out.println("NO SUFFICIENT BALANCE. GIVE A LESSER AMT. ::: " + "Thread Group: " + Thread.currentThread().getThreadGroup().getName() + ", Thread: " + Thread.currentThread().getName()+ ", Account No.: " + account.getAccNum()+ ", Cur bal: "+ account.getBalance() + ", Withdrawal req amt: "+ withdrawalAmt.get());
            return;
        }
        account.withdraw(withdrawalAmt);
    }
}
