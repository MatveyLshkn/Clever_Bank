package thread;

import dao.AccountDao;
import dao.BankDao;
import exception.NoSuchAccountFoundException;
import exception.NoSuchBankFoundException;
import util.PropertiesUtil;

import java.time.LocalDateTime;
import java.time.Month;

public class MoneyAccrualThread extends Thread {
    private Month currMonth = LocalDateTime.now().getMonth();
    private final AccountDao accountDao = AccountDao.getInstance();
    private final BankDao bankDao = BankDao.getInstance();

    /**
     * Runs the MoneyAccrualThread which will regularly, according to a schedule (once every half a minute),
     * check whether it is necessary to charge
     * percentage (1% - the value is substituted from the configuration file config.yaml) on the remainder
     * bills at the end of the month
     */
    @Override
    public void run() {
        while (true) {
            System.out.println("Attempting to accrual money...");
            accrualMoney();
            try {
                MoneyAccrualThread.sleep(30000);
            } catch (InterruptedException e) {
                System.out.println("MoneyAccrualService is interrupted");
            }
        }
    }

    /**
     * Method that will accrual money depending on condition fulfillment
     */
    private void accrualMoney() {
        int percentage = Integer.parseInt(PropertiesUtil.getYaml("bankPercentage"));
        Month temp = LocalDateTime.now().getMonth();
        if (!temp.equals(currMonth)) {
            accountDao.findAll().parallelStream().forEach(account ->
            {
                try {
                    bankDao.refill(account.getBalance() * (double) percentage / 100, account.getId());
                } catch (NoSuchAccountFoundException | NoSuchBankFoundException e) {
                    throw new RuntimeException(e);
                }
            });
            currMonth = temp;
        }
    }
}
