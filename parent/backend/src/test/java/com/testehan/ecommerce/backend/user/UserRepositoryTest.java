package com.testehan.ecommerce.backend.user;

import com.testehan.ecommerce.common.entity.Role;
import com.testehan.ecommerce.common.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest            // obviously the annotation from below means that the docker container needs to run, since it doesn't use a in memory DB, but the actual DB
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Rollback(false)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void whenPersistingUserWithOneRole_userIsPersistedWithTheRole(){
        Role adminRole = new Role();
        adminRole.setName("admin2");
        adminRole.setDescription("he is the one that can do anything in the app");

        User user = new User("email", "password", "firstName", "lastName");
        user.addRole(adminRole);
        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getRoles().stream().findAny().get().getName()).isEqualTo("admin2");
    }

    @Test
    public void whenPersistingUserWithTwoRoles_userIsPersistedWithTheRoles(){
        var adminRole = new Role();
        adminRole.setName("admin2");
        adminRole.setDescription("he is the one that can do anything in the app");

        var editorRole = new Role();
        editorRole.setName("editor2");
        editorRole.setDescription("manage categories, brands, products, articles");

        var user = new User("email", "password", "firstName", "lastName");
        user.addRole(adminRole);
        user.addRole(editorRole);
        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getRoles().size()).isEqualTo(2);
    }

    @Test
    public void whenListingAllUsers_allUsersAreReturned(){
        var adminRole = new Role();
        adminRole.setName("admin2");
        adminRole.setDescription("he is the one that can do anything in the app");

        var user1 = new User("email", "password", "firstName", "lastName");
        user1.addRole(adminRole);
        User savedUser = userRepository.save(user1);

        var user2 = new User("email2", "password2", "firstName2", "lastName2");
        user2.addRole(adminRole);
        User savedUser2 = userRepository.save(user2);

        List<User> users = userRepository.findAll();
        assertThat(users.contains(savedUser)).isTrue();
        assertThat(users.contains(savedUser2)).isTrue();
    }

    @Test
    public void whenGettingUserById_userIsObtainetCorectlyFromDB(){
        var adminRole = new Role();
        adminRole.setName("admin2");
        adminRole.setDescription("he is the one that can do anything in the app");

        var user1 = new User("email", "password", "firstName", "lastName");
        user1.addRole(adminRole);
        User savedUser = userRepository.save(user1);

        User userObtainedById = entityManager.find(User.class,savedUser.getId());

        assertThat(savedUser).isEqualTo(userObtainedById);
    }

    @Test
    public void whenUserIsUpdatedInDB_changesArePersistedInDB(){
        var adminRole = new Role();
        adminRole.setName("admin2");
        adminRole.setDescription("he is the one that can do anything in the app");

        var user1 = new User("email", "password", "firstName", "lastName");
        user1.addRole(adminRole);
        User savedUser = userRepository.save(user1);
        savedUser.setEnabled(true);
        userRepository.save(savedUser);

        User userObtainedById = entityManager.find(User.class,savedUser.getId());

        assertThat(userObtainedById.isEnabled()).isEqualTo(true);
    }

    @Test
    public void whenRemovingRoleFromAUserWithTwoRoles_userRoleIsRemoved(){
        var adminRole = new Role();
        adminRole.setName("admin2");
        adminRole.setDescription("he is the one that can do anything in the app");

        var editorRole = new Role();
        editorRole.setName("editor2");
        editorRole.setDescription("manage categories, brands, products, articles");

        var user = new User("email", "password", "firstName", "lastName");
        user.addRole(adminRole);
        user.addRole(editorRole);
        User savedUser = userRepository.save(user);
        assertThat(savedUser.getRoles().size()).isEqualTo(2);

        savedUser.getRoles().remove(adminRole);
        savedUser = userRepository.save(savedUser);

        assertThat(savedUser.getRoles().size()).isEqualTo(1);
    }

    @Test
    public void whenUserIsDeleted_changesArePersistedInDB(){
        var adminRole = new Role();
        adminRole.setName("admin2");
        adminRole.setDescription("he is the one that can do anything in the app");

        var user1 = new User("email", "password", "firstName", "lastName");
        user1.addRole(adminRole);
        User savedUser = userRepository.save(user1);

        List<User> users = userRepository.findAll();
        assertThat(users.contains(user1)).isTrue();

        userRepository.delete(savedUser);

        users = userRepository.findAll();
        assertThat(users.contains(user1)).isFalse();
    }

    @Test
    public void whenUserIsSearchedByEmail_userIsReturnedIfFound(){
        var adminRole = new Role();
        adminRole.setName("admin2");
        adminRole.setDescription("he is the one that can do anything in the app");

        var user1 = new User("email", "password", "firstName", "lastName");
        userRepository.save(user1);

        List<User> users = userRepository.findAll();
        assertThat(users.contains(user1)).isTrue();

        var userByEmail = userRepository.getUserByEmail("email");

        assertThat(userByEmail.getEmail()).isEqualTo("email");
    }

    @Test
    public void whenCountingNumberOfUsersWithInexistentId_0isReturned(){

        var noOfUsers = userRepository.countById(100);

        assertThat(noOfUsers).isEqualTo((0));

    }

    @Test
    public void whenDisabledUserIsEnabled_userGetsEnabled(){

        var user1 = new User("email", "password", "firstName", "lastName");
        user1.setEnabled(false);
        var savedUser =userRepository.save(user1);

        userRepository.updateEnabledStatus(savedUser.getId(),true);

        var updatedUser = userRepository.getUserByEmail("email");;

        assertThat(updatedUser.isEnabled()).isTrue();
    }

    @Test
    public void whenListingFirstPage_elementsFromFirstPageAppear(){
        int pageNo = 0;
        int pageSize = 4;

        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<User> page =userRepository.findAll(pageable);

        var listOfPaginatedUsers = page.getContent();
        listOfPaginatedUsers.forEach(user -> System.out.println(user));

        // because the unit test runs on the production DB, i don't know how many users are present in the DB when the test
        // runs...so I obtain the users and see how many they are. The pagination can return the maximum of 4, but if there
        // are fewer than 4, that will be the number of rows..
        var users = userRepository.findAll();
        assertThat(listOfPaginatedUsers.size()).isEqualTo(users.size()>=4 ? 4 : users.size());

    }


    @Test
    public void whenSearchingForAFirstNameKeyword_elementsContainingThatFirstnameKeywordAreReturned(){
        var user1 = new User("email", "password", "Dumbledore", "Albus");
        user1.setEnabled(false);
        userRepository.save(user1);

        int pageNo = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNo,pageSize);

        Page<User> usersPage = userRepository.findAll("Dumbledore",pageable);
        assertThat(usersPage.getContent().stream().findFirst().get().getFirstName()).isEqualTo("Dumbledore");
    }

    @Test
    public void whenSearchingForALastNameKeyword_elementsContainingThatLastNameKeywordAreReturned(){
        var user1 = new User("email", "password", "Dumbledore", "Albus");
        user1.setEnabled(false);
        userRepository.save(user1);

        int pageNo = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNo,pageSize);

        Page<User> usersPage = userRepository.findAll("Albus",pageable);
        assertThat(usersPage.getContent().stream().findFirst().get().getLastName()).isEqualTo("Albus");
    }

    @Test
    public void whenSearchingForAFirstNameOrLastNameKeyword_elementsContainingThatFirstNameOrLastKeywordAreReturned(){
        var user1 = new User("email", "password", "Dumbledore", "Albus");
        user1.setEnabled(false);
        userRepository.save(user1);
        var user2 = new User("email22", "password", "Percivus", "Dumbledore");
        user2.setEnabled(false);
        userRepository.save(user2);

        int pageNo = 0;
        int pageSize = 4;
        Pageable pageable = PageRequest.of(pageNo,pageSize);

        Page<User> usersPage = userRepository.findAll("Dumbledore",pageable);
        assertThat(usersPage.getContent().size()).isEqualTo(2);
    }
}
