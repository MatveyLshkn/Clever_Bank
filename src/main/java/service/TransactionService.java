package service;

import dao.AccountDao;
import dao.TransactionDao;
import entity.Transaction;
import entity.TransactionType;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionService implements EntityService<Transaction> {
    private static final TransactionService INSTANCE = new TransactionService();
    private final TransactionDao transactionDao = TransactionDao.getInstance();
    private final AccountDao accountDao = AccountDao.getInstance();

    private TransactionService() {
    }

    /**
     * @return Instance of class
     */
    public static TransactionService getInstance() {
        return INSTANCE;
    }

    /**
     * <p>Handles request and returns list of transactions, or particular one.</p>
     *
     * @param req presence of parameter 'id' in servlet request is up to user
     * @return list of transactions if parameter 'id' is empty or particular transaction if 'id' exists
     * @see TransactionDao
     */
    @Override
    public List<Transaction> get(HttpServletRequest req) {
        String id = req.getParameter("id");
        List<Transaction> transactions;
        if (id == null || id.isEmpty()) {
            transactions = transactionDao.findAll();
        } else {
            transactions = new ArrayList<>();
            Optional<Transaction> transaction = transactionDao.findById(Integer.parseInt(id));
            transaction.ifPresent(transactions::add);
        }
        return transactions;
    }

    /**
     * <p>Handles request and saves new transaction.</p>
     *
     * @param req must include parameters:
     *            <p>'receiveracc' account id;</p>
     *            <p>'senderacc' account id;</p>
     *            <p>'date' at format dd-MM-yyyy_HH:mm:ss;</p>
     *            <p>'type' one of 3 types from TransactionType: (TRANSFER, REFILL, WITHDRAW);</p>
     *            <p>'total'.</p>
     * @return if there is no errors returns string "Successfully saved", otherwise error message
     * @see TransactionDao
     * @see TransactionType
     */
    @Override
    public String save(HttpServletRequest req) {
        Transaction transaction = new Transaction();
        StringBuilder message = new StringBuilder();

        Integer receiverAccId = Integer.parseInt(req.getParameter("receiveracc"));
        Integer senderAccId = Integer.parseInt(req.getParameter("senderacc"));

        accountDao.findById(receiverAccId).ifPresentOrElse(account -> transaction.setReceiverAccId(Optional.of(receiverAccId)),
                () -> message.append("No such receiver account found "));

        accountDao.findById(senderAccId).ifPresentOrElse(account -> transaction.setSenderAccId(Optional.of(senderAccId)),
                () -> message.append("No such sender account found"));

        if (!message.isEmpty()) return message.toString();

        LocalDateTime date = LocalDateTime.from(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss")
                .parse(req.getParameter("date")));
        TransactionType type = TransactionType.valueOf(req.getParameter("type").toUpperCase());
        Double total = Double.parseDouble(req.getParameter("total"));

        transaction.setDate(date);
        transaction.setType(type);
        transaction.setTotal(total);

        transactionDao.save(transaction);
        return "Successfully saved";
    }

    /**
     * <p>Handles request and deletes transaction.</p>
     *
     * @param req must include parameter 'id'
     * @return if there is no errors returns string "Successfully deleted", otherwise error message
     * @see TransactionDao
     */
    @Override
    public String delete(HttpServletRequest req) {
        Integer id = Integer.parseInt(req.getParameter("id"));
        return transactionDao.delete(id) ? "Successfully deleted" : "No such transaction found";
    }

    /**
     * <p>Handles request and updates transaction info.</p>
     *
     * @param req must include parameters 'id', 'date' at format dd-MM-yyyy_HH:mm:ss
     * @return if there is no errors returns string "Successfully updated", otherwise error message
     * @see TransactionDao
     * @see TransactionType
     */
    @Override
    public String update(HttpServletRequest req) {
        Transaction transaction = new Transaction();
        StringBuilder message = new StringBuilder();

        Integer id = Integer.parseInt(req.getParameter("id"));

        transactionDao.findById(id).ifPresentOrElse(t -> transaction.setId(id),
                () -> message.append("No such transaction found "));

        if (!message.isEmpty()) return message.toString();
        LocalDateTime date = LocalDateTime.from(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss")
                .parse(req.getParameter("date")));
        transaction.setDate(date);

        transactionDao.update(transaction);
        return "Successfully updated";
    }
}
