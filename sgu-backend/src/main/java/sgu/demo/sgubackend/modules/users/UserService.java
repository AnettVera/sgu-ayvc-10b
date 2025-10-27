package sgu.demo.sgubackend.modules.users;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User save(User user){
        return userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email){
        return userRepository.findUserByEmail(email);
    }

    public Optional<User> update(Long id, User userDetails){
        return userRepository.findUserById(id).map(user ->{
            user.setName(userDetails.getName());
            user.setLastname(userDetails.getLastname());
            user.setSurname(userDetails.getSurname());
            user.setTel(userDetails.getTel());
            user.setEmail(userDetails.getEmail());
            return userRepository.save(user);
        });
    }

    public void delete(Long id){
        userRepository.delete(id);
    }

}
