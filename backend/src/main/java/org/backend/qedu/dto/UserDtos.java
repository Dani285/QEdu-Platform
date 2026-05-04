package org.backend.qedu.dto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.backend.qedu.entities.User;
import org.backend.qedu.model.Roles;

public class UserDtos {
    public UserDtos(){

    }

    public record Message(String Message){}

    public record LoginRequests(@NotBlank String userName,
                                @NotBlank String Password){}

    public record RegisterRequest(@NotBlank String userName,
                                  @NotBlank String Password,
                                  @NotBlank String fullName,
                                  @NotNull Roles role,
                                  @NotBlank String classGroup){}
    public record UserDto(Long ID,
                          String userName,
                          String fullName,
                          Roles role,
                          String classGroup){
        public static UserDto from(User user){
          return new UserDto(
                  user.getID(),
                  user.getUserName(),
                  user.getFullName(),
                  user.getRoles(),
                  user.getClassGroups()
          );
        };
    }

    @NotNull(message = "Username is required to login")

    private String userName;

    @Email(message = "Invalid Email please enter a valid one")

    @NotNull(message = "Password is required to login")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotNull(message =  "Role is necessary")
    private Roles role;

    private String getUsername(){
        return userName;
    }
    private void setUsername(String userName){
        this.userName = userName;
    }
    private Roles getRole(){
        return role;
    }
    private void setRole(Roles role){
        this.role = role;
    }
    private String getPassword(){
        return password;
    }
    private void setPassword(String password) {
        this.password = password;
    }
}

