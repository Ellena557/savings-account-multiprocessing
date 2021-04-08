import org.junit.jupiter.api.Test;

public class TransferCase {
    @Test
    public void makeTransfers() throws InterruptedException {
        int numAccounts = 10;
        SavingsAccount[] accounts = new SavingsAccount[numAccounts];
        Thread[] minorThreads = new Thread[numAccounts];

        for (int i = 0; i < numAccounts; i++) {
            long randSum = (long) (Math.random() * 300);
            accounts[i] = new SavingsAccount(randSum);
            accounts[i].setName("Account " + i);
            System.out.println("Account " + i + " has balance = " + accounts[i].getBalance());
        }

        for (int i = 0; i < numAccounts; i++) {
            int randNum = (int) (Math.random() * 10);

            // не можем перевести сами себе по логике
            while (randNum == i || randNum > 9) {
                randNum = (int) (Math.random() * 10);
            }
            minorThreads[i] = new TransferThread(accounts[randNum], accounts[i]);
            minorThreads[i].start();
        }

        // Ждём (по условию - час)
        Thread.sleep(5000);

        System.out.println("BOSS");
        Thread bossThread = new Thread(() -> {
            for (int i = 0; i < numAccounts; i++) {
                accounts[i].deposit(1000);
            }
        });

        bossThread.start();
        bossThread.join();

        System.out.println("New balances: ");
        for (int i = 0; i < numAccounts; i++) {
            System.out.println(accounts[i].getName() + " has " + accounts[i].getBalance());
        }
    }


    private class TransferThread extends Thread {
        private SavingsAccount accountFrom;
        private SavingsAccount accountTo;

        private TransferThread(SavingsAccount accountFrom, SavingsAccount accountTo) {
            this.accountFrom = accountFrom;
            this.accountTo = accountTo;
        }

        @Override
        public void run() {
            try {
                accountTo.transfer(100, accountFrom);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
