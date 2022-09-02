package pl.karol.jwt_based_app.user;

import pl.karol.jwt_based_app.task.Task;
import pl.karol.jwt_based_app.user.dto.UserDto;

import java.util.List;
import java.util.Optional;

public interface UserService {

     UserDto saveUser(UserDto createUserDto);

     Optional<UserDto> getUserByUsername(String username);
     Task addTaskToUser(String username, Task task);
     void toggleUserTask(String username, Long id);
     void deleteUserTaskById(String username, Long id);

     List<Task> findUserTasks(Long userId);

     void deleteUserById(Long userId);
}
