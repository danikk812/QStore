package by.qstore.admin.controller;

import by.qstore.admin.entity.security.QStoreUserDetails;
import by.qstore.admin.service.UserService;
import by.qstore.admin.util.FileUtil;
import by.qstore.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
public class AccountController {

    @Autowired
    private UserService userService;
    
    @GetMapping("/account")
    public ModelAndView viewProfilePage(@AuthenticationPrincipal QStoreUserDetails loggedInUser, ModelAndView modelAndView) {
        String email = loggedInUser.getUsername();
        User userByEmail = userService.getByEmail(email);

        modelAndView.addObject("userForm", userByEmail);
        modelAndView.setViewName("user/account_form");
        return modelAndView;
    }

    @PostMapping("/account/update")
    public ModelAndView saveDetails(@ModelAttribute("userForm") User user, @RequestParam("image") MultipartFile multipartFile,
                                 @AuthenticationPrincipal QStoreUserDetails loggedInUser,
                                 RedirectAttributes redirectAttributes, ModelAndView modelAndView) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.updateAccount(user);
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUtil.cleanDir(uploadDir);
            FileUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
            }
                userService.updateAccount(user);
        }

        loggedInUser.setFirstName(user.getFirstName());
        loggedInUser.setLastName(user.getLastName());

        redirectAttributes.addFlashAttribute("message", "Profile updated successfully");
        modelAndView.setViewName("redirect:/account");
        return modelAndView;
    }
}
