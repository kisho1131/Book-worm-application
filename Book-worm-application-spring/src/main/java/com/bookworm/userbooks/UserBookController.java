package com.bookworm.userbooks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.util.Objects;

@Controller
public class UserBookController {
    @Autowired
    private UserBooksRepository userBooksRepository;


    @PostMapping("/addUserBook")
    public ModelAndView addBookForUSer(@RequestBody MultiValueMap<String, String> formData, @AuthenticationPrincipal OAuth2User principal){
        if(principal == null || principal.getAttribute("login") == null){
            return null;
        }
        UserBooks userBooks = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();

        key.setUserId(principal.getAttribute("login"));
        System.out.println("User Id is : " + key.getUserId());
        String bookId = formData.getFirst("bookId");
        key.setBookId(bookId);
        userBooks.setUserBooksPrimaryKey(key);
        userBooks.setStartedDate(LocalDate.parse(Objects.requireNonNull(formData.getFirst("startDate"))));
        userBooks.setCompletedDate(LocalDate.parse(Objects.requireNonNull(formData.getFirst("completedDate"))));
        userBooks.setRating(Integer.parseInt(Objects.requireNonNull(formData.getFirst("rating"))));
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));
        userBooksRepository.save(userBooks);
        return new ModelAndView("redirect:/books/" + bookId);
    }
}
