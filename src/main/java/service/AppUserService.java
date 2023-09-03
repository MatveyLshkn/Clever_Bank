package service;

import dao.AppUserDao;
import entity.AppUser;
import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AppUserService implements EntityService<AppUser> {
    private static final AppUserService INSTANCE = new AppUserService();
    private final AppUserDao appUserDao = AppUserDao.getInstance();

    private AppUserService() {
    }

    /**
     * @return Instance of class
     */
    public static AppUserService getInstance() {
        return INSTANCE;
    }

    /**
     * <p>Handles request and returns list of appUsers, or particular one.</p>
     *
     * @param req presence of parameter 'id' in servlet request is up to user
     * @return list of appUsers if parameter 'id' is empty or particular appUser if 'id' exists
     * @see AppUserDao
     */
    @Override
    public List<AppUser> get(HttpServletRequest req) {
        String id = req.getParameter("id");
        List<AppUser> appUsers;
        if (id == null || id.isEmpty()) {
            appUsers = appUserDao.findAll();
        } else {
            appUsers = new ArrayList<>();
            Optional<AppUser> appUser = appUserDao.findById(Integer.parseInt(id));
            appUser.ifPresent(appUsers::add);
        }
        return appUsers;
    }

    /**
     * <p>Handles request and saves new appUser.</p>
     *
     * @param req must include parameter 'fullname'
     * @return if there is no errors returns string "Successfully saved", otherwise error message
     * @see AppUserDao
     */
    @Override
    public String save(HttpServletRequest req) {
        AppUser appUser = new AppUser();

        String fullName = req.getParameter("fullname");
        appUser.setFullName(fullName);
        appUserDao.save(appUser);

        return "Successfully saved";
    }

    /**
     * <p>Handles request and deletes appUser.</p>
     *
     * @param req must include parameter 'id'
     * @return if there is no errors returns string "Successfully deleted", otherwise error message
     * @see AppUserDao
     */
    @Override
    public String delete(HttpServletRequest req) {
        Integer id = Integer.parseInt(req.getParameter("id"));
        return appUserDao.delete(id) ? "Successfully deleted" : "No such user found";
    }

    /**
     * <p>Handles request and updates appUser info.</p>
     *
     * @param req must include parameter 'id', 'fullname'
     * @return if there is no errors returns string "Successfully updated", otherwise error message
     * @see AppUserDao
     */
    @Override
    public String update(HttpServletRequest req) {
        AppUser appUser = new AppUser();
        StringBuilder message = new StringBuilder();

        Integer id = Integer.parseInt(req.getParameter("id"));

        appUserDao.findById(id).ifPresentOrElse(t -> appUser.setId(id),
                () -> message.append("No such user found"));

        if (!message.isEmpty()) return message.toString();
        String fullName = req.getParameter("fullname");
        appUser.setFullName(fullName);

        appUserDao.update(appUser);
        return "Successfully updated";
    }
}
