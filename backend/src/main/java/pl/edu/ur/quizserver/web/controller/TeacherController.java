package pl.edu.ur.quizserver.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.quizserver.persistence.entity.*;
import pl.edu.ur.quizserver.persistence.pojo.GroupedPeoplePojo;
import pl.edu.ur.quizserver.web.dto.*;
import pl.edu.ur.quizserver.web.service.GroupService;
import pl.edu.ur.quizserver.web.service.LoginService;
import pl.edu.ur.quizserver.web.service.PeopleService;
import pl.edu.ur.quizserver.web.service.QuizService;
import pl.edu.ur.quizserver.web.util.GenericResponse;
import pl.edu.ur.quizserver.web.util.Tools;
import pl.edu.ur.quizserver.web.util.Views;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Transactional
@RequestMapping("/teacher")
public class TeacherController {

    @Autowired
    private PeopleService peopleService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private LoginService loginService;

    @Autowired
    private QuizService quizService;

    //region Student management

    //region GET
    @JsonView(Views.PeopleAll.class)
    @PreAuthorize("hasAuthority('permission.teacher.get_students')")
    @GetMapping("/students")
    public List<PersonEntity> getStudents()
    {
        return peopleService.getPeople(Tools.MapPersonType(Tools.PersonType.STUDENT));
    }

    @JsonView(Views.PersonDetails.class)
    @PreAuthorize("hasAuthority('permission.teacher.get_student_details')")
    @GetMapping("/student")
    public Map<String,Object> getStudentDetails(@RequestParam("id") long id)
    {
        return peopleService.getPersonDetails(id,Tools.MapPersonType(Tools.PersonType.STUDENT));
    }

    @PreAuthorize("hasAuthority('permission.teacher.get_students') " +
            "AND hasAuthority('permission.group.get_groups')")
    @GetMapping("/groups/students")
    public List<GroupedPeoplePojo> getGroupedStudents()
    {
        List<GroupedPeoplePojo> groups = groupService.getGroupedPeople();
        return groups;
    }
    //endregion

    //region POST
    @PreAuthorize("hasAuthority('permission.teacher.add_student')")
    @PostMapping("/student/add")
    public Map<String,Object> addStudent(@Valid @RequestBody CreatePersonDto createPersonDto)
    {
        Map<String,Object> result = new HashMap<>();
        String password = peopleService.createPerson(createPersonDto,Tools.MapPersonType(Tools.PersonType.STUDENT));

        result.put("message","success");
        result.put("password", password);
        return result;
    }

    @PreAuthorize("hasAuthority('permission.teacher.reset_student')")
    @PostMapping("/student/reset")
    public Map<String,String>  restStudent(@RequestParam("id") long id){
        String password = loginService.resetUser(id,Tools.MapPersonType(Tools.PersonType.STUDENT));
        Map<String, String> result = new HashMap<>();
        result.put("password",password);
        result.put("message","success");
        return result;
    }
    //endregion

    //region PUT

    @PreAuthorize("hasAuthority('permission.teacher.update_student')")
    @PutMapping("/student/update")
    public GenericResponse updateStudent(@RequestParam("id") long id,
                                         @Valid @RequestBody UpdateUserDetailsDto updateUserDetailsDto)
    {
        peopleService.updatePerson(id,updateUserDetailsDto,Tools.MapPersonType(Tools.PersonType.STUDENT));
        return new GenericResponse("success");
    }

    //endregion

    //region DELETE
    @PreAuthorize("hasAuthority('permission.teacher.delete_student')")
    @DeleteMapping("/student/delete")
    public GenericResponse deleteStudent(@RequestParam("id") long id)
    {
        peopleService.deletePerson(id,Tools.MapPersonType(Tools.PersonType.STUDENT));
        return new GenericResponse("success");
    }
    //endregion

    //endregion

    //region Quiz management

    //region GET

    @PreAuthorize("hasAuthority('permission.teacher.get_grouped_questions')")
    @GetMapping("/questions")
    public List<QuestionGroupEntity> getGroupedQuestions()
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        return quizService.getQuestionGroups(login.getPerson().getId());
    }

    @PreAuthorize("hasAuthority('permission.teacher.get_question_details')")
    @GetMapping("/question/details")
    public QuestionEntity getQuestionDetails(@RequestParam("id") long id)
    {
        return quizService.getQuestionDetails(id);
    }

    @PreAuthorize("hasAuthority('permission.teacher.get_quizzes')")
    @GetMapping("/quizzes")
    @JsonView(Views.QuizTeacherPublic.class)
    public List<QuizEntity> getQuizes()
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());
        return quizService.getQuizes(login.getPerson().getId());
    }

    @JsonView(Views.QuizTeacherPublic.class)
    @PreAuthorize("hasAuthority('permission.teacher.get_schedules')")
    @GetMapping("/schedules")
    public List<ScheduleEntity> getSchedules()
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        return quizService.getSchedules(login.getPerson().getId());
    }

    @PreAuthorize("hasAuthority('permission.teacher.get_quizzes')")
    @GetMapping("/test/results")
    public List<QuizResultEntity> getResults()
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        return quizService.getResultsByCreator(login.getPerson());
    }

    @PreAuthorize("hasAuthority('permission.teacher.get_quizzes')")
    @GetMapping("/test/results/history")
    public String getQuizResultHistory(@RequestParam("id") long id)
    {
        return quizService.getArchivedQuizResult(id);
    }

    //endregion

    //region POST

    @PreAuthorize("hasAuthority('permission.teacher.add_question_group')")
    @PostMapping("/questions/groups/add")
    public GenericResponse addQuestionGroup(@Valid @RequestBody CreateGroupDto createGroupDto)
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        quizService.createQuestionGroup(login.getPerson(),createGroupDto.getName());

        return new GenericResponse("success");
    }

    @PreAuthorize("hasAuthority('permission.teacher.add_question_to_group')")
    @PostMapping("/questions/add")
    public GenericResponse addQuestionToGroup(@RequestParam("id") long id,
                                              @Valid @RequestBody AddQuestionDto addQuestionDto)
    {
        quizService.addQuestionToGroup(id,addQuestionDto);
        return new GenericResponse("success");
    }

    @PreAuthorize("hasAuthority('permission.teacher.add_quiz')")
    @PostMapping("/quizzes/add")
    public GenericResponse addQuiz(@Valid @RequestBody CreateQuizDto dto)
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        quizService.createQuiz(login.getPerson(),dto);

        return new GenericResponse("success");
    }

    @PreAuthorize("hasAuthority('permission.teacher.add_schedule')")
    @PostMapping("/schedules/add")
    public GenericResponse addSchedule(@Valid @RequestBody CreateScheduleDto dto)
    {
        quizService.createSchedule(dto);

        return new GenericResponse("success");
    }

    //endregion

    //region PUT

    @PreAuthorize("hasAuthority('permission.teacher.update_question')")
    @PutMapping("/questions/update")
    public GenericResponse updateQuestion(@RequestParam("id") long id,
                                          @Valid @RequestBody AddQuestionDto dto)
    {
        quizService.updateQuestion(id,dto);
        return new GenericResponse("success");
    }

    //endregion

    //region DELETE

    @PreAuthorize("hasAuthority('permission.teacher.delete_question_group')")
    @DeleteMapping("/questions/groups/delete")
    public GenericResponse deleteQuestionGroup(@RequestParam("id") long id)
    {
        quizService.deleteQuestionGroup(id);
        return new GenericResponse("success");
    }

    @PreAuthorize("hasAuthority('permission.teacher.delete_question')")
    @DeleteMapping("/questions/delete")
    public GenericResponse deleteQuestion(@RequestParam("id") long id)
    {
        quizService.deleteQuestion(id);
        return new GenericResponse("success");
    }

    @PreAuthorize("hasAuthority('permission.teacher.delete_quiz')")
    @DeleteMapping("/quizzes/delete")
    public GenericResponse deleteQuiz(@RequestParam("id") long id)
    {
        quizService.deleteQuiz(id);
        return new GenericResponse("success");
    }

    @PreAuthorize("hasAuthority('permission.teacher.delete_schedule')")
    @DeleteMapping("/schedules/delete")
    public GenericResponse deleteSchedule(@RequestParam("id") long id)
    {
        quizService.deleteSchedule(id);
        return new GenericResponse("success");
    }

    //endregion

    //endregion
}
