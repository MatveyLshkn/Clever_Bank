package servlet;

import entity.Transaction;
import entity.TransactionType;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.TransactionService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/transactions")
public class TransactionServlet extends HttpServlet {
    private final TransactionService transactionService = TransactionService.getInstance();

    /**
     * <p>Handles request and prints list of transactions, or particular one.</p>
     * <p>If parameter 'id' is present in servlet request then method prints only one transaction otherwise list of all transactions.</p>
     * <p>Performs this operation by invoking 'get' method from TransactionService.</p>
     * <p>If there is no errors prints on webpage List of transactions, or one particular transaction
     * otherwise prints error message</p>
     *
     * @param req  presence of parameter 'id' in servlet request is up to user
     * @param resp servlet response
     * @see TransactionService
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        List<Transaction> transactions = transactionService.get(req);
        if (!transactions.isEmpty()) {
            transactions.forEach(transaction -> writer.write(transaction + "<br><br>"));
        } else writer.write("No such account found");
        writer.close();
    }

    /**
     * <p>Handles request and saves new transaction.</p>
     * <p>Performs this operation by invoking 'save' method from TransactionService.</p>
     * <p>If there is no errors prints on webpage "Successfully saved".</p>
     *
     * @param req  must include parameters:
     *             <p>'receiveracc' account id;</p>
     *             <p>'senderacc' account id;</p>
     *             <p>'date' at format dd-MM-yyyy_HH:mm:ss;</p>
     *             <p>'type' one of 3 types from TransactionType: (TRANSFER, REFILL, WITHDRAW);</p>
     *             <p>'total'.</p>
     * @param resp servlet response
     * @see TransactionService
     * @see TransactionType
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(transactionService.save(req));
        writer.close();
    }

    /**
     * <p>Handles request and updates transaction info.</p>
     * <p>Performs this operation by invoking 'update' method from TransactionService.</p>
     * <p>If there is no errors prints on webpage "Successfully updated"
     * otherwise prints error message</p>
     *
     * @param req  must include parameters 'id', 'date' at format dd-MM-yyyy_HH:mm:ss
     * @param resp servlet response
     * @see TransactionService
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(transactionService.update(req));
        writer.close();
    }

    /**
     * <p>Handles request and deletes transaction.</p>
     * <p>Performs this operation by invoking 'delete' method from TransactionService.</p>
     * <p> If there is no errors prints on webpage "Successfully deleted"
     * otherwise prints error message</p>
     *
     * @param req  must include parameter 'id'
     * @param resp servlet response
     * @see TransactionService
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(transactionService.delete(req));
        writer.close();
    }
}
