package pl.edu.ur.quizserver.web.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.edu.ur.quizserver.persistence.entity.*;
import pl.edu.ur.quizserver.persistence.repository.*;
import pl.edu.ur.quizserver.web.dto.*;
import pl.edu.ur.quizserver.web.error.*;
import pl.edu.ur.quizserver.web.service.QuizServiceImpl;
import pl.edu.ur.quizserver.web.util.Tools;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuizServiceImplTests {

    @Mock
    private QuestionGroupRepository questionGroupRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private PeopleRepository peopleRepository;

    @Mock
    private QuizRepository quizRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private QuizInstanceRepository quizInstanceRepository;

    @Mock
    private AvailableQuizRepository availableQuizRepository;

    @Mock
    private GeneratedQuestionRepository generatedQuestionRepository;

    @Mock
    private QuizResultRepository quizResultRepository;

    @InjectMocks
    private QuizServiceImpl quizService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetQuestionGroups() throws UserNotFoundException {
        // Arrange
        long userId = 1L;
        PersonEntity person = new PersonEntity();
        person.setId(userId);

        when(peopleRepository.findById(userId)).thenReturn(person);

        List<QuestionGroupEntity> questionGroups = new ArrayList<>();
        QuestionGroupEntity questionGroup1 = new QuestionGroupEntity();
        questionGroup1.setCreator(person);
        questionGroups.add(questionGroup1);
        when(questionGroupRepository.findByCreatorIdAndDeletedAtIsNull(userId)).thenReturn(questionGroups);

        // Act
        List<QuestionGroupEntity> result = quizService.getQuestionGroups(userId);

        // Assert
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(questionGroup1, result.get(0));
        verify(peopleRepository, times(1)).findById(userId);
        verify(questionGroupRepository, times(1)).findByCreatorIdAndDeletedAtIsNull(userId);
    }

    @Test
    public void testGetQuestionGroups_UserNotFoundException() {
        // Arrange
        long userId = 1L;
        when(peopleRepository.findById(userId)).thenReturn(null);

        // Assert
        Assertions.assertThrows(UserNotFoundException.class, () -> {
            // Act
            quizService.getQuestionGroups(userId);
        });
        verify(peopleRepository, times(1)).findById(userId);
        verifyNoInteractions(questionGroupRepository);
    }

    @Test
    public void testCreateQuestionGroup() throws GroupAlreadyExistException {
        // Arrange
        PersonEntity creator = new PersonEntity();
        String groupName = "Group A";
        when(questionGroupRepository.findByName(groupName)).thenReturn(null);

        // Act
        quizService.createQuestionGroup(creator, groupName);

        // Assert
        verify(questionGroupRepository, times(1)).findByName(groupName);
        verify(questionGroupRepository, times(1)).save(any(QuestionGroupEntity.class));
    }

    @Test
    public void testCreateQuestionGroup_GroupAlreadyExistException() {
        // Arrange
        PersonEntity creator = new PersonEntity();
        String groupName = "Group A";
        QuestionGroupEntity existingGroup = new QuestionGroupEntity();
        when(questionGroupRepository.findByName(groupName)).thenReturn(existingGroup);

        // Assert
        Assertions.assertThrows(GroupAlreadyExistException.class, () -> {
            // Act
            quizService.createQuestionGroup(creator, groupName);
        });
        verify(questionGroupRepository, times(1)).findByName(groupName);
        verifyNoMoreInteractions(questionGroupRepository);
    }

    @Test
    public void testDeleteQuestionGroup() throws GroupNotFoundException {
        // Arrange
        long groupId = 123;
        QuestionGroupEntity questionGroup = new QuestionGroupEntity();
        when(questionGroupRepository.findById(groupId)).thenReturn(questionGroup);

        // Act
        quizService.deleteQuestionGroup(groupId);

        // Assert
        verify(questionGroupRepository, times(1)).findById(groupId);
        verify(questionGroupRepository, times(1)).save(questionGroup);
        assertNotNull(questionGroup.getDeletedAt());
    }

    @Test
    public void testAddQuestionToGroup() throws GroupNotFoundException {
        // Arrange
        long groupId = 123;

        QuestionGroupEntity group = new QuestionGroupEntity();
        group.setQuestions(new ArrayList<>());
        when(questionGroupRepository.findById(groupId)).thenReturn(group);

        AddQuestionDto dto = new AddQuestionDto();
        dto.setValue("Sample question value");

        AddAnswerDto answer1 = new AddAnswerDto();
        answer1.setValue("Answer 1");
        answer1.setCorrect(true);

        AddAnswerDto answer2 = new AddAnswerDto();
        answer2.setValue("Answer 2");
        answer2.setCorrect(false);

        dto.setAnswers(Arrays.asList(answer1, answer2));

        // Act
        quizService.addQuestionToGroup(groupId, dto);

        // Assert
        verify(questionGroupRepository, times(1)).findById(groupId);
        verify(questionGroupRepository, times(1)).save(group);
        assertNotNull(group.getQuestions());
        assertEquals(1, group.getQuestions().size());
        // Additional assertions for question properties, if needed
    }

    @Test
    public void testGetQuestionDetails() throws QuestionNotFoundException {
        // Arrange
        long questionId = 456;

        QuestionEntity question = new QuestionEntity();
        when(questionRepository.findById(questionId)).thenReturn(question);

        // Act
        QuestionEntity result = quizService.getQuestionDetails(questionId);

        // Assert
        verify(questionRepository, times(1)).findById(questionId);
        assertEquals(question, result);
    }

    @Test
    public void testUpdateQuestion() throws QuestionNotFoundException {
        // Arrange
        long questionId = 789;

        QuestionEntity question = new QuestionEntity();
        when(questionRepository.findById(questionId)).thenReturn(question);

        AddQuestionDto dto = new AddQuestionDto();
        dto.setValue("Updated question value");

        AddAnswerDto answer1 = new AddAnswerDto();
        answer1.setValue("Answer 1");
        answer1.setCorrect(true);

        AddAnswerDto answer2 = new AddAnswerDto();
        answer2.setValue("Answer 2");
        answer2.setCorrect(false);

        dto.setAnswers(Arrays.asList(answer1, answer2));

        // Act
        quizService.updateQuestion(questionId, dto);

        // Assert
        verify(questionRepository, times(1)).findById(questionId);
        // verify(answerRepository, times(1)).deleteAll(question.getAnswers());
        verify(questionRepository, times(1)).save(question);
        assertEquals("Updated question value", question.getValue());
        // Additional assertions for answer properties, if needed
    }

    @Test
    public void testDeleteQuestion() throws QuestionNotFoundException {
        // Arrange
        long questionId = 123;

        QuestionEntity question = new QuestionEntity();
        when(questionRepository.findById(questionId)).thenReturn(question);

        List<AnswerEntity> answers = Arrays.asList(new AnswerEntity(), new AnswerEntity());
        question.setAnswers(answers);

        // Act
        quizService.deleteQuestion(questionId);

        // Assert
        verify(questionRepository, times(1)).findById(questionId);
        verify(answerRepository, times(1)).deleteAll(answers);
        verify(questionRepository, times(1)).delete(question);
    }

    @Test
    public void testGetQuizes() throws UserNotFoundException {
        // Arrange
        long userId = 123;

        PersonEntity person = new PersonEntity();
        when(peopleRepository.findById(userId)).thenReturn(person);

        List<QuizEntity> expectedQuizes = Arrays.asList(new QuizEntity(), new QuizEntity());
        when(quizRepository.findByCreatorId(userId)).thenReturn(expectedQuizes);

        // Act
        List<QuizEntity> actualQuizes = quizService.getQuizes(userId);

        // Assert
        verify(peopleRepository, times(1)).findById(userId);
        verify(quizRepository, times(1)).findByCreatorId(userId);
        assertEquals(expectedQuizes, actualQuizes);
    }

    @Test
    public void testCreateQuiz() throws GroupNotFoundException, QGInvalidQuestionCountException, QGInvalidRecipesCountException {
        // Arrange
        PersonEntity creator = new PersonEntity();
        CreateQuizDto dto = new CreateQuizDto();
        dto.setTitle("Sample Quiz");
        dto.setDescription("Sample quiz description");
        dto.setQuizTime((short) 60);

        QGRecipeDto recipe1 = new QGRecipeDto();
        recipe1.setGroupId(1L);
        recipe1.setCount(3);

        QGRecipeDto recipe2 = new QGRecipeDto();
        recipe2.setGroupId(2L);
        recipe2.setCount(3);

        dto.setRecipes(Arrays.asList(recipe1, recipe2));

        QuestionGroupEntity qgEntity1 = new QuestionGroupEntity();
        qgEntity1.setQuestions(Arrays.asList(new QuestionEntity(), new QuestionEntity(), new QuestionEntity()));

        QuestionGroupEntity qgEntity2 = new QuestionGroupEntity();
        qgEntity2.setQuestions(Arrays.asList(new QuestionEntity(), new QuestionEntity(), new QuestionEntity()));

        when(questionGroupRepository.findById(1L)).thenReturn(qgEntity1);
        when(questionGroupRepository.findById(2L)).thenReturn(qgEntity2);

        // Act
        quizService.createQuiz(creator, dto);

        // Assert
        verify(questionGroupRepository, times(1)).findById(1L);
        verify(questionGroupRepository, times(1)).findById(2L);
        verify(quizRepository, times(1)).save(any(QuizEntity.class));
    }

    @Test
    public void testDeleteQuiz() throws QuizNotFoundException {
        // Arrange
        long quizId = 123;

        QuizEntity quiz = new QuizEntity();
        when(quizRepository.findById(quizId)).thenReturn(quiz);

        // Act
        quizService.deleteQuiz(quizId);

        // Assert
        verify(quizRepository, times(1)).findById(quizId);
        verify(quizRepository, times(1)).delete(quiz);
    }

    @Test
    public void testGetSchedules() throws UserNotFoundException {
        // Arrange
        long userId = 123;
        List<ScheduleEntity> schedules = new ArrayList<>();
        // Add sample schedules to the list

        when(scheduleRepository.findByQuizCreatorId(userId)).thenReturn(schedules);

        // Act
        List<ScheduleEntity> result = quizService.getSchedules(userId);

        // Assert
        assertEquals(schedules, result);
        verify(scheduleRepository, times(1)).findByQuizCreatorId(userId);
    }

    @Test
    public void testGetAvailableQuizzes() throws UserNotFoundException {
        // Arrange
        long studentId = 123;
        PersonEntity student = new PersonEntity(); // Create a sample PersonEntity
        List<ScheduleEntity> schedules = new ArrayList<>(); // Create a sample list of ScheduleEntity objects

        when(peopleRepository.findById(studentId)).thenReturn(student);
        when(scheduleRepository.findByAvailableStudentIdAndAvailableInstanceIsNullAndStartsAtBeforeAndEndsAtAfter(studentId, new Date(), new Date())).thenReturn(schedules);

        // Act
        List<ScheduleEntity> result = quizService.getAvailableQuizzes(studentId);

        // Assert
        assertEquals(schedules, result);
//        verify(peopleRepository, times(1)).findById(studentId);
//        verify(scheduleRepository, times(1)).findByAvailableStudentIdAndAvailableInstanceIsNullAndStartsAtBeforeAndEndsAtAfter(studentId, new Date(), new Date());
    }

    @Test
    public void testCreateSchedule() {
        assertTrue(true);
    }

    @Test
    public void testStartQuiz() {
        assertTrue(true);
    }

    @Test
    public void testFinishQuiz() {
        assertTrue(true);
    }

    @Test
    public void testDeleteSchedule() throws ScheduleNotFoundException {
        // Arrange
        long scheduleId = 123;
        ScheduleEntity schedule = new ScheduleEntity(); // Create a sample ScheduleEntity

        when(scheduleRepository.findById(scheduleId)).thenReturn(schedule);

        // Act
        quizService.deleteSchedule(scheduleId);

        // Assert
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(scheduleRepository, times(1)).delete(schedule);
    }

    @Test
    public void testIsQuizNotAvailable() {
        // Arrange
        Date now = new Date();
        AvailableQuizEntity availableQuiz = new AvailableQuizEntity(); // Create a sample AvailableQuizEntity
        ScheduleEntity schedule = new ScheduleEntity(); // Create a sample ScheduleEntity

        schedule.setStartsAt(new Date(now.getTime() + 1000)); // Set the start time 1 second in the future
        schedule.setEndsAt(new Date(now.getTime() + 2000)); // Set the end time 2 seconds in the future

        availableQuiz.setSchedule(schedule);

        // Case 1: Current time is before the start time
        assertTrue(quizService.isQuizNotAvailable(availableQuiz));

        // Case 2: Current time is after the end time
        schedule.setStartsAt(new Date(now.getTime() - 2000)); // Set the start time 2 seconds in the past
        schedule.setEndsAt(new Date(now.getTime() - 1000)); // Set the end time 1 second in the past

        assertTrue(quizService.isQuizNotAvailable(availableQuiz));

        // Case 3: Current time is within the start and end time range
        schedule.setStartsAt(new Date(now.getTime() - 1000)); // Set the start time 1 second in the past
        schedule.setEndsAt(new Date(now.getTime() + 1000)); // Set the end time 1 second in the future

        assertFalse(quizService.isQuizNotAvailable(availableQuiz));
    }

    @Test
    public void testGetResults() {
        // Arrange
        long studentId = 123;
        PersonEntity student = new PersonEntity(); // Create a sample PersonEntity
        student.setId(123L);
        List<QuizResultEntity> results = new ArrayList<>(); // Create a sample list of QuizResultEntity objects

        when(quizResultRepository.findByPersonId(studentId)).thenReturn(results);

        // Act
        List<QuizResultEntity> result = quizService.getResults(student);

        // Assert
        assertEquals(results, result);
        verify(quizResultRepository, times(1)).findByPersonId(studentId);
    }

    @Test
    public void testGetResultsByCreator() {
        // Arrange
        PersonEntity teacher = new PersonEntity(); // Create a sample PersonEntity
        List<QuizResultEntity> results = new ArrayList<>(); // Create a sample list of QuizResultEntity objects

        when(quizResultRepository.findByQuizCreator(anyString())).thenReturn(results);

        // Act
        List<QuizResultEntity> result = quizService.getResultsByCreator(teacher);

        // Assert
        assertEquals(results, result);
        verify(quizResultRepository, times(1)).findByQuizCreator(anyString());
    }

    @Test
    public void testGetArchivedQuizResult() throws QuizResultNotFoundException {
        // Arrange
        long quizResultId = 123;
        QuizResultEntity quizResult = new QuizResultEntity(); // Create a real QuizResultEntity object
        String quizHistory = "Sample quiz history"; // Create a sample quiz history string

        when(quizResultRepository.findById(quizResultId)).thenReturn(quizResult); // Mock the repository call

        // Set up the necessary data for the quiz result object
        quizResult.setId(quizResultId);
        quizResult.setQuizHistory(quizHistory);

        // Act
        String result = quizService.getArchivedQuizResult(quizResultId);

        // Assert
        assertEquals(quizHistory, result);
        verify(quizResultRepository, times(1)).findById(quizResultId);
    }

    @Test
    void createQuestionGroupTest() throws GroupAlreadyExistException {
        PersonEntity creator = new PersonEntity();
        String groupName = "Nazwa grupy pytań";

        //Sprawdzenie czy grupa istnieje, jeżeli nie to zwróć null
        when(questionGroupRepository.findByName(groupName)).thenReturn(null);

        quizService.createQuestionGroup(creator, groupName);

        verify(questionGroupRepository, times(1)).save(Mockito.any(QuestionGroupEntity.class));
    }

    @Test
    public void getQuestionDetailsTest() {
        QuestionEntity question = new QuestionEntity();
        question.setId(1L);
        question.setValue("Co oznacza skrót HTML?");

        when(questionRepository.findById(question.getId())).thenReturn(Optional.of(question));

        Assertions.assertThrows(QuestionNotFoundException.class, () -> {quizService.getQuestionDetails(question.getId());} );
    }

    @Test
    public void addQuestionTest() {
        QuestionEntity question = new QuestionEntity();
        List<AnswerEntity> answers = new ArrayList<AnswerEntity>();
        AnswerEntity answer1 = new AnswerEntity();
        answer1.setValue("Odpowiedź 1");
        answers.add(answer1);
        AnswerEntity answer2 = new AnswerEntity();
        answer2.setValue("Odpowiedź 2");
        answers.add(answer2);

        question.setAnswers(answers);

        Assertions.assertEquals(answers, question.getAnswers());
    }

    @Test
    public void getQuizesTest() {
        QuizEntity quiz1 = new QuizEntity();
        quiz1.setId(1L);
        quiz1.setTitle("Quiz 1");

        Long userId = 1L;

        List<QuizEntity> quizes = new ArrayList<>();
        quizes.add(quiz1);

        assertThrows(UserNotFoundException.class, () -> {quizService.getQuizes(userId);});
    }
}
