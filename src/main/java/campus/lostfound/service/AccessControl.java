package campus.lostfound.service;

import campus.lostfound.model.User;
import java.sql.SQLException;

public final class AccessControl {
    private AccessControl(){}
    public static void requireAdmin(User user) throws SQLException {
        if(user==null || !"ADMIN".equals(user.getRole())) throw new SQLException("Administrator permission required.");
    }
    public static void requireStudent(User user) throws SQLException {
        if(user==null || !"STUDENT".equals(user.getRole())) throw new SQLException("Student account required.");
    }
}
