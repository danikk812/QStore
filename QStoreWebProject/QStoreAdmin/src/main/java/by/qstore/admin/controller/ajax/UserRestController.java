package by.qstore.admin.controller.ajax;

import by.qstore.admin.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserRestController {
    @Autowired
    private UserService userService;

    @PostMapping("/users/email_check")
    public String checkDuplicateEmail(Long id, String email) {
        return userService.isEmailExist(id, email) ? "true" : "false";
    }
}
