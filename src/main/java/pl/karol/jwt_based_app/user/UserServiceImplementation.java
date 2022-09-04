package pl.karol.jwt_based_app.user;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.karol.jwt_based_app.exception.ApiBadRequestException;
import pl.karol.jwt_based_app.role.Role;
import pl.karol.jwt_based_app.role.RoleRepository;
import pl.karol.jwt_based_app.task.Task;
import pl.karol.jwt_based_app.task.TaskRepository;
import pl.karol.jwt_based_app.user.dto.UserDto;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImplementation implements UserService, UserDetailsService {

    private final String USER_ROLE = "USER";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TaskRepository taskRepository;

    private final PasswordEncoder passwordEncoder;


    public UserServiceImplementation(UserRepository userRepository, RoleRepository roleRepository, TaskRepository taskRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Optional<User> user = userRepository.findByUsername(username);
       if(user.isEmpty()) throw new UsernameNotFoundException("User not found in db!");
       User userDb = user.get();
       Collection<SimpleGrantedAuthority> authorities = userDb.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toSet());
       return new org.springframework.security.core.userdetails.User(userDb.getUsername(), userDb.getPassword(), authorities);
    }

    @Override
    @Transactional
    public UserDto saveUser(UserDto createUserDto) {
        User user = UserDtoMapper.map(createUserDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Optional<Role> userRole = roleRepository.findByName(USER_ROLE);
        userRole.ifPresentOrElse(role -> user.getRoles().add(role),
                        () -> {
                    throw new NoSuchElementException();
                });
        User savedUser = userRepository.save(user);
        return UserDtoMapper.map(savedUser);
    }

    public Optional<UserDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username).map(UserDtoMapper::map);
    }

    @Override
    public Task findTaskById(String username, Long taskId){
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Task> taskOptional = taskRepository.findById(taskId);

        if(userOptional.isPresent() && taskOptional.isPresent()){
            User user = userOptional.get();
            Task task = taskOptional.get();
            if (checkIfUserIsOwnerOfTheTask(user, task)){
                return task;
            }else {
                throw new ApiBadRequestException("You are not owner of this task");
            }
        } else {
            throw new ApiBadRequestException("User or task not found");
        }
    }

    @Transactional
    public Task addTaskToUser(String username, Task task){
        Optional<User> user = userRepository.findByUsername(username);
        task.setOwner(user.get());
        user.ifPresent(u -> u.getTasks().add(task));
        return task;
    }

    @Override
    public void deleteUserTaskById(String username, Long taskId) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if(userOptional.isPresent() && taskOptional.isPresent()){
            User user = userOptional.get();
            Task task = taskOptional.get();
            boolean isUserTheTaskOwner = checkIfUserIsOwnerOfTheTask(user, task);
            if(isUserTheTaskOwner) {
                user.setTasks(user.getTasks().stream().filter(t -> !t.getId().equals(taskId)).collect(Collectors.toList()));
                taskRepository.deleteById(taskId);
            } else {
                throw new ApiBadRequestException("You are not owner of this task!");
            }
        } else {
            throw new ApiBadRequestException("User or task not found");
        }
    }

    @Override
    public List<Task> findUserTasks(Long userId) {
        List<Task> userTasks = taskRepository.findAllByOwnerId(userId);
        return userTasks;
    }

    @Override
    @Transactional
    public void deleteUserById(Long userId) {
        userRepository.deleteUserById(userId);
    }

    @Override
    @Transactional
    public void toggleUserTask(String username, Long taskId) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        Optional<Task> taskOptional = taskRepository.findById(taskId);
        if(userOptional.isPresent() && taskOptional.isPresent()){
            User user = userOptional.get();
            Task task = taskOptional.get();
            boolean isUserTheTaskOwner = checkIfUserIsOwnerOfTheTask(user, task);
            if(isUserTheTaskOwner) {
                task.setCompleted(!task.getCompleted());
            } else {
                throw new ApiBadRequestException("You are not owner of this task!");
            }
        } else {
            throw new RuntimeException("User or task not found");
        }
    }

    private boolean checkIfUserIsOwnerOfTheTask(User user, Task task){
        return task.getOwner().getId().equals(user.getId());
    }
}
