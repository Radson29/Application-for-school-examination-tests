package pl.edu.ur.quizserver.web.util;

public class Views {
    public static class PeopleAll { }
    public static class PersonDetails extends PeopleAll{}

    public static class QuizStudent {}
    public static class QuizTeacherPublic extends QuizStudent {}
    public static class QuizTeacherPrivate extends QuizTeacherPublic {}
}
