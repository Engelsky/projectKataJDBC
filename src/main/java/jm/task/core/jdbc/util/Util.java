package jm.task.core.jdbc.util;

import jm.task.core.jdbc.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// реализация настройки соединения с БД
public class Util {
    // настройки подключения к БД

    // ===== JDBC константы =====
    // com.mysql.jdbc.Driver был предложен средой, но это же устаревшее
    // а у меня версия MySQL 8-ая, посему cj
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/katapp1134";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Bzkvn809";

    // ===== Hibernate =====
    private static SessionFactory sessionFactory;

    // Добавил приватный конструктор, так Util - утилитный класс, экземпляры создавать ненада
    private Util() {}

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Загружаем драйвер
            Class.forName(DB_DRIVER);
            // Получаем соединение, используя настройки
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            // для проверки коннекта наглядно
            System.out.println("Connected to database successfully");
        }  catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            System.out.println("Connection Failed! Check output console");
        }
        return conn;
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                // Настройки Hibernate
                // Весь пут решил вынести в отдельный метод getHibernateSettings()
                Properties settings = getHibernateSettings();

                configuration.setProperties(settings);

                // Добавляем наш класс-сущность
                configuration.addAnnotatedClass(User.class);

                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                        .applySettings(settings).build();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }

    private static Properties getHibernateSettings() {
        Properties settings = new Properties();
        settings.put(Environment.DRIVER, DB_DRIVER);
        settings.put(Environment.URL, DB_URL);
        settings.put(Environment.USER, DB_USER);
        settings.put(Environment.PASS, DB_PASSWORD);
        // указываем диалект для Hibernate, чтоб он понимал, что работаем с MySQL 8
        settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        settings.put(Environment.SHOW_SQL, "true"); // Показываем SQL в консоли
        settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
        settings.put(Environment.HBM2DDL_AUTO, ""); // IDEA предлагает update,
        // но по заданию мы создаем таблицы ручками SQL в DAO
        return settings;
    }
}
