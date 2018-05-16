package com.prettyBank.models;

import com.prettyBank.resources.Resource_BankBalance;

import java.util.concurrent.atomic.AtomicInteger;

public class BankAccount {
    //add name etc later
    private int accNum;
    private Resource_BankBalance bankBal;
    public BankAccount(int acc_num){
        accNum = acc_num;
        bankBal = new Resource_BankBalance();
    }
    public int getBalance(){
        return bankBal.getResourceVal_bankbalance();
    }
    public void deposit(AtomicInteger depositAmt) {
        try{
            System.out.println("Thread Group: " + Thread.currentThread().getThreadGroup().getName() + ", Thread: " + Thread.currentThread().getName()+ ", Account No.: " + accNum+ ", Cur bal: "+ bankBal.getResourceVal_bankbalance() + ", Deposit amt: "+ depositAmt.get() + ", New bal: " + bankBal.add(depositAmt.get()));
            Thread.sleep(200);
        }catch(InterruptedException ie) {
            System.out.println(Thread.currentThread().getName()+ " encountered Interrupted exception, probably while waiting for the lock to deposit an amt.");
        }
    }
    public void withdraw(AtomicInteger withdrawalAmt) {
        try{
            System.out.println("Thread Group: " + Thread.currentThread().getThreadGroup().getName() + ", Thread: " + Thread.currentThread().getName()+ ", Account No.: " + accNum+ ", Cur bal: "+ bankBal.getResourceVal_bankbalance() + ", Withdrawal amt: "+ withdrawalAmt.get() + ", New bal: " + bankBal.deduct(withdrawalAmt.get()));
            Thread.sleep(200);
        }catch(InterruptedException ie) {
            System.out.println(Thread.currentThread().getName()+ " encountered Interrupted exception, probably while waiting for the lock to withdraw an amt.");
        }
    }

    public int getAccNum() {
        return accNum;
    }
}
