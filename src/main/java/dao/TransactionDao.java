package dao;

import entity.Transaction;
import entity.TransactionType;
import exception.NoSuchTransactionFoundException;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class TransactionDao implements Dao<Integer, Transaction> {
    private static final TransactionDao INSTANCE = new TransactionDao();
    private final ConcurrentNavigableMap<Integer, Transaction> transactionMap = new ConcurrentSkipListMap<>();

    private static final String FIND_ALL_SQL = """
            SELECT * FROM transaction;
            """;

    private static final String DELETE_SQL = """
            DELETE FROM transaction
            WHERE id = ?;
            """;
    private static final String UPDATE_SQL = """
            UPDATE transaction
            SET date = ?
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO transaction(date, type, receiveraccid, senderaccid, total)
            VALUES(?,?,?,?,?) RETURNING id;
            """;


    {
        findAll().forEach(transaction -> transactionMap.put(transaction.getId(), transaction));
    }

    private TransactionDao() {
    }

    /**
     * @return Instance of class
     */
    public static TransactionDao getInstance() {
        return INSTANCE;
    }


    /**
     * @return List of all transactions
     */
    @Override
    public List<Transaction> findAll() {
        if (!transactionMap.isEmpty()) return transactionMap.values().stream().toList();

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<Transaction> transactions = new ArrayList<>();
            while (resultSet.next()) {
                transactions.add(buildTransaction(resultSet));
            }
            return transactions;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * finds transaction by id without throwing NoSuchTransactionFoundException if not found
     *
     * @param id transaction id
     * @return Optional of transaction. Optional.empty if transaction not found
     */
    @Override
    public Optional<Transaction> findById(Integer id) {
        return Optional.ofNullable(transactionMap.get(id));
    }

    /**
     * finds transaction by id with throwing NoSuchTransactionFoundException if not found
     *
     * @param id transaction id
     * @return Transaction
     * @throws NoSuchTransactionFoundException if transaction not found
     */
    @Override
    public Transaction getById(Integer id) throws NoSuchTransactionFoundException {
        return findById(id)
                .orElseThrow(NoSuchTransactionFoundException::new);
    }

    /**
     * deletes transaction by id
     *
     * @param id transaction id
     * @return result of method: false if transaction not found, otherwise true
     */
    @Override
    public boolean delete(Integer id) {
        if (findById(id).isEmpty()) return false;

        synchronized (this) {
            transactionMap.remove(id);

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
     * updates transaction using Transaction entity
     *
     * @param entity transaction with updated info: only date updates allowed!
     * @return result of method: false if transaction not found, otherwise true
     */
    @Override
    public boolean update(Transaction entity) {
        if (findById(entity.getId()).isEmpty()) return false;
        Transaction transaction = transactionMap.get(entity.getId());

        while (true) {
            if (transaction.getLock().tryLock()) break;
        }

        transaction.setDate(entity.getDate());

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setObject(1, entity.getDate());
            statement.setObject(2, entity.getId());
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            transaction.getLock().unlock();
        }
    }

    /**
     * saves transaction using Transaction entity
     *
     * @param entity transaction to save;
     * @return transaction with assigned id
     */
    @Override
    public synchronized Transaction save(Transaction entity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {

            statement.setObject(1, Timestamp.valueOf(entity.getDate()));
            statement.setObject(2, entity.getType().name());
            statement.setObject(3, entity.getReceiverAccId().orElse(null));
            statement.setObject(4, entity.getSenderAccId().orElse(null));
            statement.setObject(5, entity.getTotal());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();
            entity.setId(generatedKeys.getObject("id", Integer.class));

            transactionMap.put(entity.getId(), entity);

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds transaction from resultSet
     *
     * @param resultSet set whose parameters will be used to build transaction
     * @return new Transaction
     */
    private Transaction buildTransaction(ResultSet resultSet) throws SQLException {
        return new Transaction(resultSet.getObject("id", Integer.class),
                resultSet.getObject("date", Timestamp.class).toLocalDateTime(),
                TransactionType.valueOf(resultSet.getObject("type", String.class)),
                Optional.ofNullable(resultSet.getObject("receiveraccid", Integer.class)),
                Optional.ofNullable(resultSet.getObject("senderaccid", Integer.class)),
                resultSet.getObject("total", Double.class));
    }
}
