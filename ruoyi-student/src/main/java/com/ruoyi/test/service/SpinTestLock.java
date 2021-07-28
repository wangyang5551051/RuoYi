package com.ruoyi.test.service;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by WangYangfan on 2020/11/3 12:10
 */
public class SpinTestLock {

    //java中原子（CAS）操作
    AtomicReference<Thread> owner = new AtomicReference<Thread>();//持有自旋锁的线程对象
    private static SpinTestLock spinTestLock =  new SpinTestLock();
    private SpinTestLock() {

    }
    public  static SpinTestLock getSpinTestLock() {
        return spinTestLock;
    }

    //lock函数将owner设置为当前线程，并且预测原来的值为空。unlock函数将owner设置为null，并且预测值为当前线程。
    // 当有第二个线程调用lock操作时由于owner值不为空，导致循环
    public void lock() {
        //一直被执行，直至第一个线程调用unlock函数将owner设置为null，第二个线程才能进入临界区。
        while (!owner.compareAndSet(null, Thread.currentThread())) {

        }
    }

    public void unLock() {
        owner.compareAndSet(Thread.currentThread(), null);
    }
}
