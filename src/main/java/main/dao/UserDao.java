package main.dao;

import main.models.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/* Defines CRUD interface for the corresponding model */
@Repository
public interface UserDao extends CrudRepository<User, Long> {
    public User findByUsername(String username);
    public User findById(long id);
}


