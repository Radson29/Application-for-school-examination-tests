package pl.edu.ur.quizserver.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.edu.ur.quizserver.QuizServerApplication;
import pl.edu.ur.quizserver.persistence.entity.*;
import pl.edu.ur.quizserver.persistence.repository.*;
import pl.edu.ur.quizserver.web.service.LoginService;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Component
@Transactional
public class Seeder implements CommandLineRunner {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired QuestionGroupRepository questionGroupRepository;

    @Autowired
    private DataSource dataSource;

    private boolean IsSeeded(){
        return permissionRepository.findAll().size() > 0;
    }

    @Override
    public void run(String... args) throws Exception {
        boolean dbSeed = false;
        for (String arg : args) {
            if(arg.contains("--db.seed=true"))
                dbSeed = true;
        }


        if(IsSeeded())
            return;

        InitializePermissions();
        InitializeRoles();
        if(dbSeed)
            InitializePeople();
        else
            InitializePeopleCore();
        if(dbSeed)
            InitializeTests();

    }

    public void InitializePermissions() {
        ExecuteSQLSeed("seeders/INSERT_PERMISSIONS.sql");
    }

    public void InitializeRoles() {
        ExecuteSQLSeed("seeders/INSERT_ROLES.sql");
    }

    public void InitializePeopleCore() {
        ExecuteSQLSeed("seeders/INSERT_PEOPLE_CORE.sql");
    }

    public void InitializePeople() {
        ExecuteSQLSeed("seeders/INSERT_PEOPLE.sql");
    }

    public void InitializeTests() {
        ExecuteSQLSeed("seeders/INSERT_TESTS.sql");
    }

    public void ExecuteSQLSeed(String file){
        ResourceDatabasePopulator resourceDatabasePopulator = new ResourceDatabasePopulator(false, false, "UTF-8", new ClassPathResource(file));
        resourceDatabasePopulator.execute(dataSource);
    }
}
