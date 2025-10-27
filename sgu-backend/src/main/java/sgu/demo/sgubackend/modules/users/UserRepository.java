package sgu.demo.sgubackend.modules.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long id);

    List<User> findAll();

    User save(User user);

    @Modifying
    @Query(value = "DELETE FROM users WHERE id = :id", nativeQuery = true)
    void delete(@Param("id") Long id);



}
