package campus.lostfound.model;

public abstract class User {
    private final int userId;
    private final String fullName;
    private final String email;
    private final String role;

    protected User(int userId, String fullName, String email, String role) {
        this.userId = userId; this.fullName = fullName; this.email = email; this.role = role;
    }
    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public abstract String dashboardTitle();
}
