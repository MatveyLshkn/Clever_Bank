package service;

import dao.AccountDao;
import dao.AppUserDao;
import dao.BankDao;
import entity.Account;
import entity.Currency;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountService implements EntityService<Account> {
    private static final AccountService INSTANCE = new AccountService();
    private final AccountDao accountDao = AccountDao.getInstance();
    private final BankDao bankDao = BankDao.getInstance();
    private final AppUserDao appUserDao = AppUserDao.getInstance();

    private AccountService() {
    }

    /**
     * @return Instance of class
     */
    public static AccountService getInstance() {
        return INSTANCE;
    }

    /**
     * <p>Handles request and returns list of accounts, or particular one.</p>
     *
     * @param req presence of parameter 'id' in servlet request is up to user
     * @return list of accounts if parameter 'id' is empty or particular account if 'id' exists
     * @see AccountDao
     */
    @Override
    public List<Account> get(HttpServletRequest req) {
        String id = req.getParameter("id");
        List<Account> accounts;
        if (id == null || id.isEmpty()) {
            accounts = accountDao.findAll();
        } else {
            accounts = new ArrayList<>();
            Optional<Account> account = accountDao.findById(Integer.parseInt(id));
            account.ifPresent(accounts::add);
        }
        return accounts;
    }

    /**
     * <p>Handles request and saves new account.</p>
     *
     * @param req must include parameters:
     *            <p>'bankid';</p>
     *            <p>'userid';</p>
     *            <p>'currency' one of 3 currencies (BYN, USD, EUR);</p>
     *            <p>'balance';</p>
     *            <p>'date' at format dd-MM-yyyy.</p>
     * @return if there is no errors returns string "Successfully saved", otherwise error message
     * @see AccountDao
     * @see Currency
     */
    @Override
    public String save(HttpServletRequest req) {
        Account account = new Account();
        StringBuilder message = new StringBuilder();

        Integer bankId = Integer.parseInt(req.getParameter("bankid"));
        Integer userId = Integer.parseInt(req.getParameter("userid"));

        bankDao.findById(bankId).ifPresentOrElse(bank -> account.setBankId(bankId),
                () -> message.append("No such bank found "));

        appUserDao.findById(userId).ifPresentOrElse(user -> account.setAppUserId(userId),
                () -> message.append("No such user found"));

        if (!message.isEmpty()) return message.toString();

        Currency currency = Currency.valueOf(req.getParameter("currency").toUpperCase());
        Double balance = Double.parseDouble(req.getParameter("balance"));
        LocalDateTime openingdate = LocalDateTime.from(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss")
                .parse(req.getParameter("openingdate") + "_00:00:00"));
        account.setCurrency(currency);
        account.setBalance(balance);
        account.setOpeningDate(openingdate);

        accountDao.save(account);
        return "Successfully saved";
    }

    /**
     * <p>Handles request and deletes account.</p>
     *
     * @param req must include parameter 'id'
     * @return if there is no errors returns string "Successfully deleted", otherwise error message
     * @see AccountDao
     */
    @Override
    public String delete(HttpServletRequest req) {
        Integer id = Integer.parseInt(req.getParameter("id"));
        return accountDao.delete(id) ? "Successfully deleted" : "No such account found";
    }

    /**
     * <p>Handles request and updates account info.</p>
     *
     * @param req must include parameters:
     *            <p>'id';</p>
     *            <p>'bankid';</p>
     *            <p>'userid';</p>
     *            <p>'currency' one of 3 currencies (BYN, USD, EUR);</p>
     *            <p>'balance'.</p>
     * @return if there is no errors returns string "Successfully updated", otherwise error message
     * @see AccountDao
     * @see Currency
     */
    @Override
    public String update(HttpServletRequest req) {
        Account account = new Account();
        StringBuilder message = new StringBuilder();

        Integer id = Integer.parseInt(req.getParameter("id"));
        Integer bankId = Integer.parseInt(req.getParameter("bankid"));
        Integer userId = Integer.parseInt(req.getParameter("userid"));

        accountDao.findById(id).ifPresentOrElse(account1 -> account.setId(id),
                () -> message.append("No such account found "));

        bankDao.findById(bankId).ifPresentOrElse(bank -> account.setBankId(bankId),
                () -> message.append("No such bank found "));

        appUserDao.findById(userId).ifPresentOrElse(user -> account.setAppUserId(userId),
                () -> message.append("No such user found"));

        if (!message.isEmpty()) return message.toString();

        Currency currency = Currency.valueOf(req.getParameter("currency").toUpperCase());
        Double balance = Double.parseDouble(req.getParameter("balance"));
        account.setCurrency(currency);
        account.setBalance(balance);

        accountDao.update(account);
        return "Successfully updated";
    }
}
