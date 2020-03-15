package com.theodore.controller;

import com.theodore.entity.Resume;
import com.theodore.service.ResumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/resume")
public class ResumeController {

    @Autowired
    private ResumeService resumeService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(String username, String password, Model model, HttpServletRequest request) {
        List<Resume> resumeList = resumeService.findAll();
        model.addAttribute("resumeList", resumeList);
        return "success";
        /*if ("admin".equals(username) && "admin".equals(password)) {
            request.getSession().setAttribute("user", "admin");

        } else {
            return "error";
        }*/
    }




    @RequestMapping("/edit")
    public String edit(Long id, Model model) {
        if (id != null){
            Resume resume = resumeService.findOne(id);
            model.addAttribute("resume", resume);
        }
        return "edit";
    }

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(Resume resume, Model model) {

        System.out.println(resume);
        resumeService.save(resume);
        List<Resume> resumeList = resumeService.findAll();
        model.addAttribute("resumeList", resumeList);
        return "success";
    }

    @RequestMapping("/delete")
    public String delete(Long id, Model model){
        resumeService.delete(id);
        List<Resume> resumeList = resumeService.findAll();
        model.addAttribute("resumeList", resumeList);
        return "success";
    }

}
