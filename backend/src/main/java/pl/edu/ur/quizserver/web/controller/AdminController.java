package pl.edu.ur.quizserver.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.quizserver.persistence.entity.PersonEntity;
import pl.edu.ur.quizserver.web.dto.CreatePersonDto;
import pl.edu.ur.quizserver.web.dto.UpdateUserDetailsDto;
import pl.edu.ur.quizserver.web.service.GroupService;
import pl.edu.ur.quizserver.web.service.LoginService;
import pl.edu.ur.quizserver.web.service.PeopleService;
import pl.edu.ur.quizserver.web.util.GenericResponse;
import pl.edu.ur.quizserver.web.util.Tools;
import pl.edu.ur.quizserver.web.util.Views;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private LoginService loginService;

    //region GET's

    @JsonView(Views.PeopleAll.class)
    @PreAuthorize("hasAuthority('permission.admin.get_teachers')")
    @GetMapping("/teachers")
    public List<PersonEntity> getTeachers()
    {
        return peopleService.getPeople(Tools.MapPersonType(Tools.PersonType.TEACHER));
    }

    @JsonView(Views.PersonDetails.class)
    @PreAuthorize("hasAuthority('permission.admin.get_teacher_details')")
    @GetMapping("/teacher")
    public Map<String,Object> getTeacherDetails(@RequestParam("id") long id)
    {
        return peopleService.getPersonDetails(id,Tools.MapPersonType(Tools.PersonType.TEACHER));
    }

    //endregion

    //region PUT

    @PreAuthorize("hasAuthority('permission.admin.update_teacher')")
    @PutMapping("/teacher/update")
    public GenericResponse updateTeacher(@RequestParam("id") long id,
                                         @Valid @RequestBody UpdateUserDetailsDto updateUserDetailsDto)
    {
        peopleService.updatePerson(id,updateUserDetailsDto,Tools.MapPersonType(Tools.PersonType.TEACHER));
        return new GenericResponse("success");
    }

    //endregion

    //region POST

    @PreAuthorize("hasAuthority('permission.admin.add_teacher')")
    @PostMapping("/teacher/add")
    public Map<String,Object> addTeacher(@Valid @RequestBody CreatePersonDto createPersonDto)
    {
        Map<String,Object> result = new HashMap<>();
        String password = peopleService.createPerson(createPersonDto,Tools.MapPersonType(Tools.PersonType.TEACHER));

        result.put("message","success");
        result.put("password", password);
        return result;
    }

    @PreAuthorize("hasAuthority('permission.admin.reset_teacher')")
    @PostMapping("/teacher/reset")
    public Map<String,String>  restTeacher(@RequestParam("id") long id){
        String password = loginService.resetUser(id,Tools.MapPersonType(Tools.PersonType.TEACHER));
        Map<String, String> result = new HashMap<>();
        result.put("password",password);
        result.put("message","success");
        return result;
    }

    //endregion

    //region DELETE's
    @PreAuthorize("hasAuthority('permission.admin.delete_teacher')")
    @DeleteMapping("/teacher/delete")
    public GenericResponse deleteTeacher(@RequestParam("id") long id)
    {
        peopleService.deletePerson(id,Tools.MapPersonType(Tools.PersonType.TEACHER));
        return new GenericResponse("success");
    }
    //endregion
}
