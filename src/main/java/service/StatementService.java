package service;

import exception.NoSuchAccountFoundException;
import exception.NoSuchUserFoundException;
import jakarta.servlet.http.HttpServletRequest;
import util.CheckPrinter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class StatementService {
    private static final StatementService INSTANCE = new StatementService();

    private StatementService() {
    }

    /**
     * @return Instance of class
     */
    public static StatementService getInstance() {
        return INSTANCE;
    }

    /**
     * <p>Handles request and prints money statement in statement-money folder.</p>
     *
     * @param req must include parameters:
     *            <p>'id';</p>
     *            <p>'from' date at format dd.MM.yyy;</p>
     *            <p>'to' date at format dd.MM.yyy.</p>
     * @return if there is no errors returns string "Statement successfully printed", otherwise error message
     * @see CheckPrinter
     */
    public String printStatement(HttpServletRequest req) {
        StringBuilder message = new StringBuilder();
        String id = req.getParameter("id");
        String from = req.getParameter("from");
        String to = req.getParameter("to");

        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("dd.MM.yyy_HH:mm:ss").toFormatter();
        LocalDateTime fromTime = LocalDateTime.from(formatter.parse(from + "_00:00:00"));
        LocalDateTime toTime = LocalDateTime.from(formatter.parse(to + "_00:00:00"));

        try {
            CheckPrinter.printMoneyStatement(Integer.parseInt(id), fromTime, toTime);
            message.append("Statement successfully printed");
        } catch (NoSuchAccountFoundException e) {
            message.append("No such account found ");
        } catch (NoSuchUserFoundException e) {
            message.append("No such user found");
        }
        return message.toString();
    }
}
