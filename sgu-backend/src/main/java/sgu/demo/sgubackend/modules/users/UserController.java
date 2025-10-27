package sgu.demo.sgubackend.modules.users;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sgu.demo.sgubackend.utils.ApiResponse;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAll() {
        List<User> users = userService.findAll();
        return ApiResponse.success(users, "Usuarios obtenidos").toResponseEntity();
    }

    @GetMapping("/{email}")
    public ResponseEntity<ApiResponse> findByEmail(@PathVariable String email) {
        return userService.findUserByEmail(email)
                .map(user -> ApiResponse.success(user, "Usuario encontrado").toResponseEntity())
                .orElseGet(() -> ApiResponse.error("Usuario no encontrado", HttpStatus.NOT_FOUND).toResponseEntity());
    }

    @PostMapping
    public ResponseEntity<ApiResponse> save(@RequestBody User user) {
        User saved = userService.save(user);
        return ApiResponse.success(saved, "Usuario creado", HttpStatus.CREATED).toResponseEntity();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse>update(@PathVariable Long id, @RequestBody User userDetails){
        return userService.update(id, userDetails)
                .map(updatedUser -> ApiResponse.success(updatedUser, "Usuario actualizado").toResponseEntity())
                .orElseGet(() -> ApiResponse.error("Usuario no encontrado", HttpStatus.NOT_FOUND).toResponseEntity());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id){
        userService.delete(id);
        return ApiResponse.success(null, "Usuario eliminado").toResponseEntity();
    }
}
