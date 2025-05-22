package com.linuxdo.cdkdistribution.controller;

import com.linuxdo.cdkdistribution.model.Cdk;
import com.linuxdo.cdkdistribution.model.CdkActivity;
import com.linuxdo.cdkdistribution.model.User;
import com.linuxdo.cdkdistribution.service.CdkActivityService;
import com.linuxdo.cdkdistribution.service.CdkService;
import com.linuxdo.cdkdistribution.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class WebController {

    private final CdkActivityService cdkActivityService;
    private final CdkService cdkService;
    private final UserService userService;

    @Autowired
    public WebController(
            CdkActivityService cdkActivityService,
            CdkService cdkService,
            UserService userService) {
        this.cdkActivityService = cdkActivityService;
        this.cdkService = cdkService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            Model model) {
        
        User currentUser = userService.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        
        Page<CdkActivity> activities;
        if (currentUser != null) {
            activities = cdkActivityService.getAvailableActivities(currentUser, pageable);
        } else {
            activities = cdkActivityService.getPublicActiveActivities(pageable);
        }
        
        model.addAttribute("activities", activities);
        return "index";
    }

    @GetMapping("/activity/{id}")
    public String activityDetail(@PathVariable Long id, Model model) {
        Optional<CdkActivity> activityOpt = cdkActivityService.getActivityById(id);
        
        if (activityOpt.isEmpty()) {
            return "redirect:/";
        }
        
        User currentUser = userService.getCurrentUser();
        CdkActivity activity = activityOpt.get();
        
        model.addAttribute("activity", cdkActivityService.convertToDto(activity, currentUser));
        
        return "activity-detail";
    }

    @GetMapping("/publish")
    public String publish(Model model) {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/";
        }
        
        // Check if user has sufficient trust level
        if (currentUser.getTrustLevel() < 2) {
            return "redirect:/";
        }
        
        return "publish";
    }

    @GetMapping("/my-cdks")
    public String myCdks(Model model) {
        User currentUser = userService.getCurrentUser();
        
        if (currentUser == null) {
            return "redirect:/";
        }
        
        // Get claimed CDKs
        List<Cdk> claimedCdks = cdkService.getCdksClaimedByUser(currentUser);
        model.addAttribute("claimedCdks", claimedCdks);
        
        // Get published activities
        Pageable pageable = PageRequest.of(0, 100, Sort.by("createdAt").descending());
        Page<CdkActivity> publishedActivities = cdkActivityService.getActivitiesByCreator(currentUser, pageable);
        model.addAttribute("publishedActivities", publishedActivities.getContent());
        
        return "my-cdks";
    }

    @GetMapping("/error")
    public String error(@RequestParam(required = false) String message, Model model) {
        model.addAttribute("errorMessage", message);
        return "error";
    }
}
