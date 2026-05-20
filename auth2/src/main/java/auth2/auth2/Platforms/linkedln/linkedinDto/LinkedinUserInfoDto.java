package auth2.auth2.Platforms.linkedln.linkedinDto;

public class LinkedinUserInfoDto {

    private String sub;
    private String name;
    private String email;

    public LinkedinUserInfoDto(String sub, String name, String email) {
        this.sub = sub;
        this.name = name;
        this.email = email;
    }

    public LinkedinUserInfoDto() {
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
