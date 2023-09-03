import dao.BankDao;
import exception.InsufficientFundsException;
import exception.NoSuchAccountFoundException;
import exception.NoSuchBankFoundException;
import exception.NoSuchUserFoundException;
import util.AccountStatementPeriod;
import util.CheckPrinter;

import java.util.Map;

public class Main {

    public static void main(String[] args) {
        //write method chosen from below  here
        printAccountStatement();
    }

    private static void withdraw() {
        try {
            BankDao bankDao = BankDao.getInstance();
            System.out.println(bankDao.withdraw(100d, 1));
        } catch (NoSuchAccountFoundException e) {
            System.out.println("No such account found");
        } catch (InsufficientFundsException e) {
            System.out.println("Insufficient funds");
        } catch (NoSuchBankFoundException e) {
            System.out.println("No such bank found");
        }
    }

    private static void refill() {
        try {
            BankDao bankDao = BankDao.getInstance();
            Double refill = bankDao.refill(100d, 1);
            System.out.println("new balance: " + refill);
        } catch (NoSuchAccountFoundException e) {
            System.out.println("No such account found");
        } catch (NoSuchBankFoundException e) {
            System.out.println("No such bank found");
        }

    }

    private static void transfer() {
        try {
            BankDao bankDao = BankDao.getInstance();
            Map<String, Double> transfer = bankDao.transfer(1, 2, 500d);
            System.out.println("senderBalance: " + transfer.get("senderBalance"));
            System.out.println("receiverBalance: " + transfer.get("receiverBalance"));
        } catch (InsufficientFundsException e) {
            System.out.println("Insufficient funds");
        } catch (NoSuchAccountFoundException e) {
            System.out.println("No such account found");
        } catch (NoSuchBankFoundException e) {
            System.out.println("No such bank found");
        }
    }

    private static void printAccountStatement() {
        try {
            CheckPrinter.printAccountStatement(AccountStatementPeriod.WHOLE_PERIOD, 1);
        } catch (NoSuchAccountFoundException e) {
            System.out.println("No such account found");
        } catch (NoSuchUserFoundException e) {
            System.out.println("No such user found");
        }
    }
}
