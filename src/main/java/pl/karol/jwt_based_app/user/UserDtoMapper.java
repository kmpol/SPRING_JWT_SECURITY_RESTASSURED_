package pl.karol.jwt_based_app.user;

import pl.karol.jwt_based_app.user.dto.UserDto;

public class UserDtoMapper {

    public static User map(UserDto userDto){
        User user = new User();
        user.setId(userDto.getId());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setUsername(userDto.getUsername());
        user.setPassword(userDto.getPassword());
        user.setRoles(userDto.getRoles());
        user.setTasks(userDto.getTasks());
        return user;
    }

    public static UserDto map(User user){
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setUsername(user.getUsername());
        userDto.setPassword(user.getPassword());
        userDto.setRoles(user.getRoles());
        userDto.setTasks(user.getTasks());
        return userDto;
    }
}
