package mate.academy.jvbookstore.config;

import org.testcontainers.containers.MySQLContainer;

public class CustomMySqlContainer extends MySQLContainer<CustomMySqlContainer> {
    private static final String DB_IMAGE = "mysql:8";

    private static CustomMySqlContainer mySqlContainer;

    private CustomMySqlContainer() {
        super(DB_IMAGE);
    }

    public static synchronized CustomMySqlContainer getInstance() {
        if (mySqlContainer == null) {
            mySqlContainer = new CustomMySqlContainer();
        }
        return mySqlContainer;
    }

    public void start() {
        System.setProperty("TEST_DB_URL", mySqlContainer.getJdbcUrl());
        System.setProperty("TEST_DB_USERNAME", mySqlContainer.getUsername());
        System.setProperty("TEST_DB_PASSWORD", mySqlContainer.getPassword());
        mySqlContainer.setCommand("--host-cache-size=0");
    }

    public void stop() {

    }
}
