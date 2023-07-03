package pl.edu.ur.quizserver.web.service;

import pl.edu.ur.quizserver.persistence.entity.*;
import pl.edu.ur.quizserver.web.dto.AddQuestionDto;
import pl.edu.ur.quizserver.web.dto.CreateQuizDto;
import pl.edu.ur.quizserver.web.dto.CreateScheduleDto;
import pl.edu.ur.quizserver.web.dto.FinishQuizDto;
import pl.edu.ur.quizserver.web.error.*;

import java.util.List;

public interface QuizService {

    List<QuestionGroupEntity> getQuestionGroups(long userId)         throws UserNotFoundException;
    void createQuestionGroup(PersonEntity creator, String groupName) throws GroupAlreadyExistException;
    void deleteQuestionGroup(long groupId)                           throws GroupNotFoundException;
    void addQuestionToGroup(long groupid, AddQuestionDto dto)        throws GroupNotFoundException;
    QuestionEntity getQuestionDetails(long questionId)               throws QuestionNotFoundException;
    void updateQuestion(long questionId, AddQuestionDto dto)         throws QuestionNotFoundException;
    void deleteQuestion(long questionId)                             throws QuestionNotFoundException;
    List<QuizEntity> getQuizes(long userId)                          throws UserNotFoundException;
    void createQuiz(PersonEntity creator, CreateQuizDto dto)         throws GroupNotFoundException, QGInvalidQuestionCountException, QGInvalidRecipesCountException;
    void deleteQuiz(long quizId)                                     throws QuizNotFoundException;
    void createSchedule(CreateScheduleDto dto)                       throws QuizNotFoundException, UserNotFoundException;
    void deleteSchedule(long scheduleId)                             throws ScheduleNotFoundException;
    List<ScheduleEntity> getSchedules(long userId)                   throws UserNotFoundException;

    //region Student access
    boolean isQuizNotAvailable(AvailableQuizEntity availableQuiz);
    List<ScheduleEntity> getAvailableQuizzes(long studentId)         throws UserNotFoundException;
    QuizInstanceEntity startQuiz(PersonEntity student,
                           long scheduleId)                          throws QuizNotFoundException, QuizExpiredException, QuizAlreadyRunningException;
    QuizResultEntity finishQuiz(PersonEntity student,
                                FinishQuizDto dto)                   throws QuizNotFoundException, QuizExpiredException;
    List<QuizResultEntity> getResults(PersonEntity student);
    List<QuizResultEntity> getResultsByCreator(PersonEntity teacher);
    String getArchivedQuizResult(long quizResultId)                  throws QuizResultNotFoundException;
    //endregion
}
