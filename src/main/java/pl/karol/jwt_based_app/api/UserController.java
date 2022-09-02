package pl.karol.jwt_based_app.api;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pl.karol.jwt_based_app.exception.ApiBadRequestException;
import pl.karol.jwt_based_app.role.Role;
import pl.karol.jwt_based_app.task.Task;
import pl.karol.jwt_based_app.user.User;
import pl.karol.jwt_based_app.user.UserDtoMapper;
import pl.karol.jwt_based_app.user.UserService;
import pl.karol.jwt_based_app.user.dto.UserDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static pl.karol.jwt_based_app.utils.JwtUtils.algorithm;
import static pl.karol.jwt_based_app.utils.JwtUtils.getUsernameFromTokenInsideRequest;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody UserDto userDto){
        UserDto savedUserDto = userService.saveUser(userDto);
        URI savedUserDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUserDto.getId())
                .toUri();
        return ResponseEntity.created(savedUserDtoUri).body(savedUserDto);
    }

    @PostMapping("/users/tasks")
    public ResponseEntity<Task> createAndAddTaskToUser(HttpServletRequest request, @RequestBody Task task){
        String username = getUsernameFromTokenInsideRequest(request);
        Task taskSaved = userService.addTaskToUser(username, task);
        URI savedUserDtoUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(taskSaved.getId())
                .toUri();
        return ResponseEntity.created(savedUserDtoUri).body(taskSaved);
        //TODO: otestowac, metoda gotowa, sprawdzic body co zwraca w tescie, czy id to
    }

    @PatchMapping("/users/tasks/{taskId}")
    public ResponseEntity<?> toggleUserTask(HttpServletRequest request, @PathVariable Long taskId){
        userService.toggleUserTask(getUsernameFromTokenInsideRequest(request), taskId);
        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/users/tasks/{taskId}")
    public ResponseEntity<?> deleteUserTaskById(HttpServletRequest request, @PathVariable Long taskId){
        String username = getUsernameFromTokenInsideRequest(request);
        userService.deleteUserTaskById(username, taskId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/tasks")
    public ResponseEntity<List<Task>> findUserTasks(HttpServletRequest request){
        String username = getUsernameFromTokenInsideRequest(request);
        Optional<UserDto> userDto = userService.getUserByUsername(username);
        List<Task> userTasks = userService.findUserTasks(userDto.get().getId());
        return ResponseEntity.ok().body(userTasks);
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> DeleteUserById(HttpServletRequest request){
        String username = getUsernameFromTokenInsideRequest(request);
        Optional<UserDto> userDto = userService.getUserByUsername(username);
        if(userDto.isPresent()){
            userService.deleteUserById(userDto.get().getId());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/tokens/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String username = getUsernameFromTokenInsideRequest(request);
                User user = UserDtoMapper.map(userService.getUserByUsername(username).get());
                System.out.println(user.getRoles());
                String access_token = JWT.create().withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10*60*1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Map<String, String> tokens = new HashMap<>();
                tokens.put("access_token", access_token);
                tokens.put("refresh_token", authHeader.substring("Bearer ".length()));

                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception){
                System.out.println("Error has occurred during refresh_token validation: " + exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }

        } else {
            throw new ApiBadRequestException("Refresh token is missing!");
        }
    }
}
