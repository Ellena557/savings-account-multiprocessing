import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SavingsAccountExtra {
    private long balance;
    private Lock lock = new ReentrantLock();
    private Lock transferLock = new ReentrantLock();
    private Condition condition = lock.newCondition();
    private int numPreferred;
    private Condition conditionPreferred = lock.newCondition();
    private String name;

    public SavingsAccountExtra(long balance) {
        this.balance = balance;
        this.numPreferred = 0;
    }

    public void deposit(int k) {
        lock.lock();
        try {
            balance += k;
            System.out.println(getName() + " add " + k + ". Balance is " + balance);
            conditionPreferred.signalAll();
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
        lock.lock();
        try {
            numPreferred += 1;
        } finally {
            lock.unlock();
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
            numPreferred -= 1;
            if (numPreferred == 0) {
                condition.signalAll();
            } else {
                conditionPreferred.signalAll();
            }
        } finally {
            lock.unlock();
        }
    }

    public void transfer(int k, SavingsAccountExtra reserve) throws InterruptedException {
        transferLock.lock();
        try {
            System.out.println("Transfer " + k + " from " + reserve.getName() + " to " + getName());
            double state = Math.random();
            if (state < 0.5) {
                reserve.ordinaryWithdraw(k);
            } else {
                reserve.preferredWithdraw(k);
            }
            deposit(k);
        } finally {
            transferLock.unlock();
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
