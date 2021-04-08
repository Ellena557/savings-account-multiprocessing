import org.junit.Assert;
import org.junit.Test;

public class SavingsAccountTest {

    @Test
    public void withdrawTest() throws InterruptedException {
        SavingsAccount account = new SavingsAccount(50);
        account.setName("Acc");
        account.ordinaryWithdraw(50);
        Assert.assertEquals(0, account.getBalance());
    }

    @Test
    public void depositTest() {
        SavingsAccount account = new SavingsAccount(70);
        account.setName("Acc");
        account.deposit(50);
        Assert.assertEquals(120, account.getBalance());
    }

    @Test
    public void preferredWithdrawTest() throws InterruptedException {
        SavingsAccount account = new SavingsAccount(500);
        account.setName("Acc");
        account.preferredWithdraw(50);
        Assert.assertEquals(450, account.getBalance());
    }
}