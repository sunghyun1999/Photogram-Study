package com.cos.photogramstart.web;

import com.cos.photogramstart.config.auth.PrincipalDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserController {

    @GetMapping("/user/{id}")
    public String profile(@PathVariable int id) {
        return "user/profile";
    }

    @GetMapping("/user/{id}/update")
    public String update(@PathVariable int id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("세션정보 : " + principalDetails.getUser());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        PrincipalDetails principalDetails2 = (PrincipalDetails) auth.getPrincipal();
        System.out.println("직접 찾은 세션 정보 : " + principalDetails2.getUser());

        return "user/update";
    }
}
