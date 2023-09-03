package servlet;

import entity.AppUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.AppUserService;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/users")
public class AppUserServlet extends HttpServlet {
    private final AppUserService appUserService = AppUserService.getInstance();

    /**
     * <p>Handles request and prints list of appUsers, or particular one.</p>
     * <p>If parameter 'id' is present in servlet request then method prints only one appUser otherwise list of all appUsers.</p>
     * <p>Performs this operation by invoking 'get' method from AppUserService.</p>
     * <p>If there is no errors prints on webpage List of appUsers, or one particular appUser
     * otherwise prints error message.</p>
     *
     * @param req  presence of parameter 'id' in servlet request is up to user
     * @param resp servlet response
     * @see AppUserService
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        List<AppUser> appUsers = appUserService.get(req);
        if (!appUsers.isEmpty()) {
            appUsers.forEach(appUser -> writer.write(appUser + "<br><br>"));
        } else writer.write("No such user found");
        writer.close();
    }

    /**
     * <p>Handles request and saves new appUser.</p>
     * <p>Performs this operation by invoking 'save' method from AppUserService.</p>
     * <p>If there is no errors prints on webpage "Successfully saved".</p>
     *
     * @param req  must include parameter 'fullname'
     * @param resp servlet response
     * @see AppUserService
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(appUserService.save(req));
        writer.close();
    }

    /**
     * <p>Handles request and updates appUser info.</p>
     * <p>Performs this operation by invoking 'update' method from AppUserService.</p>
     * <p>If there is no errors prints on webpage "Successfully updated"
     * otherwise prints error message.</p>
     *
     * @param req  must include parameters 'id', 'fullname'
     * @param resp servlet response
     * @see AppUserService
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(appUserService.update(req));
        writer.close();
    }

    /**
     * <p>Handles request and deletes appUser.</p>
     * <p>Performs this operation by invoking 'delete' method from AppUserService.</p>
     * <p>If there is no errors prints on webpage "Successfully deleted"
     * otherwise prints error message.</p>
     *
     * @param req  must include parameter 'id'
     * @param resp servlet response
     * @see AppUserService
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = resp.getWriter();
        writer.write(appUserService.delete(req));
        writer.close();
    }
}
