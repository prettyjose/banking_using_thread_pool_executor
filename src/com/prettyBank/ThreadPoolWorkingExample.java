package com.prettyBank;

import com.prettyBank.runnables.DepositorRunnable;
import com.prettyBank.runnables.WithdrawerRunnable;
import com.prettyBank.util.BankAccounts;

import java.util.Random;
import java.util.concurrent.*;

public class ThreadPoolWorkingExample {
    static int counter = 0;
    static int withdrawerThreadCounter = 0;
    final static String baseName = "depositor_";

public static void main(String[] args) {

    BankAccounts.populateAccounts();

//    Runnable depositorRunnable = new DepositorRunnable(BankAccounts.getAccount(1), 100);
    ThreadPoolExecutor withdrawalService = new ThreadPoolExecutor(2, 2, 100, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(2), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread newThread = new Thread(r, "withdrawer_"+withdrawerThreadCounter++);
            System.out.println("\nNEW THREAD CREATED:: THREAD GROUP: " + newThread.getThreadGroup().getName() + ", THREAD NAME: " + newThread.getName() + "\n");
            return newThread;
        }
    });
//    es.execute(depositorRunnable);
//
////    ((com.prettyBank.runnables.DepositorRunnable)depositorRunnable).setDepositAmt(1000); // old thread modified doesn't give the right functionality
////    es.execute(depositorRunnable);
//    depositorRunnable = new com.prettyBank.runnables.DepositorRunnable(com.prettyBank.util.BankAccounts.getAccount(2), 300);
//    es.execute(depositorRunnable);
//    depositorRunnable = new com.prettyBank.runnables.DepositorRunnable(com.prettyBank.util.BankAccounts.getAccount(1), 400);
//    es.execute(depositorRunnable);

    int corePoolSize = 2;
    ThreadGroup[] threadGroup = {new ThreadGroup("core pool"), new ThreadGroup("extended pool")};
    ThreadPoolExecutor es2 = new ThreadPoolExecutor( corePoolSize, 3, 100, TimeUnit.MILLISECONDS,
//            /* IN THIS CASE, MAX POOL SIZE WILL NEVER GET USED*/new LinkedBlockingQueue<Runnable>());
            /* When AbortPolicy RejectedExecutionHandler is triggered,
             * Parent thread i.e., main()'s execution will get aborted or exited from. */
//            new LinkedBlockingQueue<Runnable>(4), new ThreadPoolExecutor.AbortPolicy());

            /* When DiscardPolicy RejectedExecutionHandler is triggered,
             * Parent thread i.e., main()'s execution will continue. */
//              new LinkedBlockingQueue<Runnable>(4), new ThreadPoolExecutor.DiscardPolicy());

            /* DiscardOldestPolicy RejectedExecutionHandler
             * A handler for rejected tasks that discards the oldest unhandled request and
             * then retries execute, unless the executor is shut down, in which case the task is discarded.  */
//            new LinkedBlockingQueue<Runnable>(4), new ThreadPoolExecutor.DiscardOldestPolicy());

            /* CallerRunsPolicy RejectedExecutionHandler
             * Runs the rejected task directly in the calling thread(in this case main) of the execute method,
             * unless the executor has been shut down, in which case the task is discarded  */
            new LinkedBlockingQueue<Runnable>(4),
            /*
            * class SimpleThreadFactory implements ThreadFactory {
               public Thread newThread(Runnable r) {
                 return new Thread(r);

             }}
             pass new SimpleThreadFactory();
            */
            /*  OR pass
            * new ThreadFactory() {
                public Thread newThread(Runnable r) {
                 return new Thread(r);

             }}  OR SIMPLY THE FOLLOWING LAMBDA EXP
 */
            (r) ->  {
                System.out.println(r.getClass());
                    Thread newThread = new Thread(
                            (++counter > corePoolSize)? threadGroup[1]: threadGroup[0],
                            r,
                            baseName + counter);
//                                ((r.getClass().equals(com.prettyBank.runnables.WithdrawerRunnable.class))? "withdrawer_" : baseName) + counter);
                        System.out.println("\nNEW THREAD CREATED:: THREAD GROUP: " + newThread.getThreadGroup().getName() + ", THREAD NAME: " + newThread.getName() + "\n");
                        return newThread;
                    },
            new ThreadPoolExecutor.CallerRunsPolicy());

    Random r= new Random();
    int nextAccNum;
    Random r2= new Random();
    int depositAmount=0, withdrawalAmt=0;
    DepositorRunnable depositorRunnable1;
    WithdrawerRunnable withdrawerRunnable1;

    for(int i = 1; i<=10; i++) {
        while((nextAccNum = r2.nextInt(4)) == 0);
        while((depositAmount = r.nextInt(500)) == 0);
        depositorRunnable1 =  new DepositorRunnable(BankAccounts.getAccount(nextAccNum), depositAmount);
        es2.execute(depositorRunnable1);

        System.out.println("\napprox. no. of THREADS actively EXECUTING TASKs = active thread count: " + es2.getActiveCount());
        System.out.println("current no. of THREADS in the pool = pool size: " + es2.getPoolSize());
        System.out.println("queue size: "+es2.getQueue().size());

        System.out.println("core pool size: " + es2.getCorePoolSize());
        System.out.println("max allowed no. of threads: " + es2.getMaximumPoolSize());

        System.out.println("highest ever simultaneously active thread count: " + es2.getLargestPoolSize());
        System.out.println("task count ever scheduled: "+ es2.getTaskCount());
        System.out.println("completed task count: "+ es2.getCompletedTaskCount());

        System.out.println("i="+i+" acc num="+nextAccNum+ " dep amt="+depositAmount );

        if(i%2==0){
            while((withdrawalAmt = r.nextInt(1000)) == 0);
            withdrawerRunnable1 = new WithdrawerRunnable(BankAccounts.getAccount(nextAccNum), withdrawalAmt);
//            es2.execute(withdrawerRunnable1);
            withdrawalService.execute(withdrawerRunnable1);//I am using separate withdrawal service just to understand the behaviour.
            // It might not be a good design to have separate services for separate operations.

            System.out.println("\napprox. no. of THREADS actively EXECUTING TASKs = active thread count: " + withdrawalService.getActiveCount());
            System.out.println("current no. of THREADS in the pool = pool size: " + withdrawalService.getPoolSize());
            System.out.println("queue size: "+withdrawalService.getQueue().size());

            System.out.println("core pool size: " + withdrawalService.getCorePoolSize());
            System.out.println("max allowed no. of threads: " + withdrawalService.getMaximumPoolSize());

            System.out.println("highest ever simultaneously active thread count: " + withdrawalService.getLargestPoolSize());
            System.out.println("task count ever scheduled: "+ withdrawalService.getTaskCount());
            System.out.println("completed task count: "+ withdrawalService.getCompletedTaskCount());

            System.out.println("i="+i+" acc num="+nextAccNum+ " dep amt="+withdrawalAmt );
        }


    }

    try{
        while(es2.getCompletedTaskCount() < es2.getTaskCount() || withdrawalService.getCompletedTaskCount() < withdrawalService.getTaskCount()) {
            System.out.println("\nCOMPLETED TASK COUNT: "+ es2.getCompletedTaskCount() + ",  TOTAL TASK COUNT ever scheduled: " + es2.getTaskCount());
            System.out.println("\nCOMPLETED TASK COUNT: "+ withdrawalService.getCompletedTaskCount() + ",  TOTAL TASK COUNT ever scheduled: " + withdrawalService.getTaskCount());
            System.out.println("=======SO MAIN() THREAD GOING TO SLEEP FOR 2S TO LET ALL TASKS IN THE PIPELINE TO COMPLETE=====\n");
            Thread.sleep(2000);
        }
    }catch(InterruptedException ie){

    }finally {
        System.out.println("\n=======MAIN() THREAD IS AWAKE======================\n");
    }

    System.out.println("\n**********FROM STMTS AFTER FOR LOOP IN MAIN() THREAD***********");
    System.out.println("approx. no. of THREADS actively EXECUTING TASKs = active thread count: " + es2.getActiveCount());
    System.out.println("current no. of THREADS in the pool = pool size: " + es2.getPoolSize());
    System.out.println("queue size: "+es2.getQueue().size());

    System.out.println("core pool size: " + es2.getCorePoolSize());
    System.out.println("max allowed no. of threads: " + es2.getMaximumPoolSize());

    System.out.println("highest ever simultaneously active thread count: " + es2.getLargestPoolSize());
    System.out.println("task count ever scheduled: "+ es2.getTaskCount());
    System.out.println("completed task count: "+ es2.getCompletedTaskCount());

    //SHUTDOWN LOGIC
    System.out.println("\n\n<<<<<<<<<  SHUTTING DOWN EXEC SERVICE2  >>>>>>>>>>>>>");

    es2.shutdown(); /*with this es will move from RUNNING to SHUTDOWN>STOP>TIDYING>TERMINATED states.
    All threads in its pool will be issued interrupt. So no more execution can be expected*/
    System.out.println("is shutdown:" + es2.isShutdown()  +" is terminating: "+ es2.isTerminating()+" is terminated: "+ es2.isTerminated());
    try {
        //while(es2.getCompletedTaskCount() < es2.getTaskCount()) { //Doesn't make sense as no new task will get completed.
            System.out.println("Will wait 2s for termination");
            es2.awaitTermination(2, TimeUnit.SECONDS);
        //}
    }catch(InterruptedException ie){}
    finally {
        if(!es2.isTerminated()) {
            System.out.println("Manually kill non-finished tasks. Completed task count: " + es2.getCompletedTaskCount());
            es2.shutdownNow();
        }else{
            System.out.println("EXEC SERVICE IS TERMINATED SUCCESSFULLY. Completed task count: " + es2.getCompletedTaskCount());
        }
    }

    System.out.println("\n\n<<<<<<<<<  SHUTTING DOWN WITHDRAWAL SERVICE  >>>>>>>>>>>>>");

    withdrawalService.shutdown(); /*with this es will move from RUNNING to SHUTDOWN>STOP>TIDYING>TERMINATED states.
    All threads in its pool will be issued interrupt. So no more execution can be expected*/
    try {
        System.out.println("Will wait 2s for termination");
        withdrawalService.awaitTermination(2, TimeUnit.SECONDS);
        //}
    }catch(InterruptedException ie){}
    finally {
        if(!withdrawalService.isTerminated()) {
            System.out.println("Manually kill non-finished tasks. Completed task count: " + withdrawalService.getCompletedTaskCount());
            withdrawalService.shutdownNow();
        }else{
            System.out.println("withdrawalService EXEC SERVICE IS TERMINATED SUCCESSFULLY. Completed task count: " + withdrawalService.getCompletedTaskCount());
        }
    }

    //Verify if expected changes in bank balances is there.
    BankAccounts.printAllAccounts();

    System.out.println("\nTHANK YOU FOR USING pretty'S BANKING FACILITY. WE CAN'T WAIT TO SEE YOU AGAIN. :) BYE FOR NOW");
}
}
