class BankAccount {
    private double balance;

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }
}

public class EX2 {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();

        account.setBalance(5000);

        System.out.println("Account Balance: " + account.getBalance());
    }
}