package by.qstore.admin.service;

import by.qstore.admin.exception.UserNotFoundException;
import by.qstore.admin.repository.RoleRepository;
import by.qstore.admin.repository.UserRepository;
import by.qstore.common.entity.Role;
import by.qstore.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;



@Service
@Transactional
public class UserService {
    public static final int USERS_PER_PAGE = 4;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public List<User> listAll() {
        return userRepository.findAll();
    }

    public Page<User> listByPage(int pageNum, String sortBy, String sortOrder, String keyword) {
        Sort sort = Sort.by(sortBy);
        sort = sortOrder.equals("asc") ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(pageNum - 1, USERS_PER_PAGE, sort);

        if (keyword  != null) {
            return userRepository.findAllByKeyword(keyword, pageable);
        }

        return userRepository.findAll(pageable);
    }

    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    public User save(User user) {
        boolean isUpdatingUser = (user.getId() != null);
        if (isUpdatingUser) {
            User existingUser = userRepository.findById(user.getId()).get();
            if (user.getPassword().isEmpty()) {
                user.setPassword(existingUser.getPassword());
            } else {
                encodePassword(user);
            }
        } else {
            encodePassword(user);
        }
        return userRepository.save(user);
    }

    public User updateAccount(User userInForm) {
        User userById = userRepository.findById(userInForm.getId()).get();
        
        if (!userInForm.getPassword().isEmpty()) {
            userById.setPassword(userInForm.getPassword());
            encodePassword(userById);
        }

        if (userInForm.getPhotos() != null) {
            userById.setPhotos(userInForm.getPhotos());

        }

        userById.setFirstName(userInForm.getFirstName());
        userById.setLastName(userInForm.getLastName());
        return userRepository.save(userById);
    }

    public User getByEmail(String email) {
        return userRepository.getUserByEmail(email);
    }

    public boolean isEmailExist(Long id, String email) {
        User userByEmail = userRepository.getUserByEmail(email);

        if (userByEmail == null) {
            return false;
        }

        boolean isCreatingNew = (id == null);

        if (isCreatingNew) {
            return userByEmail != null;
        } else {
            return userByEmail.getId() != id;
        }
    }

    public User getById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
    }

    public void delete(Long id) throws UserNotFoundException {
        Long usersCountById = userRepository.countById(id);
        if (usersCountById == null || usersCountById == 0) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    public void updateUserEnableStatus(Long id, boolean enabled) {
        userRepository.updateEnabledStatus(id, enabled);
    }

    private void encodePassword(User user) {
        String passwordEncoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(passwordEncoded);
    }
}
