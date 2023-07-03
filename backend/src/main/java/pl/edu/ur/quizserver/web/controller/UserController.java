package pl.edu.ur.quizserver.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;
import pl.edu.ur.quizserver.persistence.entity.PermissionEntity;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.web.dto.ChangePasswordDto;
import pl.edu.ur.quizserver.web.error.UserNotFoundException;
import pl.edu.ur.quizserver.web.service.LoginService;
import pl.edu.ur.quizserver.web.util.GenericResponse;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private TokenStore tokenStore;

    //region GET

    /**

     Endpoint for getting information about the authenticated user.
     @return a PersonEntity object representing the authenticated user's profile information
     @throws UserNotFoundException if the user cannot be found in the database
     */
    @PreAuthorize("hasAuthority('permission.user.get_my_info')")
    @GetMapping("/info")
    public PersonEntity info()
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());
        if(login == null)
            throw new UserNotFoundException();
        return login.getPerson();
    }


    /**
     Endpoint that returns the permissions, role name and whether the user needs to reset their password of the current authenticated user.
     @return a Map with the following keys: "permissions" - a list of strings representing the permissions of the user,
     */
    @PreAuthorize("hasAuthority('permission.user.get_permissions')")
    @GetMapping("/permissions")
    public Map<String, Object> permissions()
    {
        Map<String, Object> model = new HashMap<>();

        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());
        List<String> permissions = login.getRole().getPermissions().stream().map(PermissionEntity::getPermission).collect(Collectors.toList());

        model.put("permissions",permissions);
        model.put("role",login.getRole().getName());
        model.put("reset_password",login.isResetPassword());

        return model;
    }


    /**
     Endpoint to logout a user.
     @param auth The OAuth2Authentication object containing the access token details.
     @return A GenericResponse indicating success.
     */
    @PreAuthorize("hasAuthority('permission.user.logout')")
    @GetMapping("/logout")
    public GenericResponse logout(final OAuth2Authentication auth)
    {
        final OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) auth.getDetails();
        String accessToken = details.getTokenValue();
        OAuth2AccessToken token = tokenStore.readAccessToken(accessToken);
        tokenStore.removeAccessToken(token);

        return new GenericResponse("success");
    }

    //endregion

    //region POST

    @PreAuthorize("hasAuthority('permission.user.change_password')")
    @PostMapping("/changepassword")
    public GenericResponse changePassword(@Valid @RequestBody ChangePasswordDto dto) throws RuntimeException
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());
        login.setPassword(dto.getPassword());
        login.setResetPassword(false);
        loginService.save(login,true);
        return new GenericResponse("success");
    }

    //endregion
}
