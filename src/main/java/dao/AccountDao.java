package dao;


import entity.Account;
import entity.Currency;
import entity.Transaction;
import exception.NoSuchAccountFoundException;
import util.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class AccountDao implements Dao<Integer, Account> {
    private static final AccountDao INSTANCE = new AccountDao();
    private final ConcurrentNavigableMap<Integer, Account> accountMap = new ConcurrentSkipListMap<>();
    private static final TransactionDao transactionDao = TransactionDao.getInstance();

    private static final String FIND_ALL_SQL = """
            SELECT * FROM account;
            """;
    private static final String DELETE_SQL = """
            DELETE FROM account WHERE id = ?;
            """;
    private static final String UPDATE_SQL = """
            UPDATE account
            SET currency = ?,
                balance = ?,
                bankid = ?,
                appuserid = ?
            WHERE id = ?;
            """;

    private static final String SAVE_SQL = """
            INSERT INTO account(currency, openingdate, balance, bankid, appuserid)
            VALUES(?,?,?,?,?)
            RETURNING id;
            """;

    {
        findAll().forEach(account -> accountMap.put(account.getId(), account));
    }

    private AccountDao() {
    }

    /**
     * @return Instance of class
     */
    public static AccountDao getInstance() {
        return INSTANCE;
    }


    /**
     * Blocks locks of both accounts
     *
     * @param acc1 first account to lock
     * @param acc2 second account to lock
     */
    public void lockAccounts(Account acc1, Account acc2) {
        while (true) {
            boolean acc1LockResult = acc1.getLock().tryLock();
            boolean acc2LockResult = acc2.getLock().tryLock();
            if (acc1LockResult && acc2LockResult) break;
            if (acc1LockResult) acc1.getLock().unlock();
            if (acc2LockResult) acc2.getLock().unlock();
        }
    }

    /**
     * Blocks lock of an account
     *
     * @param acc account to lock
     */
    public void lockAccount(Account acc) {
        while (true) {
            if (acc.getLock().tryLock()) break;
        }
    }

    /**
     * Calculates income of Account by period of time (from, to)
     *
     * @param accId id of Account
     * @param from  start date of calculating
     * @param to    finish date of calculating
     * @return calculated income of particular account at chosen period (from, to)
     */
    public Double getIncomeByIdAndPeriod(Integer accId, LocalDateTime from, LocalDateTime to) {
        List<Transaction> transactions = transactionDao.findAll();
        return transactions.stream()
                .filter(transaction -> Objects.equals(transaction.getReceiverAccId().orElse(0), accId))
                .filter(transaction -> transaction.getDate().isBefore(to) && transaction.getDate().isAfter(from))
                .map(Transaction::getTotal)
                .reduce(Double::sum).orElse(0d);
    }

    /**
     * Calculates outgo of Account by period of time (from, to)
     *
     * @param accId id of Account
     * @param from  start date of calculating
     * @param to    finish date of calculating
     * @return calculated outgo of particular account at chosen period (from, to)
     */
    public Double getOutgoByIdAndPeriod(Integer accId, LocalDateTime from, LocalDateTime to) {
        List<Transaction> transactions = transactionDao.findAll();
        return -transactions.stream()
                .filter(transaction -> Objects.equals(transaction.getSenderAccId().orElse(0), accId))
                .filter(transaction -> transaction.getDate().isBefore(to) && transaction.getDate().isAfter(from))
                .map(Transaction::getTotal)
                .reduce(Double::sum).orElse(0d);
    }

    /**
     * @return List of all Accounts
     */
    @Override
    public List<Account> findAll() {
        if (!accountMap.isEmpty()) return accountMap.values().stream().toList();

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Account> accounts = new ArrayList<>();
            while (resultSet.next()) {
                accounts.add(buildAccount(resultSet));
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * finds account by id without throwing NoSuchAccountFoundException if not found
     *
     * @param id account id
     * @return Optional of Account. Optional.empty if account not found
     */
    @Override
    public Optional<Account> findById(Integer id) {
        return Optional.ofNullable(accountMap.get(id));
    }

    /**
     * finds account by id with throwing NoSuchAccountFoundException if not found
     *
     * @param id account id
     * @return Account
     * @throws NoSuchAccountFoundException if account not found
     */
    @Override
    public Account getById(Integer id) throws NoSuchAccountFoundException {
        return findById(id)
                .orElseThrow(NoSuchAccountFoundException::new);
    }

    /**
     * deletes account by id
     *
     * @param id account id
     * @return result of method: false if account not found, otherwise true
     */
    @Override
    public boolean delete(Integer id) {
        if (findById(id).isEmpty()) return false;

        synchronized (this) {
            accountMap.remove(id);

            try (Connection connection = ConnectionManager.get();
                 PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {

                statement.setObject(1, id);
                statement.executeUpdate();

                return true;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * updates account using Account entity
     *
     * @param entity account with updated info
     * @return result of method: false if account not found, otherwise true
     */
    @Override
    public boolean update(Account entity) {
        if (findById(entity.getId()).isEmpty()) return false;
        Account account = accountMap.get(entity.getId());

        lockAccount(account);

        account.setCurrency(entity.getCurrency());
        account.setBalance(entity.getBalance());
        account.setBankId(entity.getBankId());
        account.setAppUserId(entity.getAppUserId());

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setObject(1, entity.getCurrency().name());
            statement.setObject(2, entity.getBalance());
            statement.setObject(3, entity.getBankId());
            statement.setObject(4, entity.getAppUserId());
            statement.setObject(5, entity.getId());
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            account.getLock().unlock();
        }
    }

    /**
     * saves account using Account entity
     *
     * @param entity account to save;
     * @return account with assigned id
     */
    @Override
    public synchronized Account save(Account entity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setObject(1, entity.getCurrency().name());
            statement.setObject(2, Timestamp.valueOf(entity.getOpeningDate()));
            statement.setObject(3, entity.getBalance());
            statement.setObject(4, entity.getBankId());
            statement.setObject(5, entity.getAppUserId());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            entity.setId(generatedKeys.getObject("id", Integer.class));

            accountMap.put(entity.getId(), entity);

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds Account from resultSet
     *
     * @param resultSet set whose parameters will be used to build account
     * @return new Account
     */
    private Account buildAccount(ResultSet resultSet) throws SQLException {
        return new Account(
                resultSet.getObject("id", Integer.class),
                Currency.valueOf(resultSet.getObject("currency", String.class)),
                resultSet.getObject("openingdate", Timestamp.class).toLocalDateTime(),
                resultSet.getObject("balance", Double.class),
                resultSet.getObject("bankid", Integer.class),
                resultSet.getObject("appuserid", Integer.class)
        );
    }
}
