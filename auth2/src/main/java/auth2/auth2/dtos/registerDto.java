package auth2.auth2.dtos;


import auth2.auth2.users.UsersRoles;

public class registerDto {


        private String userName;
        private String userEmail;
        private String password;
        private UsersRoles userRole;

    public registerDto() {
    }

    public registerDto(String userName, String userEmail, String password,UsersRoles userRole) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.password = password;
        this.userRole= userRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UsersRoles getUserRole() {

        return userRole;
    }

    public void setUserRole(UsersRoles userRole) {
        this.userRole = userRole;
    }
}
