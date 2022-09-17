package com.bookworm.userbooks;

import com.bookworm.books.Book;
import com.bookworm.books.BookRepository;
import com.bookworm.user.BookByUserRepository;
import com.bookworm.user.BooksByUser;
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
import java.util.Optional;

@Controller
public class UserBookController {
    @Autowired
    private UserBooksRepository userBooksRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookByUserRepository booksByUserRepository;

    @PostMapping("/addUserBook")
    public ModelAndView addBookForUSer(@RequestBody MultiValueMap<String, String> formData, @AuthenticationPrincipal OAuth2User principal){
        if(principal == null || principal.getAttribute("login") == null){
            return null;
        }
        String bookId = formData.getFirst("bookId");
        assert bookId != null;
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (!optionalBook.isPresent()) {
            return new ModelAndView("redirect:/");
        }
        Book book = optionalBook.get();

        UserBooks userBooks = new UserBooks();
        UserBooksPrimaryKey key = new UserBooksPrimaryKey();
        int rating = Integer.parseInt(Objects.requireNonNull(formData.getFirst("rating")));
        String userId = principal.getAttribute("login");
        key.setUserId(principal.getAttribute("login"));
        System.out.println("User Id is : " + key.getUserId());
        // String bookId = formData.getFirst("bookId");
        key.setBookId(bookId);
        userBooks.setUserBooksPrimaryKey(key);
        userBooks.setStartedDate(LocalDate.parse(Objects.requireNonNull(formData.getFirst("startDate"))));
        userBooks.setCompletedDate(LocalDate.parse(Objects.requireNonNull(formData.getFirst("completedDate"))));
        userBooks.setRating(Integer.parseInt(Objects.requireNonNull(formData.getFirst("rating"))));
        userBooks.setReadingStatus(formData.getFirst("readingStatus"));
        userBooksRepository.save(userBooks);

        BooksByUser booksByUser = new BooksByUser();
        booksByUser.setId(userId);
        booksByUser.setBookId(bookId);
        booksByUser.setBookName(book.getName());
        booksByUser.setCoverIds(book.getCoverIds());
        booksByUser.setAuthorNames(book.getAuthorNames());
        booksByUser.setReadingStatus(formData.getFirst("readingStatus"));
        booksByUser.setRating(rating);
        booksByUserRepository.save(booksByUser);

        return new ModelAndView("redirect:/books/" + bookId);
    }
}
