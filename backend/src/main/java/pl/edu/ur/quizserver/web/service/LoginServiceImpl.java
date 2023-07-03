package pl.edu.ur.quizserver.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;
import pl.edu.ur.quizserver.persistence.entity.PermissionEntity;
import pl.edu.ur.quizserver.persistence.repository.LoginRepository;
import pl.edu.ur.quizserver.web.error.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginRepository loginRepository;

    @Bean
    @Override
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public LoginEntity findByLogin(String login) {
        return loginRepository.findByLogin(login);
    }

    @Override
    public LoginEntity findById(long id) {
        return loginRepository.findById(id);
    }

    @Override
    public List<LoginEntity> findAll() {
        return loginRepository.findAll();
    }

    /**
     Saves the LoginEntity to the database, and optionally encodes the password before saving.
     @param login the LoginEntity to be saved
     @param encodePassword a boolean indicating whether to encode the password before saving
     */
    @Override
    public void save(LoginEntity login, boolean encodePassword) {
        if(encodePassword)
            login.setPassword(passwordEncoder().encode(login.getPassword()));
        loginRepository.save(login);
    }
    /**
     Generates a random string of the specified length using alphanumeric characters.
     @param length the length of the string to generate
     @return a random string of length characters
     */
    public static String generateRandomChars(int length) {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        Random random = new Random();

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }
    /**
     Generates a new random password for the specified login entity.
     The password is a string of 10 randomly generated alphanumeric characters.
     @param login the login entity for which to generate a new password
     @return the new password string
     */
    @Override
    public String generateNewPassword(LoginEntity login) {
        String password = generateRandomChars(10);
        login.setPassword(password);
        save(login,true);
        return password;
    }
    /**
     Resets the password of a user with the specified ID and type by generating a new random password
     and setting the "reset password" flag to true in the login entity.
     @param id the ID of the user to reset the password for
     @param type the type of the user to reset the password for
     @return the new password string
     @throws UserNotFoundException if the user with the specified ID and type is not found
     */
    @Override
    public String resetUser(long id, String type)
       throws UserNotFoundException {
            LoginEntity login = loginRepository.findById(id);
            if(!isValidUser(login,type))
                throw new UserNotFoundException();

            String password = this.generateNewPassword(login);
            login.setResetPassword(true);

            return password;
    }
    /**
     Checks whether the specified login entity represents a valid user of the specified person type.
     A user is considered valid if the login entity is not null, contains a person entity, and has a role
     with a name that matches the specified person type. The person entity must also not be marked as deleted.
     @param login the login entity to check
     @param personType the type of person to check the login entity against
     @return true if the login entity is a valid user of the specified person type, false otherwise
     */
    @Override
    public boolean isValidUser(LoginEntity login, String personType) {
        return (login != null && login.getPerson() != null && login.getRole().getName().equals(personType) && !login.getPerson().isDeleted());
    }

    /**
     Returns a UserDetails object containing user information based on the given username.
     @param username the username to search for in the LoginEntity table
     @return a UserDetails object representing the user associated with the given username
     @throws UsernameNotFoundException if no user is found with the given username in the LoginEntity table or if the user's associated PersonEntity has been deleted
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginEntity login = loginRepository.findByLogin(username);
        if(login == null || login.getPerson() == null  || login.getPerson().isDeleted())
            throw new UsernameNotFoundException("No user found with name: " + username);

        return new User(login.getLogin(),
                login.getPassword(),
                true,
                true,
                true,
                true,
                getGrantedAuthorities(login.getRole().getPermissions().stream().map(PermissionEntity::getPermission).collect(Collectors.toList()))
        );
    }

    /**
     Returns a list of granted authorities based on the given list of permissions.
     @param permissions a list of permissions to be converted to granted authorities
     @return a list of granted authorities
     */
    private List<GrantedAuthority> getGrantedAuthorities(final List<String> permissions)
    {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for(String permission : permissions)
            grantedAuthorities.add(new SimpleGrantedAuthority(permission));
        return grantedAuthorities;
    }
}
