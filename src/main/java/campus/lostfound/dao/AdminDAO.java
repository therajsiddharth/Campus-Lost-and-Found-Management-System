package campus.lostfound.dao;

import campus.lostfound.db.DB;
import java.sql.*;
import java.util.*;

public class AdminDAO {
    public List<Object[]> users() throws SQLException {
        List<Object[]> rows=new ArrayList<>();
        String sql="SELECT user_id,full_name,student_code,email,role,account_status,created_at FROM users ORDER BY user_id";
        try(Connection c=DB.getConnection();Statement s=c.createStatement();ResultSet r=s.executeQuery(sql)){
            while(r.next()) rows.add(new Object[]{r.getInt("user_id"),r.getString("full_name"),r.getString("student_code"),
                r.getString("email"),r.getString("role"),r.getString("account_status"),r.getTimestamp("created_at")});
        } return rows;
    }
    public void setUserStatus(int userId,String status,int adminId) throws SQLException {
        if(userId==adminId) throw new SQLException("You cannot deactivate your own admin account.");
        if(!"ACTIVE".equals(status)&&!"INACTIVE".equals(status)) throw new SQLException("Invalid account status.");
        try(Connection c=DB.getConnection()){
            c.setAutoCommit(false);
            try(PreparedStatement p=c.prepareStatement("UPDATE users SET account_status=? WHERE user_id=? AND role='STUDENT'")){
                p.setString(1,status);p.setInt(2,userId);
                if(p.executeUpdate()==0) throw new SQLException("Student account not found.");
            }
            try(PreparedStatement p=c.prepareStatement("INSERT INTO admin_logs(admin_id,action,target_type,target_id,details) VALUES(?,?,?,?,?)")){
                p.setInt(1,adminId);p.setString(2,"CHANGE_USER_STATUS");p.setString(3,"USER");p.setInt(4,userId);p.setString(5,status);p.executeUpdate();
            }
            c.commit();
        }
    }
    public List<Object[]> categories() throws SQLException {
        List<Object[]> rows=new ArrayList<>();
        try(Connection c=DB.getConnection();Statement s=c.createStatement();
            ResultSet r=s.executeQuery("SELECT category_id,category_name,active FROM categories ORDER BY category_name")){
            while(r.next()) rows.add(new Object[]{r.getInt(1),r.getString(2),r.getBoolean(3)});
        } return rows;
    }
    public void addCategory(String name) throws SQLException {
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("INSERT INTO categories(category_name) VALUES(?)")){
            p.setString(1,name.trim());p.executeUpdate();
        }
    }
    public void setCategoryActive(int id,boolean active) throws SQLException {
        try(Connection c=DB.getConnection();PreparedStatement p=c.prepareStatement("UPDATE categories SET active=? WHERE category_id=?")){
            p.setBoolean(1,active);p.setInt(2,id);p.executeUpdate();
        }
    }
}
