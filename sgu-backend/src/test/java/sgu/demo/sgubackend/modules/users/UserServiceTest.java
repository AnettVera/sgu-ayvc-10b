package sgu.demo.sgubackend.modules.users;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    @Test
    void update_existingUser_updatesAndSaves() {
        User existing = new User(1L, "Anett", "Vera", "Carbajal", "anettyo@gmail.com", "7711920657");
        User details = new User(null, "Mali", "Vera", "Carbajal", "mali@gmail.com", "7772929657");
        when(userRepository.findUserById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<User> result = userService.update(1L, details);

        assertTrue(result.isPresent());
        User updated = result.get();
        assertEquals(1L, updated.getId());
        assertEquals("Mali", updated.getName());
        assertEquals("Vera", updated.getLastname());
        assertEquals("Carbajal", updated.getSurname());
        assertEquals("mali@gmail.com", updated.getEmail());
        assertEquals("7772929657", updated.getTel());

        verify(userRepository).findUserById(1L);
        verify(userRepository).save(existing);
    }

    @Test
    void update_nonExisting_returnsEmpty() {
        when(userRepository.findUserById(2L)).thenReturn(Optional.empty());

        Optional<User> result = userService.update(2L, new User());

        assertTrue(result.isEmpty());
        verify(userRepository).findUserById(2L);
        verify(userRepository, never()).save(any());
    }
}

