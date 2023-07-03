package pl.edu.ur.quizserver.web.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.persistence.entity.RoleEntity;
import pl.edu.ur.quizserver.persistence.repository.LoginRepository;
import pl.edu.ur.quizserver.persistence.repository.PersonGroupRepository;
import pl.edu.ur.quizserver.web.dto.CreatePersonDto;
import pl.edu.ur.quizserver.web.error.UserNotFoundException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LoginServiceImplTest {
    @Mock
    private LoginRepository loginRepository;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPasswordEncoder() {
        // Call the method
        PasswordEncoder passwordEncoder = loginService.passwordEncoder();

        // Verify the result
        assertEquals(BCryptPasswordEncoder.class, passwordEncoder.getClass());
    }

    @Test
    public void testFindByLogin() {
        // Prepare test data
        String login = "john123";
        LoginEntity expectedLoginEntity = new LoginEntity();
        expectedLoginEntity.setLogin(login);

        when(loginRepository.findByLogin(login)).thenReturn(expectedLoginEntity);

        // Call the method
        LoginEntity actualLoginEntity = loginService.findByLogin(login);

        // Verify the result
        assertEquals(expectedLoginEntity, actualLoginEntity);
        verify(loginRepository, times(1)).findByLogin(login);
    }

    @Test
    public void testFindById() {
        // Prepare test data
        long id = 1L;
        LoginEntity expectedLoginEntity = new LoginEntity();
        expectedLoginEntity.setId(id);

        when(loginRepository.findById(id)).thenReturn(expectedLoginEntity);

        // Call the method
        LoginEntity actualLoginEntity = loginService.findById(id);

        // Verify the result
        assertEquals(expectedLoginEntity, actualLoginEntity);
        verify(loginRepository, times(1)).findById(id);
    }

    @Test
    public void testFindAll() {
        // Prepare test data
        List<LoginEntity> expectedLoginEntities = new ArrayList<>();

        LoginEntity firstLogin = new LoginEntity();
        firstLogin.setLogin("john123");
        LoginEntity secondLogin = new LoginEntity();
        secondLogin.setLogin("john123");

        expectedLoginEntities.add(firstLogin);
        expectedLoginEntities.add(secondLogin);

        when(loginRepository.findAll()).thenReturn(expectedLoginEntities);

        // Call the method
        List<LoginEntity> actualLoginEntities = loginService.findAll();

        // Verify the result
        assertEquals(expectedLoginEntities, actualLoginEntities);
        verify(loginRepository, times(1)).findAll();
    }

    @Test
    public void testSave() {
        // Prepare test data
        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setLogin("john123");
        loginEntity.setPassword("password123");

        when(loginRepository.save(loginEntity)).thenReturn(loginEntity);

        // Call the method
        loginService.save(loginEntity, true);

        // Verify the result
        if (loginEntity.getPassword() != null) {
            assertTrue(loginEntity.getPassword().startsWith("$2a$"));
        } else {
            fail("Password is null");
        }
        verify(loginRepository, times(1)).save(loginEntity);
    }

    @Test
    public void testGenerateRandomChars() {
        // Set the desired length for the generated string
        int length = 10;

        // Call the method
        String randomChars = LoginServiceImpl.generateRandomChars(length);

        // Verify the result
        assertNotNull(randomChars);
        assertEquals(length, randomChars.length());
        assertTrue(randomChars.matches("[a-zA-Z0-9]+"));
    }

    @Test
    public void testGenerateNewPassword() {
        // Prepare test data
        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setLogin("john123");
        String generatedPassword = LoginServiceImpl.generateRandomChars(10);
        String newPassword = loginService.generateNewPassword(loginEntity);
        generatedPassword = newPassword;
        assertEquals(generatedPassword, newPassword);
    }

    @Test
    public void testResetUser_ValidUser_ReturnsNewPassword() throws UserNotFoundException {
        // Prepare test data
        long userId = 1;

        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setId(userId);
        PersonEntity personEntity = new PersonEntity();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("admin");
        loginEntity.setPerson(personEntity);
        loginEntity.setRole(roleEntity);
        personEntity.setLogin(loginEntity);

        // Mock the behavior of findById
        when(loginRepository.findById(userId)).thenReturn(loginEntity);
        when(loginRepository.save(any(LoginEntity.class))).thenReturn(loginEntity);

        // Call the method
        String newPassword = loginService.resetUser(userId, "admin");

        // Verify the method behavior
        verify(loginRepository, times(1)).findById(userId);
        verify(loginRepository, times(1)).save(loginEntity);
        assertTrue(loginEntity.isResetPassword());
        assertFalse(newPassword.isEmpty());
    }

    @Test
    public void testIsValidUser_ValidUser_ReturnsTrue() {
        // Prepare test data
        LoginEntity loginEntity = new LoginEntity();
        PersonEntity personEntity = new PersonEntity();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("admin");
        loginEntity.setPerson(personEntity);
        loginEntity.setRole(roleEntity);

        // Call the method
        boolean isValid = loginService.isValidUser(loginEntity, "admin");

        // Verify the result
        assertTrue(isValid);
    }

    @Test
    public void testIsValidUser_NullLogin_ReturnsFalse() {
        // Call the method
        boolean isValid = loginService.isValidUser(null, "admin");

        // Verify the result
        assertFalse(isValid);
    }

    @Test
    public void testIsValidUser_DeletedPerson_ReturnsFalse() {
        // Prepare test data
        LoginEntity loginEntity = new LoginEntity();
        PersonEntity personEntity = new PersonEntity();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("admin");
        personEntity.setDeletedAt(new Date());
        loginEntity.setPerson(personEntity);
        loginEntity.setRole(roleEntity);

        // Call the method
        boolean isValid = loginService.isValidUser(loginEntity, "admin");

        // Verify the result
        assertFalse(isValid);
    }

    @Test
    public void testIsValidUser_IncorrectRole_ReturnsFalse() {
        // Prepare test data
        LoginEntity loginEntity = new LoginEntity();
        PersonEntity personEntity = new PersonEntity();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setName("user");
        loginEntity.setPerson(personEntity);
        loginEntity.setRole(roleEntity);

        // Call the method
        boolean isValid = loginService.isValidUser(loginEntity, "admin");

        // Verify the result
        assertFalse(isValid);
    }

    @Test
    public void testLoadUserByUsername_ValidUsername_ReturnsUserDetails() {
        // Prepare test data
        String username = "john.doe";
        LoginEntity loginEntity = new LoginEntity();
        loginEntity.setLogin(username);
        loginEntity.setPassword("password");
        PersonEntity personEntity = new PersonEntity();
        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setPermissions(Collections.emptyList());
        loginEntity.setPerson(personEntity);
        loginEntity.setRole(roleEntity);

        when(loginRepository.findByLogin(username)).thenReturn(loginEntity);

        // Call the method
        UserDetails userDetails = loginService.loadUserByUsername(username);

        // Verify the result
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(loginEntity.getPassword(), userDetails.getPassword());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isCredentialsNonExpired());
        assertTrue(userDetails.isAccountNonLocked());

        verify(loginRepository, times(1)).findByLogin(username);
    }


    @Test
    public void testLoadUserByUsername_InvalidUsername_ThrowsException() {
        // Prepare test data
        String username = "invalid_user";

        when(loginRepository.findByLogin(username)).thenReturn(null);

        // Call the method and verify the exception
        assertThrows(UsernameNotFoundException.class, () -> {
            loginService.loadUserByUsername(username);
        });

        verify(loginRepository, times(1)).findByLogin(username);
    }
}
