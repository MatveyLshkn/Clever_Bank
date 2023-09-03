package servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.StatementService;
import util.CheckPrinter;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet("/statement")
public class MoneyStatementServlet extends HttpServlet {
    private final StatementService statementService = StatementService.getInstance();

    /**
     * <p>Handles request and prints money statement in statement-money folder.</p>
     * <p>If there is no errors prints on webpage "Statement successfully printed"
     * otherwise prints error message.</p>
     * @param req must include parameters:
     *            <p>'id';</p>
     *            <p>'from' date at format dd.MM.yyy;</p>
     *            <p>'to' date at format dd.MM.yyy.</p>
     * @param resp servlet response
     * @see StatementService
     * @see CheckPrinter
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(statementService.printStatement(req));
        writer.close();
    }
}
