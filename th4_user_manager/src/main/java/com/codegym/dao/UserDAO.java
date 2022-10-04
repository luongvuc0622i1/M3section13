package com.codegym.dao;

import com.codegym.model.User;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO implements IUserDAO {
    private static final String jdbcURL = "jdbc:mysql://localhost:3306/demo?useSSL=false";
    private static final String jdbcUsername = "root";
    private static final String jdbcPassword = "22072022";

    private static final String INSERT_USERS_SQL = "INSERT INTO users (name, email, country) VALUES (?, ?, ?);";
    private static final String SELECT_USER_BY_ID = "select id,name,email,country from users where id =?";
    private static final String SELECT_ALL_USERS = "select * from users";
    private static final String DELETE_USERS_SQL = "delete from users where id = ?;";
    private static final String UPDATE_USERS_SQL = "update users set name = ?,email= ?, country =? where id = ?;";
    private static final String SQL_INSERT = "INSERT INTO employee (name, salary, created_date) VALUES (?, ?, ?)";
    private static final String SQL_UPDATE = "UPDATE employee SET salary=? WHERE name=?";
    private static final String SQL_TABLE_CREATE = "CREATE TABLE employee"
            + "("
            + "id           serial          primary key,"
            + "name         varchar(100)    NOT NULL,"
            + "salary       numeric(15,2)   NOT NULL,"
            + "created_date  timestamp"
            + ")";
    private static final String SQL_TABLE_DROP = "DROP TABLE IF EXISTS employee";

    public UserDAO() {
    }

    private Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            c = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public void insertUser(User user) throws SQLException {
        Connection c = getConnection();
        try {
            PreparedStatement ps = c.prepareStatement(INSERT_USERS_SQL);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getCountry());
            ps.executeUpdate();
        } catch (SQLException e) {
            printSQLException(e);
        }
    }

    @Override
    public User selectUser(int id) {
        User user = null;
        Connection c = getConnection();
        try {
            PreparedStatement ps = c.prepareStatement(SELECT_USER_BY_ID);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                user = new User(name, email, country);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return user;
    }

    @Override
    public List<User> selectAllUsers() {
        List<User> users = new ArrayList<>();
        Connection c = getConnection();
        try {
            PreparedStatement ps = c.prepareStatement(SELECT_ALL_USERS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String country = rs.getString("country");
                User user = new User(id, name, email, country);
                users.add(user);
            }
        } catch (SQLException e) {
            printSQLException(e);
        }
        return users;
    }

    @Override
    public boolean deleteUser(int id) throws SQLException {
        boolean result;
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement(DELETE_USERS_SQL);
        ps.setInt(1, id);
        result = ps.executeUpdate() > 0;
        return result;
    }

    @Override
    public boolean updateUser(User user) throws SQLException {
        boolean result;
        Connection c = getConnection();
        PreparedStatement ps = c.prepareStatement(UPDATE_USERS_SQL);
        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getCountry());
        ps.setInt(4, user.getId());
        result = ps.executeUpdate() > 0;
        return result;
    }

    @Override
    public void insertUpdateUseTransaction() {
        Connection c = getConnection();
        try {
            Statement s = c.createStatement();
            PreparedStatement psInsert = c.prepareStatement(SQL_INSERT);
            PreparedStatement psUpdate = c.prepareStatement(SQL_UPDATE);
            s.execute(SQL_TABLE_DROP);
            s.execute(SQL_TABLE_CREATE);

            c.setAutoCommit(false);

            psInsert.setString(1, "Quynh");
            psInsert.setBigDecimal(2, new BigDecimal(10));
            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            psInsert.execute();

            psInsert.setString(1, "Ngan");
            psInsert.setBigDecimal(2, new BigDecimal(20));
            psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            psInsert.execute();

            psUpdate.setBigDecimal(1, new BigDecimal(999.99));
            psUpdate.setString(2, "Quynh");
            psUpdate.execute();

            c.commit();

            c.setAutoCommit(true);

        } catch (Exception e) {

            System.out.println(e.getMessage());

            e.printStackTrace();

        }
    }

    private void printSQLException(SQLException ex) {
        for (Throwable e : ex) {
            if (e instanceof SQLException) {
                e.printStackTrace(System.err);
                System.err.println("SQLState: " + ((SQLException) e).getSQLState());
                System.err.println("Error Code: " + ((SQLException) e).getErrorCode());
                System.err.println("Message: " + e.getMessage());
                Throwable t = ex.getCause();
                while (t != null) {
                    System.out.println("Cause: " + t);
                    t = t.getCause();
                }
            }
        }
    }
}
