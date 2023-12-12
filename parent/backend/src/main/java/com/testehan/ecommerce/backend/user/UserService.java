package com.testehan.ecommerce.backend.user;

import com.testehan.ecommerce.common.entity.Role;
import com.testehan.ecommerce.common.entity.User;
import com.testehan.ecommerce.common.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class UserService {

    public static final int USER_PAGE_SIZE = 3;     // todo would be nice for this to be configurable..
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAllUsers(){

        return (List<User>) userRepository.findAll(Sort.by("firstName").ascending());
    }

    public List<Role> findAllRoles(){
        return (List<Role>) roleRepository.findAll();
    }

    public Page<User> listUsersByPage(int pageNumber, String sortField, String sortOrder, String keyword){
        Sort sort = Sort.by(sortField);
        sort = sortOrder.equalsIgnoreCase("ASC") ? sort.ascending() : sort.descending();

        // the first pageNumber displayed in the UI is 1...but the paging starts from 0, hence why we need to substract 1
        Pageable pageable = PageRequest.of(pageNumber -1, USER_PAGE_SIZE, sort);

        if (Strings.isNotBlank(keyword)){
            return userRepository.findAllByKeyword(keyword,pageable);
        } else {
            return userRepository.findAll(pageable);
        }
    }

    public User save(User user) {
        boolean isUpdatingUser = user.getId() != null;

        // we need to do this so that when editing the user, and if do not provide the password, we do not want to
        //store an empty password for that user..
        if (isUpdatingUser){
            User existingUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()){
                user.setPassword(existingUser.getPassword());
            } else {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.save(user);
    }

    public User updateAccount(User userInForm) {
        User userInDB = userRepository.findById(userInForm.getId()).get();

        if (!userInForm.getPassword().isEmpty()) {
            userInDB.setPassword(passwordEncoder.encode(userInForm.getPassword()));
        }

        if (userInForm.getPhoto() != null) {
            userInDB.setPhoto(userInForm.getPhoto());
        }

        userInDB.setFirstName(userInForm.getFirstName());
        userInDB.setLastName(userInForm.getLastName());

        return userRepository.save(userInDB);
    }

    public boolean isEmailUnique(Integer id, String email){
        User userByEmail = userRepository.getUserByEmail(email);

        if (userByEmail == null){
            return true;
        }

        boolean isCreatingNew = id == null;
        if (isCreatingNew){
            if (userByEmail != null){
                return false;           // this means that we try to create a new user, but the email is already present in the DB
            }
        } else {
            if (userByEmail.getId()!=id){
                return false;
            }
        }

        return true;
    }

    public User findById(Integer id) throws UserNotFoundException {
        try {
            return userRepository.findById(id).get();
        } catch(NoSuchElementException e){
            throw  new UserNotFoundException("Could not find any user with id " + id);
        }
    }

    public User findByEmail(String email) throws UserNotFoundException {
        try {
            return userRepository.getUserByEmail(email);
        } catch(NoSuchElementException e){
            throw  new UserNotFoundException("Could not find any user with email " + email);
        }
    }

    public void deleteUser(Integer id) throws UserNotFoundException {
        Long count = userRepository.countById(id);
        if (count == null || count == 0){
            throw new UserNotFoundException("Could not find any user with ID " + id);
        }

        userRepository.deleteById(id);

    }

    public void updateEnabledStatus(Integer id, boolean enabled){
        userRepository.updateEnabledStatus(id,enabled);
    }
}
