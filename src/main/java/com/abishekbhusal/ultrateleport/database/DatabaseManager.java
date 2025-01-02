package com.abishekbhusal.ultrateleport.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {
    private HikariDataSource dataSource;

    public DatabaseManager(String databaseFilePath) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databaseFilePath);
        config.setMaximumPoolSize(10);
        this.dataSource = new HikariDataSource(config);

        initializeDatabase();
    }

    private void initializeDatabase() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            String createTableQuery = """
                    CREATE TABLE IF NOT EXISTS ultrateleport (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        uuid TEXT NOT NULL,
                        location TEXT NOT NULL,
                        name TEXT NOT NULL
                    );
                    """;

            statement.execute(createTableQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insertData(UUID uuid, String location, String name) {
        String query = "INSERT INTO ultrateleport (uuid, location, name) VALUES (?, ?, ?);";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, location);
            statement.setString(3, name);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteData(UUID uuid, String name) {
        String query = "DELETE FROM ultrateleport WHERE uuid = ? AND name = ?;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, name);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> listLocationsAndNames(UUID uuid) {
        List<String> result = new ArrayList<>();
        String query = "SELECT location, name FROM ultrateleport WHERE uuid = ?;";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String location = resultSet.getString("location");
                    String name = resultSet.getString("name");
                    result.add(location + " - " + name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public int countEntries(UUID uuid) {
        String query = "SELECT COUNT(*) AS count FROM ultrateleport WHERE uuid = ?;";
        int count = 0;

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public void close() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
