package service;

import dao.BankDao;
import entity.Bank;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BankService implements EntityService<Bank> {
    private static final BankService INSTANCE = new BankService();
    private final BankDao bankDao = BankDao.getInstance();

    private BankService() {
    }

    /**
     * @return Instance of class
     */
    public static BankService getInstance() {
        return INSTANCE;
    }

    /**
     * <p>Handles request and returns list of banks, or particular one.</p>
     *
     * @param req presence of parameter 'id' in servlet request is up to user
     * @return list of banks if parameter 'id' is empty or particular bank if 'id' exists
     * @see BankDao
     */
    @Override
    public List<Bank> get(HttpServletRequest req) {
        String id = req.getParameter("id");
        List<Bank> banks;
        if (id == null || id.isEmpty()) {
            banks = bankDao.findAll();
        } else {
            banks = new ArrayList<>();
            Optional<Bank> bank = bankDao.findById(Integer.parseInt(id));
            bank.ifPresent(banks::add);
        }
        return banks;
    }

    /**
     * <p>Handles request and saves new bank.</p>
     *
     * @param req must include parameter 'name'
     * @return if there is no errors returns string "Successfully saved", otherwise error message
     * @see BankDao
     */
    @Override
    public String save(HttpServletRequest req) {
        Bank bank = new Bank();

        String name = req.getParameter("name");
        bank.setName(name);
        bankDao.save(bank);

        return "Successfully saved";
    }

    /**
     * <p>Handles request and deletes bank.</p>
     *
     * @param req must include parameter 'id'
     * @return if there is no errors returns string "Successfully deleted", otherwise error message
     * @see BankDao
     */
    @Override
    public String delete(HttpServletRequest req) {
        Integer id = Integer.parseInt(req.getParameter("id"));
        return bankDao.delete(id) ? "Successfully deleted" : "No such bank found";
    }

    /**
     * <p>Handles request and updates bank info.</p>
     *
     * @param req must include parameter 'id', 'name'
     * @return if there is no errors returns string "Successfully updated", otherwise error message
     * @see BankDao
     */
    @Override
    public String update(HttpServletRequest req) {
        Bank bank = new Bank();
        StringBuilder message = new StringBuilder();

        Integer id = Integer.parseInt(req.getParameter("id"));

        bankDao.findById(id).ifPresentOrElse(t -> bank.setId(id),
                () -> message.append("No such bank found"));

        if (!message.isEmpty()) return message.toString();
        String name = req.getParameter("name");
        bank.setName(name);

        bankDao.update(bank);
        return "Successfully updated";
    }
}