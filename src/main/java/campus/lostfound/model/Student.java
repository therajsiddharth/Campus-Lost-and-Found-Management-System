package campus.lostfound.model;

public class Student extends User {
    public Student(int id, String name, String email) { super(id, name, email, "STUDENT"); }
    @Override public String dashboardTitle() { return "Student Portal"; }
}
