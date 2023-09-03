package servlet;

import entity.Account;
import entity.Currency;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AccountService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/accounts")
public class AccountServlet extends HttpServlet {
    private final AccountService accountService = AccountService.getInstance();

    /**
     * <p>Handles request and prints list of accounts, or particular one.</p>
     * <p>If parameter 'id' is present in servlet request then method prints only one account otherwise list of all accounts.</p>
     * <p>Performs this operation by invoking 'get' method from AccountService.</p>
     * <p>If there is no errors prints on webpage List of accounts, or one particular account
     * otherwise prints error message.</p>
     *
     * @param req  presence of parameter 'id' in servlet request is up to user
     * @param resp servlet response
     * @see AccountService
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        List<Account> accounts = accountService.get(req);
        if (!accounts.isEmpty()) {
            accounts.forEach(account -> writer.write(account + "<br><br>"));
        } else writer.write("No such account found");
        writer.close();
    }

    /**
     * <p>Handles request and saves new account.</p>
     * <p>Performs this operation by invoking 'save' method from AccountService.</p>
     * <p>If there is no errors prints on webpage "Successfully saved".</p>
     *
     * @param req  must include parameters:
     *             <p>'bankid';</p>
     *             <p>'userid';</p>
     *             <p>'currency' one of 3 currencies (BYN, USD, EUR);</p>
     *             <p>'balance';</p>
     *             <p>'openingdate' date of creation at format dd-MM-yyyy.</p>
     * @param resp servlet response
     * @see AccountService
     * @see Currency
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(accountService.save(req));
        writer.close();
    }

    /**
     * <p>Handles request and updates account.</p>
     * <p>Performs this operation by invoking 'update' method from AccountService.</p>
     * <p>If there is no errors prints on webpage "Successfully updated"
     * otherwise prints error message.</p>
     *
     * @param req  must include parameters:
     *             <p>'id';</p>
     *             <p>'bankid';</p>
     *             <p>'userid';</p>
     *             <p>'currency' one of 3 currencies (BYN, USD, EUR);</p>
     *             <p>'balance'.</p>
     * @param resp servlet response
     * @see AccountService
     * @see Currency
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(accountService.update(req));
        writer.close();
    }

    /**
     * <p>Handles request and deletes account.</p>
     * <p>Performs this operation by invoking 'delete' method from AccountService.</p>
     * <p>If there is no errors prints on webpage "Successfully deleted"
     * otherwise prints error message.</p>
     *
     * @param req  must include parameter 'id'
     * @param resp servlet response
     * @see AccountService
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(accountService.delete(req));
        writer.close();
    }
}
