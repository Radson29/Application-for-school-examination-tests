package pl.edu.ur.quizserver.web.service;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.edu.ur.quizserver.persistence.entity.*;
import pl.edu.ur.quizserver.persistence.repository.*;
import pl.edu.ur.quizserver.web.dto.*;
import pl.edu.ur.quizserver.web.error.*;
import pl.edu.ur.quizserver.web.util.Tools;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizServiceImpl implements QuizService {

    @Autowired
    private PeopleRepository peopleRepository;

    @Autowired
    private QuestionGroupRepository questionGroupRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AvailableQuizRepository availableQuizRepository;

    @Autowired
    private GeneratedQuestionRepository generatedQuestionRepository;

    @Autowired
    private QuizInstanceRepository quizInstanceRepository;

    @Autowired
    private QuizResultRepository quizResultRepository;

    private static Random random = new Random();

    /**
     Returns a list of question groups created by the user with the given ID.
     @param userId the ID of the user for whom to retrieve question groups
     @return a list of question groups created by the user
     @throws UserNotFoundException if a user with the given ID does not exist in the database
     */
    @Override
    public List<QuestionGroupEntity> getQuestionGroups(long userId) throws UserNotFoundException {
        PersonEntity person = peopleRepository.findById(userId);//todo
        if(person == null)
            throw new UserNotFoundException();
        return questionGroupRepository.findByCreatorIdAndDeletedAtIsNull(person.getId());
    }

    /**
     Creates a new question group with the given name for the specified creator.
     @param creator the person creating the question group
     @param groupName the name of the new question group
     @throws GroupAlreadyExistException if a group with the same name already exists
     */
    @Override
    public void createQuestionGroup(PersonEntity creator, String groupName) throws GroupAlreadyExistException {
        if(questionGroupRepository.findByName(groupName) != null)
            throw new GroupAlreadyExistException();

        QuestionGroupEntity questionGroup = new QuestionGroupEntity();
        questionGroup.setName(groupName);
        questionGroup.setCreator(creator);

        questionGroupRepository.save(questionGroup);
    }

    /**
     Deletes a question from the database based on the given identifier.
     @param groupId identifier of the question group to be deleted
     @throws QuestionNotFoundException if the question with the given identifier is not found in the database
     */
    @Override
    public void deleteQuestionGroup(long groupId) throws GroupNotFoundException {
        QuestionGroupEntity questionGroup = questionGroupRepository.findById(groupId);
        if(questionGroup == null)
            throw new GroupNotFoundException();

        questionGroup.setDeletedAt(new Date());
        questionGroupRepository.save(questionGroup);
    }


    /**
     Adds a new question to the question group with the given ID.
     @param groupid the ID of the question group to which the new question belongs
     @param dto an AddQuestionDto object representing the value and answers of the new question
     @throws GroupNotFoundException if the question group with the given ID does not exist or has been deleted
     */
    @Override
    public void addQuestionToGroup(long groupid, AddQuestionDto dto) throws GroupNotFoundException {
        QuestionGroupEntity group = questionGroupRepository.findById(groupid);
        if(group == null)
            throw new GroupNotFoundException();
        if(group.getDeletedAt() != null)
            throw new GroupNotFoundException();

        QuestionEntity question = new QuestionEntity();

        question.setValue(dto.getValue());
        question.setAnswers(answersDtoToAnswer(dto));
        group.addQuestion(question);
        questionGroupRepository.save(group);
    }

    /**
     Retrieves the Question corresponding to the given question ID.
     @param questionId the ID of the question to retrieve.
     @return the QuestionEntity representing the question with the given ID.
     @throws QuestionNotFoundException if the question with the given ID is not found in the database.
     */
    @Override
    public QuestionEntity getQuestionDetails(long questionId) throws QuestionNotFoundException {
        QuestionEntity question = questionRepository.findById(questionId);
        if(question == null)
            throw new QuestionNotFoundException();

        return question;
    }

    /**
     Updates the question with the given questionId with the new question value and answers provided in the AddQuestionDto object.
     @param questionId the ID of the question to be updated
     @param dto the AddQuestionDto object containing the new question value and list of answers
     @throws QuestionNotFoundException if the question with the given questionId does not exist in the database
     */
    @Override
    public void updateQuestion(long questionId, AddQuestionDto dto) throws QuestionNotFoundException {
        QuestionEntity question = questionRepository.findById(questionId);
        if(question == null)
            throw new QuestionNotFoundException();

        answerRepository.deleteAll(question.getAnswers());

        question.setValue(dto.getValue());
        question.setAnswers(answersDtoToAnswer(dto));

        questionRepository.save(question);
    }

    /**
     Deletes the question with the specified identifier. If the question is not found, a QuestionNotFoundException is thrown.
     @param questionId the identifier of the question to delete
     @throws QuestionNotFoundException if the question with the specified identifier does not exist
     */
    @Override
    public void deleteQuestion(long questionId) throws QuestionNotFoundException {
        QuestionEntity question = questionRepository.findById(questionId);
        if(question == null)
            throw new QuestionNotFoundException();

        answerRepository.deleteAll(question.getAnswers());
        questionRepository.delete(question);
    }

    /**
     Returns a list of quizzes created by the user with the specified ID.
     @param userId the ID of the user whose quizzes should be retrieved.
     @return a list of QuizEntity objects representing the quizzes created by the user.
     @throws UserNotFoundException if no user with the specified ID exists in the database.
     */
    @Override
    public List<QuizEntity> getQuizes(long userId) throws UserNotFoundException {
        PersonEntity person = peopleRepository.findById(userId); //todo
        if(person == null)
            throw new UserNotFoundException();
        return quizRepository.findByCreatorId(userId);
    }

    /**
     Creates a new quiz based on the provided data.
     @param creator object representing the creator of the quiz
     @param dto object representing the data needed to create the quiz
     @throws GroupNotFoundException thrown when the specified question group does not exist in the database
     @throws QGInvalidQuestionCountException thrown when the number of questions in the group is less than the number of questions required to generate a quiz module
     @throws QGInvalidRecipesCountException thrown when no quiz module could be created
     */
    @Override
    public void createQuiz(PersonEntity creator, CreateQuizDto dto) throws GroupNotFoundException, QGInvalidQuestionCountException, QGInvalidRecipesCountException {
        QuizEntity quiz = new QuizEntity();

        quiz.setTitle(dto.getTitle());
        quiz.setDescription(dto.getDescription());
        quiz.setQuizTime(dto.getQuizTime());
        quiz.setCreator(creator);

        List<QGRecipeEntity> recipes = new ArrayList<>();

        for(QGRecipeDto qRecipeDTO : dto.getRecipes())
        {
            QuestionGroupEntity qgEntity = questionGroupRepository.findById((long)qRecipeDTO.getGroupId());
            if(qgEntity == null)
                throw new GroupNotFoundException();
            if(qRecipeDTO.getCount() > qgEntity.getQuestions().size())
                throw new QGInvalidQuestionCountException();
            if(qRecipeDTO.getCount() == 0)
                continue;

            QGRecipeEntity recipe = new QGRecipeEntity();
            recipe.setQuestionGroup(qgEntity);
            recipe.setQuestionsToGenerate(qRecipeDTO.getCount());
            recipe.setQuiz(quiz);

            recipes.add(recipe);
        }
        if(recipes.size() == 0)
            throw new QGInvalidRecipesCountException();

        quiz.setQuestionModules(recipes);

        quizRepository.save(quiz);
    }

    /**
     Deletes a quiz with the given ID.
     @param quizId the ID of the quiz to delete.
     @throws QuizNotFoundException if a quiz with the given ID was not found in the database.
     */
    @Override
    public void deleteQuiz(long quizId) throws QuizNotFoundException {
        QuizEntity quiz = quizRepository.findById(quizId);
        if(quiz == null)
            throw new QuizNotFoundException();
        //todo remove schedule
       //todo remove instances
        quizRepository.delete(quiz);
    }


    /**
     Converts a list of objects of class AddAnswerDto to a list of objects of class AnswerEntity.
     @param dto an object of class AddQuestionDto containing a list of answers.
     @return returns a list of objects of class AnswerEntity created based on the dto object.
     */
    private List<AnswerEntity> answersDtoToAnswer(AddQuestionDto dto)
    {
        List<AnswerEntity> answers = new ArrayList<>();

        for(AddAnswerDto addAnswerDto : dto.getAnswers())
        {
            AnswerEntity answer = new AnswerEntity();
            answer.setValue(addAnswerDto.getValue());
            answer.setCorrect(addAnswerDto.isCorrect());
            answers.add(answer);
        }
        return answers;
    }

    /**
     * Retrieves a list of schedules for the specified user.
     *
     * @param userId the ID of the user
     * @return a list of ScheduleEntity objects representing the user's schedules
     * @throws UserNotFoundException if the specified user is not found
     */
    @Override
    public List<ScheduleEntity> getSchedules(long userId) throws UserNotFoundException {
        return scheduleRepository.findByQuizCreatorId(userId);
    }

    /**
     * Retrieves a list of available quizzes for the specified student.
     *
     * @param studentId the ID of the student
     * @return a list of ScheduleEntity objects representing the available quizzes
     * @throws UserNotFoundException if the specified student is not found
     */
    @Override
    public List<ScheduleEntity> getAvailableQuizzes(long studentId) throws UserNotFoundException {
        PersonEntity student = peopleRepository.findById(studentId);
        if(student == null)
            throw new UserNotFoundException();

        List<ScheduleEntity> schedules = scheduleRepository.findByAvailableStudentIdAndAvailableInstanceIsNullAndStartsAtBeforeAndEndsAtAfter(studentId,new Date(), new Date());

        return schedules;
    }

    /**
     * Creates a schedule using the information provided in the CreateScheduleDto.
     *
     * @param dto the CreateScheduleDto containing the details of the schedule to be created
     * @throws QuizNotFoundException    if the specified quiz is not found
     * @throws UserNotFoundException    if the specified user is not found
     */
    @Override
    public void createSchedule(CreateScheduleDto dto) throws QuizNotFoundException, UserNotFoundException {
        QuizEntity quiz = quizRepository.findById((long)dto.getQuizId());
        if(quiz == null)
            throw new QuizNotFoundException();

        List<AvailableQuizEntity> available = new ArrayList<>();
        for(Integer id : dto.getStudents())
        {
            PersonEntity person = peopleRepository.findById(id);
            if(person == null || !person.getLogin().getRole().getName().equals(Tools.MapPersonType(Tools.PersonType.STUDENT)))
                throw new UserNotFoundException();

            AvailableQuizEntity availableQuiz = new AvailableQuizEntity();
            availableQuiz.setStudent(person);

            available.add(availableQuiz);
        }

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setQuiz(quiz);
        schedule.setStartsAt(dto.getStartsAt());
        schedule.setEndsAt(dto.getEndsAt());
        schedule.setAvailable(available);

        scheduleRepository.save(schedule);
    }

    /**
     * Starts a quiz for the specified student and schedule.
     *
     * @param student    the PersonEntity representing the student
     * @param scheduleId the ID of the schedule for the quiz
     * @return a QuizInstanceEntity representing the started quiz
     * @throws QuizNotFoundException      if the specified quiz is not found
     * @throws QuizExpiredException       if the specified quiz has expired
     * @throws QuizAlreadyRunningException if the specified quiz is already running
     */
    @Override
    public QuizInstanceEntity startQuiz(PersonEntity student, long scheduleId) throws QuizNotFoundException, QuizExpiredException, QuizAlreadyRunningException {
        ScheduleEntity schedule = scheduleRepository.findById(scheduleId);
        if(schedule == null || !schedule.getAvailableFor().contains(student))
            throw new QuizNotFoundException();

        AvailableQuizEntity access = availableQuizRepository.findByStudentIdAndScheduleId(student.getId(),scheduleId);

        if(isQuizNotAvailable(access))
            throw new QuizExpiredException();

        if(access.getInstance() != null) {
            return access.getInstance();
        }

        QuizInstanceEntity quizInstance = new QuizInstanceEntity();

        quizInstance.setStudent(student);
        quizInstance.setQuiz(schedule.getQuiz());
        quizInstance.setStartedAt(new Date());

        List<QuestionEntity> allQuestions = new ArrayList<>();

        int temp = 0;
        for(QGRecipeEntity module : schedule.getQuiz().getQuestionRecipes()) {
            List<QuestionEntity> questionsToPickFrom = module.getQuestionGroup().getQuestions();

            while (temp < module.getQuestionsToGenerate()) {
                QuestionEntity question = questionsToPickFrom.get(random.nextInt(questionsToPickFrom.size()));
                if(allQuestions.contains(question))
                    continue;
                allQuestions.add(question);
                temp++;
            }
            temp = 0;
        }


        List<GeneratedQuestionEntity> result = allQuestions.stream().map(question ->
        {
            Collections.shuffle(question.getAnswers());

            GeneratedQuestionEntity generatedQuestion = new GeneratedQuestionEntity();
            generatedQuestion.setQuestion(question);
            generatedQuestion.setQuizInstance(quizInstance);

            return generatedQuestion;
        }).collect(Collectors.toList());
        quizInstance.setGeneratedQuestions(result);

        List<GeneratedQuestionEntity> copy = new ArrayList<>(result);
        generatedQuestionRepository.saveAll(copy);

        access.setInstance(quizInstance);
        return quizInstance;
    }

    /**
     * Archives an answered question by generating a JSON object.
     *
     * @param question The GeneratedQuestionEntity representing the question to be archived.
     * @param answers  The List of Long values representing the answers to the question.
     * @return The JSONObject containing the archived question and answers.
     */
    private JSONObject archiveAnsweredQuestion(GeneratedQuestionEntity question, List<Long> answers){
        JSONObject obj = new JSONObject();
        obj.put("question", question.getQuestion().getValue());
        JSONArray arr = new JSONArray();
        for(AnswerEntity answer : question.getQuestion().getAnswers()){
            JSONObject answerObj = new JSONObject();
            answerObj.put("value", answer.getValue());
            answerObj.put("correct", answer.isCorrect());
            answerObj.put("selected", answers.contains(answer.getId()));
            arr.put(answerObj);
        }
        obj.put("answers", arr);
        return obj;
    }

    /**
     * Overrides the method from the superclass to finish a quiz attempted by a student.
     *
     * @param student The PersonEntity representing the student who attempted the quiz.
     * @param dto The FinishQuizDto containing the details of the finished quiz.
     * @return A QuizResultEntity representing the result of the finished quiz.
     * @throws QuizNotFoundException if the quiz cannot be found.
     * @throws QuizExpiredException if the quiz has already expired.
     */
    @Override
    public QuizResultEntity finishQuiz(PersonEntity student, FinishQuizDto dto) throws QuizNotFoundException, QuizExpiredException{
        QuizInstanceEntity quizInstance = quizInstanceRepository.findById((long)dto.getQuizInstanceId());
        if(quizInstance == null || quizInstance.getStudent() != student)
            throw new QuizNotFoundException();

        if(quizExpired(quizInstance))
            throw new QuizExpiredException();

        if(quizInstance.getAvailable() == null)
            throw new QuizNotFoundException();

        Map<Long, AnsweredQuestionDto> answeredQuestions = validateFixQuizInstanceAnswers(quizInstance.getGeneratedQuestions(),dto.getData());

        List<JSONObject> archivedQuestions = new ArrayList<>();

        int score = 0;

        for(GeneratedQuestionEntity genQuestion : quizInstance.getGeneratedQuestions())
        {
            AnsweredQuestionDto answeredDto = answeredQuestions.get(genQuestion.getId());

            List<Long> correctAnswersIds = answerRepository.findByQuestionIdAndCorrectIsTrue(genQuestion.getQuestion().getId())
                    .stream().map(AnswerEntity::getId).collect(Collectors.toList());

            if(checkAnswers(correctAnswersIds,answeredDto.getAnswersIds()))
                score++;

            archivedQuestions.add(archiveAnsweredQuestion(genQuestion,answeredDto.getAnswersIds()));
        }

        JSONObject archivedQuiz = new JSONObject();
        archivedQuiz.put("title", quizInstance.getQuiz().getTitle());
        archivedQuiz.put("questions", archivedQuestions);

        availableQuizRepository.delete(quizInstance.getAvailable());
        generatedQuestionRepository.deleteAll(quizInstance.getGeneratedQuestions());
        quizInstanceRepository.delete(quizInstance);

        QuizResultEntity result = new QuizResultEntity();

        result.setPerson(student);
        result.setScore(score);
        result.setQuestionsCount(quizInstance.getGeneratedQuestions().size());
        result.setQuizTitle(quizInstance.getQuiz().getTitle());
        result.setQuizCreator(quizInstance.getQuiz().getCreator().getFirstName() + " " +
                quizInstance.getQuiz().getCreator().getLastName());
        result.setSubmittedAt(new Date());
        result.setQuizHistory(archivedQuiz.toString());
        System.out.println(archivedQuiz.toString());
        quizResultRepository.save(result);

        return result;
    }

    /**
     * Checks whether a quiz instance has expired.
     *
     * @param instance The QuizInstanceEntity representing the quiz instance to check.
     * @return {@code true} if the quiz instance has expired, {@code false} otherwise.
     */
    private boolean quizExpired(QuizInstanceEntity instance)
    {
        Date now = new Date();
        if(now.getTime() - instance.getStartedAt().getTime()
                > instance.getQuiz().getQuizTime() * 60 * 1000)
            return true;
        return false;
    }

    /**
     * Validates and fixes the answers for a quiz instance based on the generated questions and answered question DTOs.
     *
     * @param generatedQuestions The List of GeneratedQuestionEntity representing the generated questions for the quiz instance.
     * @param dtos The List of AnsweredQuestionDto representing the answered question DTOs for the quiz instance.
     * @return A Map<Long, AnsweredQuestionDto> containing the validated and fixed answers for each question ID.
     */
    private Map<Long, AnsweredQuestionDto> validateFixQuizInstanceAnswers(List<GeneratedQuestionEntity> generatedQuestions, List<AnsweredQuestionDto> dtos)
    {
        Map<Long, AnsweredQuestionDto> dtoMap =
                dtos.stream().collect(Collectors.toMap(AnsweredQuestionDto::getQuestionId, question -> question));

        generatedQuestions.forEach(question -> {
            if(!dtoMap.containsKey(question.getId()))
                dtoMap.put(question.getId(), new AnsweredQuestionDto());
        });

        return dtoMap;
    }

    /**
     * Checks whether the answers provided by the user match the correct answers.
     *
     * @param correctAnswers The List of Long representing the correct answers for a question.
     * @param fromUser The List of Long representing the answers provided by the user.
     * @return {@code true} if the user answers match the correct answers, {@code false} otherwise.
     */
    private boolean checkAnswers(List<Long> correctAnswers, List<Long> fromUser)
    {
        if(fromUser.size() != correctAnswers.size())
            return false;

        for(Long correct : correctAnswers)
            if(!fromUser.contains(correct))
                return false;
        return true;
    }

    /**
     * Deletes a schedule with the specified schedule ID.
     *
     * @param scheduleId the ID of the schedule to be deleted
     * @throws ScheduleNotFoundException if the specified schedule is not found
     */
    @Override
    public void deleteSchedule(long scheduleId) throws ScheduleNotFoundException {
        ScheduleEntity schedule = scheduleRepository.findById(scheduleId);
        if(schedule == null)
            throw new ScheduleNotFoundException();
        scheduleRepository.delete(schedule);
    }

    /**
     * Overrides the method from the superclass to check if a quiz is not available.
     *
     * @param availableQuiz The AvailableQuizEntity representing the available quiz to check.
     * @return {@code true} if the quiz is not available, {@code false} otherwise.
     */
    @Override
    public boolean isQuizNotAvailable(AvailableQuizEntity availableQuiz)
    {
        Date now = new Date();
        if(!now.after(availableQuiz.getSchedule().getStartsAt())
                || !now.before(availableQuiz.getSchedule().getEndsAt()))
            return true;
        return false;
    }

    /**
     * Overrides the method from the superclass to retrieve the quiz results for a student.
     *
     * @param student The PersonEntity representing the student for whom to retrieve the quiz results.
     * @return A List of QuizResultEntity representing the quiz results for the student.
     */
    @Override
    public List<QuizResultEntity> getResults(PersonEntity student) {
        return quizResultRepository.findByPersonId(student.getId());
    }

    /**
     * Retrieves the quiz results for quizzes created by a specific teacher.
     *
     * @param teacher The PersonEntity representing the teacher who created the quizzes.
     * @return A List of QuizResultEntity representing the quiz results for quizzes created by the teacher.
     */
    @Override
    public List<QuizResultEntity> getResultsByCreator(PersonEntity teacher) {
        return quizResultRepository.findByQuizCreator(teacher.getFirstName() + " " + teacher.getLastName());
    }

    /**
     * Retrieves the archived quiz result as a string for the given quiz result ID.
     *
     * @param quizResultId The ID of the quiz result to retrieve.
     * @return The archived quiz result as a string.
     * @throws QuizResultNotFoundException If the quiz result with the specified ID is not found.
     */
    @Override
    public String getArchivedQuizResult(long quizResultId) throws QuizResultNotFoundException {
        QuizResultEntity quizResult = quizResultRepository.findById(quizResultId);
        if(quizResult == null)
            throw new QuizResultNotFoundException();
        return quizResult.getQuizHistory();
    }
}
