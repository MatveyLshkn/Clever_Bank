package servlet;

import entity.Bank;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.BankService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/banks")
public class BankServlet extends HttpServlet {
    private final BankService bankService = BankService.getInstance();

    /**
     * <p>Handles request and prints list of banks, or particular one.</p>
     * <p>If parameter 'id' is present in servlet request then method prints only one bank otherwise list of all banks.</p>
     * <p>Performs this operation by invoking 'get' method from BankService.</p>
     * <p>If there is no errors prints on webpage List of banks, or one particular bank
     * otherwise prints error message</p>
     *
     * @param req  presence of parameter 'id' in servlet request is up to user
     * @param resp servlet response
     * @see BankService
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        List<Bank> banks = bankService.get(req);
        if (!banks.isEmpty()) {
            banks.forEach(bank -> writer.write(bank + "<br><br>"));
        } else writer.write("No such bank found");
        writer.close();
    }

    /**
     * <p>Handles request and saves new bank.</p>
     * <p>Performs this operation by invoking 'save' method from BankService.</p>
     * <p>If there is no errors prints on webpage "Successfully saved".</p>
     *
     * @param req  must include parameter 'name'
     * @param resp servlet response
     * @see BankService
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(bankService.save(req));
        writer.close();
    }

    /**
     * <p>Handles request and updates bank info.</p>
     * <p>Performs this operation by invoking 'update' method from BankService.</p>
     * <p>If there is no errors prints on webpage "Successfully updated"
     * otherwise prints error message</p>
     *
     * @param req  must include parameters 'id','name'
     * @param resp servlet response
     * @see BankService
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(bankService.update(req));
        writer.close();
    }

    /**
     * <p>Handles request and deletes bank.</p>
     * <p>Performs this operation by invoking 'delete' method from BankService.</p>
     * <p>If there is no errors prints on webpage "Successfully deleted"
     * otherwise prints error message</p>
     *
     * @param req  must include parameter 'id'
     * @param resp servlet response
     * @see BankService
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(bankService.delete(req));
        writer.close();
    }
}
