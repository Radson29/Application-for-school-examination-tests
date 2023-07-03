package pl.edu.ur.quizserver.web.util;

public class Tools {
    public enum PersonType{
        STUDENT,
        TEACHER,
        ADMIN
    }
    public static String MapPersonType(PersonType personType){
        return "ROLE_" + personType.toString();
    }
}
