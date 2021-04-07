import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SavingsAccount {
    private int balance;
    Lock lock = new ReentrantLock();
    Condition condition = lock.newCondition();
    Lock preferredLock = new ReentrantLock();
    private int numPreferred;
    Condition conditionPreferred = lock.newCondition();


    public SavingsAccount(int balance) {
        this.balance = balance;
        this.numPreferred = 0;
    }

    public void deposit(int k) {
        lock.lock();
        try {
            balance += k;
            System.out.println("ADD:   balance=" + balance + " :: added=" + k);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void ordinaryWithdraw(int k) throws InterruptedException {
        lock.lock();
        try {
            while (true) {
                if (balance < k || numPreferred > 0) {
                    condition.await();
                }
                if (balance > k) {
                    break;
                }
            }
            balance -= k;
            System.out.println("SUB:   balance=" + balance + " :: subbed=" + k);
        } finally {
            lock.unlock();
        }
    }

    public void preferredWithdraw(int k) throws InterruptedException {
        // add the numbed of preferred
        preferredLock.lock();
        try {
            numPreferred += 1;
        }
        finally {
            preferredLock.unlock();
        }

        // withdraw
        lock.lock();
        try {
            while (balance < k) {
                conditionPreferred.await();
            }
            balance -= k;
            System.out.println("PREFERRED SUB:   balance=" + balance + " :: subbed=" + k);

            // notify others
            preferredLock.lock();
            numPreferred -= 1;
            if (numPreferred == 0) {
                condition.signalAll();
            }
            else {
                conditionPreferred.signalAll();
            }
            preferredLock.unlock();
        }
        finally {
            lock.unlock();
        }
    }


    public int getBalance() {
        return balance;
    }
}
