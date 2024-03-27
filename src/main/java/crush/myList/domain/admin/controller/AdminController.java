package crush.myList.domain.admin.controller;

import crush.myList.domain.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/index")
    public String home(Model model) {
        model.addAttribute("memberList", adminService.getAllMembersOrderByCreatedDateDesc());
        model.addAttribute("allPlayList", adminService.getAllPlaylistsOrderByCreatedDateDesc());
        model.addAttribute("formList", adminService.getAllFormsOrderByCreatedDateDesc());

        return "index";
    }
}
