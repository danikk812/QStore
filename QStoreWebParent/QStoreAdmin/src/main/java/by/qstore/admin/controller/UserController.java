package by.qstore.admin.controller;

import by.qstore.admin.exception.UserNotFoundException;
import by.qstore.admin.export.UserExcelExporter;
import by.qstore.admin.service.UserService;
import by.qstore.admin.util.FileUtil;
import by.qstore.common.entity.Role;
import by.qstore.common.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/users")
    public ModelAndView listFirstPage(ModelAndView modelAndView) {
        return listByPage(1, "firstName", "asc", null, modelAndView);
    }

    @GetMapping("users/page/{pageNum}")
    public ModelAndView listByPage(@PathVariable("pageNum") int pageNum, @RequestParam("sortBy") String sortBy,
                                   @RequestParam("sortOrder") String sortOrder, @RequestParam("keyword") String keyword,
                                   ModelAndView modelAndView) {
        Page<User> page = userService.listByPage(pageNum, sortBy, sortOrder, keyword);
        List<User> userList = page.getContent();

        long startCount = getPageStartCount(pageNum);
        long endCount = getPageEndCount(pageNum, page);

        String reverseSortOrder = sortOrder.equals("asc") ? "desc" : "asc";

        modelAndView.addObject("totalPages", page.getTotalPages());
        modelAndView.addObject("currentPage", pageNum);
        modelAndView.addObject("startCount", startCount);
        modelAndView.addObject("endCount", endCount);
        modelAndView.addObject("totalUsers", page.getTotalElements());


        modelAndView.addObject("sortBy", sortBy);
        modelAndView.addObject("sortOrder", sortOrder);
        modelAndView.addObject("reverseSortOrder", reverseSortOrder);

        modelAndView.addObject("keyword", keyword);

        modelAndView.addObject("userList", userList);

        modelAndView.setViewName("users");
        return modelAndView;
    }

    @GetMapping("/users/new")
    public ModelAndView addUser(ModelAndView modelAndView) {
        List<Role> roleList = userService.listRoles();
        User user = new User();
        user.setEnabled(true);

        modelAndView.addObject("userForm", user);
        modelAndView.addObject("roleList", roleList);
        modelAndView.addObject("pageTitle", "Create New User");

        modelAndView.setViewName("add_user");
        return modelAndView;
    }

    @PostMapping("/users/save")
    public ModelAndView saveUser(@ModelAttribute("userForm") User user, @RequestParam("image") MultipartFile multipartFile,
                                 RedirectAttributes redirectAttributes, ModelAndView modelAndView) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            user.setPhotos(fileName);
            User savedUser = userService.save(user);
            String uploadDir = "user-photos/" + savedUser.getId();

            FileUtil.cleanDir(uploadDir);
            FileUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            if (user.getPhotos().isEmpty()) {
                user.setPhotos(null);
                userService.save(user);
            }
        }
        userService.save(user);
        redirectAttributes.addFlashAttribute("message", "The user has been saved successfully");
        modelAndView.setViewName(getRedirectURLtoUser(user));
        return modelAndView;
    }


    @GetMapping("/users/edit/{id}")
    public ModelAndView editUser(@PathVariable(name = "id") long id, RedirectAttributes redirectAttributes,
                                 ModelAndView modelAndView) {
        try {
            User userById = userService.getById(id);
            List<Role> roleList = userService.listRoles();

            modelAndView.addObject("userForm", userById);
            modelAndView.addObject("roleList", roleList);
            modelAndView.addObject("pageTitle", "Edit User #" + id);

            modelAndView.setViewName("add_user");
            return modelAndView;
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            modelAndView.setViewName("redirect:/users");
            return modelAndView;
        }
    }

    @GetMapping("/users/delete/{id}")
    public ModelAndView deleteUser(@PathVariable(name = "id") long id, RedirectAttributes redirectAttributes,
                                   ModelAndView modelAndView) {
        try {
            userService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The user with id " + id + " has been deleted successfully");
        } catch (UserNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }
        modelAndView.setViewName("redirect:/users");
        return modelAndView;
    }

    @GetMapping("/users/{id}")
    public ModelAndView updateUserEnabledStatus(@PathVariable("id") Long id, @RequestParam("enabled") boolean enabled,
                                                RedirectAttributes redirectAttributes, ModelAndView modelAndView) {
        userService.updateUserEnableStatus(id, enabled);
        String status = enabled ? "enabled" : "disabled";
        redirectAttributes.addFlashAttribute("message", "The user with id " + id + " has been " + status);
        modelAndView.setViewName("redirect:/users");
        return modelAndView;
    }

    @GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        List<User> userList = userService.listAll();

        UserExcelExporter exporter = new UserExcelExporter();
        exporter.export(userList, response);
    }



    private long getPageStartCount(int pageNum) {
        return (long) (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
    }

    private long getPageEndCount(int pageNum, Page<User> page) {
        long endCount = getPageStartCount(pageNum) + UserService.USERS_PER_PAGE - 1;
        if (endCount > page.getTotalElements()) {
            endCount = page.getTotalElements();
        }
        return endCount;
    }

    private String getRedirectURLtoUser(User user) {
        String emailName = user.getEmail().split("@")[0];
        return "redirect:/users/page/1?sortBy=id&sortOrder=asc&keyword=" + emailName;
    }
}
