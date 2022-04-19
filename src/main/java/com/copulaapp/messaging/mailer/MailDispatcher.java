package com.copulaapp.messaging.mailer;

import java.util.ArrayDeque;
import java.util.logging.Logger;

/**
 * Created by heeleaz on 9/10/17.
 */
public class MailDispatcher implements Runnable {
    public static final int MAXIMUM_MPS = 14;
    private static final MailDispatcher singleton = new MailDispatcher();
    private static final Logger logger = Logger.getLogger("MailDispatcher");
    private final Object lock = new Object();
    private int sentMessageCount;
    private ArrayDeque<BatchJob> queue = new ArrayDeque<>();
    private boolean running = false;

    public static MailDispatcher getInstance() {
        return singleton;
    }

    public void startDispatcher() {
        if (!running) {
            running = true;
            new Thread(this).start();
            logger.info("Dispatcher started");
        } else {
            logger.info("Dispatcher already running");
        }
    }

    public void stopDispatcher(boolean cancelJobs) {
        running = false;
        logger.info("Dispatcher stopped");

        if (cancelJobs) queue.clear();
    }

    public void clearJobs() {
        queue.clear();
    }

    public int getSentMessageCount() {
        return sentMessageCount;
    }

    public void postJob(BatchJob job) {
        queue.add(job);
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        while (running) {
            synchronized (lock) {
                while (queue.size() == 0) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ignored) {
                    }
                }

                BatchJob job = queue.poll();
                if (job.sendMessage()) {
                    ++sentMessageCount;//increase sent message count
                    if (sentMessageCount % MAXIMUM_MPS == 0) {
                        long elapsedTime = System.currentTimeMillis() - startTime;
                        if (elapsedTime <= 1000) {
                            try {//sleep for the rest of the time to complete 1s
                                Thread.sleep(1000 - elapsedTime);
                            } catch (InterruptedException ignored) {
                            }
                        }
                        startTime = System.currentTimeMillis();//update to new time
                    }
                } else queue.addFirst(job);//add the job again. so as to retry push
            }
        }
    }
}
