package dao;

import entity.Account;
import entity.Bank;
import entity.Transaction;
import entity.TransactionType;
import exception.InsufficientFundsException;
import exception.NoSuchAccountFoundException;
import exception.NoSuchBankFoundException;
import util.CheckPrinter;
import util.ConnectionManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class BankDao implements Dao<Integer, Bank> {
    private static final BankDao INSTANCE = new BankDao();
    private final ConcurrentNavigableMap<Integer, Bank> bankMap = new ConcurrentSkipListMap<>();
    private static final TransactionDao transactionDao = TransactionDao.getInstance();
    private static final AccountDao accountDao = AccountDao.getInstance();
    private static final String WITHDRAW_SQL = """
            UPDATE account
            SET balance = balance - ?
            WHERE id = ?;
            """;

    private static final String REFILL_SQL = """
            UPDATE account
            SET balance = balance + ?
            WHERE id = ?;
            """;

    private static final String TRANSFER_SQL = """
            BEGIN;
            %s
            %s
            COMMIT;
            """.formatted(WITHDRAW_SQL, REFILL_SQL);

    private static final String FIND_ALL_SQL = """
            SELECT *
            FROM bank;
            """;

    private static final String DELETE_SQL = """
            DELETE FROM bank
            WHERE id = ?;
            """;
    private static final String UPDATE_SQL = """
            UPDATE bank
            SET name = ?
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO bank(name)
            VALUES(?)
            RETURNING id;
            """;

    {
        findAll().forEach(bank -> bankMap.put(bank.getId(), bank));
    }

    private BankDao() {
    }

    /**
     * @return Instance of class
     */
    public static BankDao getInstance() {
        return INSTANCE;
    }

    /**
     * performs transfer operation between sender account and receiver account
     *
     * @param senderAccountId   id of senders account
     * @param receiverAccountId id of receivers account
     * @param amount            amount of money to be transferred
     * @return map with updated balances: senderBalance for sender and receiverBalance for receiver
     * @throws InsufficientFundsException  if sender account have less money that is required for the transfer
     * @throws NoSuchAccountFoundException if one of 2 accounts does not exist
     * @throws NoSuchBankFoundException    if bank does not exist
     */
    public Map<String, Double> transfer(Integer senderAccountId, Integer receiverAccountId, Double amount) throws InsufficientFundsException, NoSuchAccountFoundException, NoSuchBankFoundException {
        Account sender = accountDao.getById(senderAccountId);
        Account receiver = accountDao.getById(receiverAccountId);

        accountDao.lockAccounts(sender, receiver);

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(TRANSFER_SQL)) {
            Double senderBalance = sender.getBalance();
            Double receiverBalance = receiver.getBalance();

            if (senderBalance.compareTo(amount) < 0) throw new InsufficientFundsException();


            statement.setObject(1, amount);
            statement.setObject(2, senderAccountId);
            statement.setObject(3, amount);
            statement.setObject(4, receiverAccountId);
            statement.executeUpdate();

            Transaction transaction = new Transaction();
            transaction.setDate(LocalDateTime.now());
            transaction.setType(TransactionType.TRANSFER);
            transaction.setReceiverAccId(Optional.of(receiverAccountId));
            transaction.setSenderAccId(Optional.of(senderAccountId));
            transaction.setTotal(amount);

            CheckPrinter.printReceipt(transactionDao.save(transaction));

            Map<String, Double> balance = new HashMap<>();
            balance.put("senderBalance", senderBalance - amount);
            balance.put("receiverBalance", receiverBalance + amount);
            return balance;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            sender.getLock().unlock();
            receiver.getLock().unlock();
        }
    }

    /**
     * performs transfer operation between sender account and receiver account
     *
     * @param amount    amount of money to be refilled
     * @param accountId id of receivers account
     * @return updated balance
     * @throws NoSuchAccountFoundException if one of 2 accounts does not exist
     * @throws NoSuchBankFoundException    if bank does not exist
     */
    public Double refill(Double amount, Integer accountId) throws NoSuchAccountFoundException, NoSuchBankFoundException {
        Account account = accountDao.getById(accountId);

        accountDao.lockAccount(account);

        Double balance = account.getBalance();

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(REFILL_SQL)) {
            statement.setObject(1, amount);
            statement.setObject(2, accountId);
            statement.executeUpdate();

            Transaction transaction = new Transaction();
            transaction.setDate(LocalDateTime.now());
            transaction.setType(TransactionType.REFILL);
            transaction.setReceiverAccId(Optional.of(accountId));
            transaction.setSenderAccId(Optional.empty());
            transaction.setTotal(amount);


            CheckPrinter.printReceipt(transactionDao.save(transaction));

            return balance + amount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            account.getLock().unlock();
        }
    }

    /**
     * performs transfer operation between sender account and receiver account
     *
     * @param amount    amount of money to be withdrawn
     * @param accountId id of senders account
     * @return updated balance
     * @throws NoSuchAccountFoundException if one of 2 accounts does not exist
     * @throws NoSuchBankFoundException    if bank does not exist
     * @throws InsufficientFundsException  if sender account have less money that is required
     */
    public Double withdraw(Double amount, Integer accountId) throws NoSuchAccountFoundException, InsufficientFundsException, NoSuchBankFoundException {
        Account account = accountDao.getById(accountId);

        accountDao.lockAccount(account);


        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(WITHDRAW_SQL)) {
            Double balance = account.getBalance();

            if (balance.compareTo(amount) < 0) throw new InsufficientFundsException();


            statement.setObject(1, amount);
            statement.setObject(2, accountId);
            statement.executeUpdate();

            Transaction transaction = new Transaction();
            transaction.setDate(LocalDateTime.now());
            transaction.setType(TransactionType.WITHDRAW);
            transaction.setReceiverAccId(Optional.empty());
            transaction.setSenderAccId(Optional.of(accountId));
            transaction.setTotal(amount);

            CheckPrinter.printReceipt(transactionDao.save(transaction));

            return balance - amount;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            account.getLock().unlock();
        }
    }

    /**
     * @return List of all Banks
     */
    @Override
    public List<Bank> findAll() {
        if (!bankMap.isEmpty()) return bankMap.values().stream().toList();

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Bank> banks = new ArrayList<>();
            while (resultSet.next()) {
                banks.add(buildBank(resultSet));
            }
            return banks;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * finds bank by id without throwing NoSuchBankFoundException if not found
     *
     * @param id bank id
     * @return Optional of Bank. Optional.empty if bank not found
     */
    @Override
    public Optional<Bank> findById(Integer id) {
        return Optional.ofNullable(bankMap.get(id));
    }

    /**
     * finds bank by id with throwing NoSuchBankFoundException if not found
     *
     * @param id bank id
     * @return Bank
     * @throws NoSuchBankFoundException if bank not found
     */
    @Override
    public Bank getById(Integer id) throws NoSuchBankFoundException {
        return findById(id)
                .orElseThrow(NoSuchBankFoundException::new);
    }

    /**
     * deletes bank by id
     *
     * @param id bank id
     * @return result of method: false if bank not found, otherwise true
     */
    @Override
    public boolean delete(Integer id) {
        if (findById(id).isEmpty()) return false;

        synchronized (this) {
            bankMap.remove(id);

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
     * updates bank using Bank entity
     *
     * @param entity bank with updated info
     * @return result of method: false if bank not found, otherwise true
     */
    @Override
    public boolean update(Bank entity) {
        if (findById(entity.getId()).isEmpty()) return false;
        Bank bank = bankMap.get(entity.getId());

        while (true) {
            if (bank.getLock().tryLock()) break;
        }

        bankMap.get(entity.getId()).setName(entity.getName());

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setObject(1, entity.getName());
            statement.setObject(2, entity.getId());
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            bank.getLock().unlock();
        }
    }

    /**
     * saves bank using Bank entity
     *
     * @param entity bank to save;
     * @return bank with assigned id
     */
    @Override
    public synchronized Bank save(Bank entity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, entity.getName());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            entity.setId(generatedKeys.getObject("id", Integer.class));

            bankMap.put(entity.getId(), entity);

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds Bank from resultSet
     *
     * @param resultSet set whose parameters will be used to build bank
     * @return new Bank
     */
    private Bank buildBank(ResultSet resultSet) throws SQLException {
        return new Bank(resultSet.getObject("id", Integer.class),
                resultSet.getObject("name", String.class));
    }
}
