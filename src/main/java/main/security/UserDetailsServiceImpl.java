package main.security;

import main.dao.UserDao;
import main.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/* Spring security class for finding, loading or registering users */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty())
            throw new UsernameNotFoundException("Username is empty!");

        User findUser = userDao.findByUsername(username);
        if (findUser != null)
            return UserDetailsImpl.createFromUser(findUser);
        throw new UsernameNotFoundException(username + " is not found!");
    }

    public User registerUser(User user) {
        // Hashed the password here
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return userDao.save(user);
    }


}
