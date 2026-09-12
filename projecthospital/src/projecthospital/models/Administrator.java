package projecthospital.models;


public class Administrator extends User {

    
    public Administrator() {
        super();
        this.role = "ADMIN";
    }

    public Administrator(int userId, String username, String password,
                         String fullName, String email, String phone) {
        super(userId, username, password, "ADMIN", fullName, email, phone);
    }

    
    @Override
    public String getUserInfo() {
        return "Administrator: " + fullName + "\n" +
               "Username: " + username + "\n" +
               "Email: " + email + "\n" +
               "Phone: " + phone;
    }

    @Override
    public String toString() {
        return "Administrator{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
