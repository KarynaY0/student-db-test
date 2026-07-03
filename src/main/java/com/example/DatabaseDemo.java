package com.example;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDemo {

    private static final String DB_URL = "jdbc:sqlite:student.db";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            System.out.println("Connected to student.db");

            runScript(conn, "create.sql");
            runScript(conn, "insert.sql");
            runScript(conn, "query.sql");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void runScript(Connection conn, String resourceName) throws Exception {
        System.out.println("\n===== Running " + resourceName + " =====");

        String content = readResource(resourceName);
        List<String> statements = splitStatements(content);

        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                if (sql.isBlank()) continue;

                if (isSelect(sql)) {
                    try (ResultSet rs = stmt.executeQuery(sql)) {
                        System.out.println("\n--- Query ---\n" + sql);
                        printResultSet(rs);
                    }
                } else {
                    stmt.execute(sql);
                    System.out.println("\n--- Executed ---\n" + sql);
                }
            }
        }
    }

    private static boolean isSelect(String sql) {
        return sql.trim().toUpperCase().startsWith("SELECT");
    }

    private static String readResource(String name) throws Exception {
        Path path = Path.of("src/main/resources", name);
        return Files.readString(path);
    }

    private static List<String> splitStatements(String script) {
        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();

        for (String line : script.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith("--") || trimmed.isEmpty()) continue;

            current.append(line).append("\n");
            if (trimmed.endsWith(";")) {
                String statement = current.toString().trim();
                statements.add(statement.substring(0, statement.length() - 1));
                current.setLength(0);
            }
        }

        if (!current.toString().isBlank()) {
            statements.add(current.toString().trim());
        }

        return statements;
    }

    private static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        StringBuilder header = new StringBuilder();
        for (int i = 1; i <= columnCount; i++) {
            header.append(meta.getColumnName(i));
            if (i < columnCount) header.append(" | ");
        }
        System.out.println(header);
        System.out.println("-".repeat(header.length()));

        boolean hasRows = false;
        while (rs.next()) {
            hasRows = true;
            StringBuilder row = new StringBuilder();
            for (int i = 1; i <= columnCount; i++) {
                row.append(rs.getString(i));
                if (i < columnCount) row.append(" | ");
            }
            System.out.println(row);
        }

        if (!hasRows) {
            System.out.println("(no rows)");
        }
    }
}
