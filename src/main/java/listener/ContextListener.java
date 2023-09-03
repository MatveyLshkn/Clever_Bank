package listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import thread.MoneyAccrualThread;
import util.ConnectionManager;

@WebListener
public class ContextListener implements ServletContextListener {
    private final MoneyAccrualThread moneyAccrualThread = new MoneyAccrualThread();

    /**
     * As soon as the server is started, the method starts MoneyAccrualThread
     *
     * @see MoneyAccrualThread
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        moneyAccrualThread.start();
        ServletContextListener.super.contextInitialized(sce);
    }

    /**
     * As soon as the server is stopped, the method stops MoneyAccrualThread and closes connection pool
     *
     * @see MoneyAccrualThread
     * @see ConnectionManager
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        moneyAccrualThread.interrupt();
        ConnectionManager.closePool();
        ServletContextListener.super.contextDestroyed(sce);
    }
}
