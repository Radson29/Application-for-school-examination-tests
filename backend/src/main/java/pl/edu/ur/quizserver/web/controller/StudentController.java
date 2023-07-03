package pl.edu.ur.quizserver.web.controller;

import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pl.edu.ur.quizserver.persistence.entity.LoginEntity;
import pl.edu.ur.quizserver.persistence.entity.QuizInstanceEntity;
import pl.edu.ur.quizserver.persistence.entity.QuizResultEntity;
import pl.edu.ur.quizserver.persistence.entity.ScheduleEntity;
import pl.edu.ur.quizserver.web.dto.FinishQuizDto;
import pl.edu.ur.quizserver.web.service.LoginService;
import pl.edu.ur.quizserver.web.service.QuizService;
import pl.edu.ur.quizserver.web.util.Views;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    private QuizService quizService;

    @Autowired
    private LoginService loginService;

    //region GET
    @JsonView(Views.QuizStudent.class)
    @PreAuthorize("hasAuthority('permission.student.get_my_tests')")
    @GetMapping("/tests")
    public List<ScheduleEntity> getTests()
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        return  quizService.getAvailableQuizzes(login.getPerson().getId());
    }

    @PreAuthorize("hasAuthority('permission.student.start_test')")
    @GetMapping("/test/start")
    @JsonView(Views.QuizStudent.class)
    public QuizInstanceEntity startTest(@RequestParam("id") long scheduleId)
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        return quizService.startQuiz(login.getPerson(), scheduleId);
    }

    @PreAuthorize("hasAuthority('permission.student.get_my_results')")
    @GetMapping("/test/results")
    public List<QuizResultEntity> getResults()
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        return quizService.getResults(login.getPerson());
    }

    //endregion

    //region POST
    @PreAuthorize("hasAuthority('permission.student.finish_test')")
    @PostMapping("/test/finish")
    public QuizResultEntity finish(@Valid @RequestBody FinishQuizDto dto)
    {
        UserDetails details = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        LoginEntity login = loginService.findByLogin(details.getUsername());

        return quizService.finishQuiz(login.getPerson(),dto);

    }
    //endregion

}
