package com.bookworm.userbooks;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserBookController {

    @PostMapping("/addUserBook")
    public String addBookForUSer(@RequestBody MultiValueMap<String, String> formData, @AuthenticationPrincipal OAuth2User principal){

        return "";
    }
}
