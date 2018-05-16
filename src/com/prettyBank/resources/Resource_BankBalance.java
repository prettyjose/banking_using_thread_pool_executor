package com.prettyBank.resources;

import java.util.concurrent.locks.*;
import java.util.concurrent.locks.Lock;

public class Resource_BankBalance{
    final ReadWriteLock lock = new ReentrantReadWriteLock();
    private int resourceVal_bankbalance = 0;
    public int getResourceVal_bankbalance() {
        try {
            lock.readLock().lock();
            return resourceVal_bankbalance;
        }finally {
            lock.readLock().unlock();
        }
    }
    public int add(int depositAmt) throws InterruptedException{

        try {
            lock.writeLock().lockInterruptibly();
            resourceVal_bankbalance += depositAmt;
            return resourceVal_bankbalance;
        } finally {
            lock.writeLock().unlock();
        }
    }
    public int deduct(int withdrawalAmt) throws InterruptedException{
        try {
            lock.writeLock().lockInterruptibly();
            resourceVal_bankbalance -= withdrawalAmt;
            return resourceVal_bankbalance;
        } finally {
            lock.writeLock().unlock();
        }
    }

}


















































































































































































































