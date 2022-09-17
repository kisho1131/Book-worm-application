package com.bookworm.home;

import com.bookworm.user.BookByUserRepository;
import com.bookworm.user.BooksByUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.query.CassandraPageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IndexController {

    @Autowired
    private BookByUserRepository bookByUserRepository;

    private final String COVER_IMG_ROOT = "https://covers.openlibrary.org/b/id/";

    @GetMapping("/")
    public String home(@AuthenticationPrincipal OAuth2User principal, Model model){
        if(principal == null || principal.getAttribute("login") == null){
            return "login-index";
        }
        String userId = principal.getAttribute("login");
        Slice<BooksByUser> booksSlice =  bookByUserRepository.findAllById(userId, CassandraPageRequest.of(0,15));
        List<BooksByUser> booksByUser = booksSlice.getContent();
        System.out.println(booksByUser);
        booksByUser = booksByUser.stream().distinct().map(book ->{
            String coverImageUrl = "/images/not_found.png";
            // System.out.println(coverImageUrl);
            if(book.getCoverIds() != null & book.getCoverIds().size() > 0){
                coverImageUrl = COVER_IMG_ROOT + book.getCoverIds().get(0)+"-L.jpg";
            }
            // System.out.println(coverImageUrl);
            book.setCoverUrl(coverImageUrl);
            return book;
        }).collect(Collectors.toList());
        model.addAttribute("userId", userId);
        model.addAttribute("books", booksByUser);
        return "home";
    }
}
