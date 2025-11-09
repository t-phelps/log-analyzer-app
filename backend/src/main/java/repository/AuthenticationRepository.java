package repository;

import dto.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthenticationRepository {



    public Optional<User> getUser(User user) {
        return Optional.ofNullable(user);
    }

    public void createUser(User user){

    }
}
