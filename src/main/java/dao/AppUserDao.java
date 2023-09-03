package dao;

import entity.AppUser;
import exception.NoSuchUserFoundException;
import util.ConnectionManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class AppUserDao implements Dao<Integer, AppUser> {
    private static final AppUserDao INSTANCE = new AppUserDao();
    private final ConcurrentNavigableMap<Integer, AppUser> appUserMap = new ConcurrentSkipListMap<>();
    private static final String FIND_ALL_SQL = """
            SELECT *
            FROM appuser;
            """;
    private static final String DELETE_SQL = """
            DELETE
            FROM appuser
            WHERE id = ?;
            """;
    private static final String UPDATE_SQL = """
            UPDATE appuser
            SET fullname = ?
            WHERE id = ?;
            """;
    private static final String SAVE_SQL = """
            INSERT INTO appuser(fullname)
            VALUES(?)
            RETURNING id;
            """;

    {
        findAll().forEach(appUser -> appUserMap.put(appUser.getId(), appUser));
    }

    private AppUserDao() {
    }

    /**
     * @return Instance of class
     */
    public static AppUserDao getInstance() {
        return INSTANCE;
    }

    /**
     * @return List of all AppUsers
     */
    @Override
    public List<AppUser> findAll() {
        if (!appUserMap.isEmpty()) return appUserMap.values().stream().toList();

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL)) {
            ResultSet resultSet = statement.executeQuery();
            List<AppUser> appUsers = new ArrayList<>();
            while (resultSet.next()) {
                appUsers.add(buildAppUser(resultSet));
            }
            return appUsers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * finds appUser by id without throwing NoSuchAppUserFoundException if not found
     *
     * @param id transaction id
     * @return Optional of AppUser. Optional.empty if appUser not found
     */
    @Override
    public Optional<AppUser> findById(Integer id) {
        return Optional.ofNullable(appUserMap.get(id));
    }

    /**
     * finds appUser by id with throwing NoSuchAppUserFoundException if not found
     *
     * @param id appUser id
     * @return AppUser
     * @throws NoSuchUserFoundException if appUser not found
     */
    @Override
    public AppUser getById(Integer id) throws NoSuchUserFoundException {
        return findById(id)
                .orElseThrow(NoSuchUserFoundException::new);
    }

    /**
     * deletes appUser by id
     *
     * @param id transaction id
     * @return result of method: false if appUser not found, otherwise true
     */
    @Override
    public boolean delete(Integer id) {
        if (findById(id).isEmpty()) return false;

        synchronized (this) {
            appUserMap.remove(id);

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
     * updates appUser using AppUser entity
     *
     * @param entity appUser with updated info
     * @return result of method: false if appUser not found, otherwise true
     */
    @Override
    public boolean update(AppUser entity) {
        if (findById(entity.getId()).isEmpty()) return false;
        AppUser appUser = appUserMap.get(entity.getId());

        while (true) {
            if (appUser.getLock().tryLock()) break;
        }

        appUser.setFullName(entity.getFullName());

        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {

            statement.setObject(1, entity.getFullName());
            statement.setObject(2, entity.getId());
            statement.executeUpdate();

            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            appUser.getLock().unlock();
        }
    }

    /**
     * saves appUser using AppUser entity
     *
     * @param entity appUser to save;
     * @return appUser with assigned id
     */
    @Override
    public synchronized AppUser save(AppUser entity) {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setObject(1, entity.getFullName());
            statement.executeUpdate();
            ResultSet generatedKeys = statement.getGeneratedKeys();
            generatedKeys.next();

            entity.setId(generatedKeys.getObject("id", Integer.class));

            appUserMap.put(entity.getId(), entity);

            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds AppUser from resultSet
     *
     * @param resultSet set whose parameters will be used to build appUser
     * @return new AppUser
     */
    private AppUser buildAppUser(ResultSet resultSet) throws SQLException {
        return new AppUser(resultSet.getObject("id", Integer.class),
                resultSet.getObject("fullname", String.class));
    }
}
