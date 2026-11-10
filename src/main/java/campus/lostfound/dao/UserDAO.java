package campus.lostfound.dao;

import campus.lostfound.db.DB;
import campus.lostfound.model.*;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UserDAO {
    public User login(String email, String password) throws SQLException {
        String sql="SELECT user_id,full_name,email,password_hash,role,account_status FROM users WHERE email=?";
        try(Connection c=DB.getConnection(); PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,email);
            try(ResultSet r=p.executeQuery()){
                if(!r.next() || !"ACTIVE".equals(r.getString("account_status"))) return null;
                if(!BCrypt.checkpw(password,r.getString("password_hash"))) return null;
                int id=r.getInt("user_id"); String name=r.getString("full_name"); String em=r.getString("email");
                return "ADMIN".equals(r.getString("role")) ? new Admin(id,name,em) : new Student(id,name,em);
            }
        }
    }
    public void registerStudent(String name,String studentCode,String email,String password) throws SQLException {
        String sql="INSERT INTO users(full_name,student_code,email,password_hash,role) VALUES(?,?,?,?, 'STUDENT')";
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement(sql)){
            p.setString(1,name);p.setString(2,studentCode.isBlank()?null:studentCode);
            p.setString(3,email);p.setString(4,BCrypt.hashpw(password,BCrypt.gensalt(12)));p.executeUpdate();
        }
    }
    public boolean adminExists() throws SQLException {
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM users WHERE role='ADMIN'");
            ResultSet r=p.executeQuery()){r.next();return r.getInt(1)>0;}
    }
    public void createFirstAdmin(String name,String email,String password) throws SQLException {
        try(Connection c=DB.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement lock=c.prepareStatement("SELECT COUNT(*) FROM users WHERE role='ADMIN' FOR UPDATE");
                ResultSet r=lock.executeQuery()){
                r.next(); if(r.getInt(1)>0) throw new SQLException("An admin account already exists.");
            }
            try(PreparedStatement p=c.prepareStatement("INSERT INTO users(full_name,email,password_hash,role) VALUES(?,?,?,'ADMIN')")){
                p.setString(1,name);p.setString(2,email);p.setString(3,BCrypt.hashpw(password,BCrypt.gensalt(12)));p.executeUpdate();
            }
            c.commit();
        }
    }
}
