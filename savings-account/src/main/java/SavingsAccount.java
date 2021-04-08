import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SavingsAccount {
    private long balance;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private Lock preferredLock = new ReentrantLock();
    private int numPreferred;
    private Condition conditionPreferred = lock.newCondition();
    private String name;

    public SavingsAccount(long balance) {
        this.balance = balance;
        this.numPreferred = 0;
    }

    public void deposit(int k) {
        lock.lock();
        try {
            balance += k;
            System.out.println(getName() + " add " + k + ". Balance is " + balance);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void ordinaryWithdraw(int k) throws InterruptedException {
        lock.lock();
        try {
            while (balance < k || numPreferred > 0) {
                condition.await();
            }
            balance -= k;
            System.out.println(getName() + " withdraw " + k + ". Balance is " + balance);
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public void preferredWithdraw(int k) throws InterruptedException {
        // add the numbed of preferred
        preferredLock.lock();
        try {
            numPreferred += 1;
        } finally {
            preferredLock.unlock();
        }

        // withdraw
        lock.lock();
        try {
            while (balance < k) {
                conditionPreferred.await();
            }
            balance -= k;
            System.out.println(getName() + " withdraw preferably " + k + ". Balance is " + balance);
            // notify others
            preferredLock.lock();
            numPreferred -= 1;
            if (numPreferred == 0) {
                condition.signalAll();
            } else {
                conditionPreferred.signalAll();
            }
            preferredLock.unlock();
        } finally {
            lock.unlock();
        }
    }

    public void transfer(int k, SavingsAccount reserve) throws InterruptedException {
        lock.lock();
        try {
            System.out.println("Transfer " + k + " from " + reserve.getName() + " to " + getName());
            reserve.ordinaryWithdraw(k);
            deposit(k);
        } finally {
            lock.unlock();
        }
    }

    public long getBalance() {
        return balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
