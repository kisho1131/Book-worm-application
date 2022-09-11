package com.bookworm.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//https://covers.openlibrary.org/b/id/1-L.jpg
import java.util.Optional;

@Controller
public class BookController {

    private final String COVER_IMG_ROOT = "https://covers.openlibrary.org/b/id/";

    @Autowired
    private BookRepository bookRepository;

    @GetMapping(value = "/books/{bookId}")
    public String getBook(@PathVariable String bookId, Model model, @AuthenticationPrincipal OAuth2User principal){
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if(optionalBook.isPresent()){
            Book book = optionalBook.get();
            String coverImageUrl = "/images/not_found.png";
            if(book.getCoverIds()!= null & book.getCoverIds().size() > 0){
                coverImageUrl = COVER_IMG_ROOT + book.getCoverIds().get(0) + "-L.jpg";
            }
            if(principal != null && principal.getAttribute("login")!=null){
                model.addAttribute("loginId", principal.getAttribute("login"));
            }
            model.addAttribute("coverImage", coverImageUrl);
            model.addAttribute("book", book);
            return "book";
        }
        return "book-not-found";
    }
}
