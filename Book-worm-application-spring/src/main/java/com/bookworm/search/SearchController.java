package com.bookworm.search;

import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SearchController {

    private final WebClient webClient;
    private final String COVER_IMG_ROOT = "https://covers.openlibrary.org/b/id/";

    public SearchController(WebClient.Builder webClientBuilder){
        this.webClient = webClientBuilder.exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16*1024*1024))
                        .build()).baseUrl("https://openlibrary.org/search.json").build();
    }

    @GetMapping(value = "/search")
    public String getSearchResult(@RequestParam String query, Model model){
        Mono<SearchResult> resultsMono = this.webClient.get()
                .uri("?q={query}", query)
                .retrieve().bodyToMono(SearchResult.class);
        SearchResult result = resultsMono.block();
        List<SearchResultBook> books = result.getDocs()
                .stream()
                .limit(20)
                .map(bookResult -> {
                    bookResult.setKey(bookResult.getKey().replace("/works/", ""));
                    String coverId = bookResult.getCover_i();
                    if(StringUtils.hasText(coverId)){
                        coverId = COVER_IMG_ROOT + coverId + "-M.jpg";
                    }else{
                        coverId = "/images/not_found.png";
                    }
                    bookResult.setCover_i(coverId);
                    return bookResult;
                }).collect(Collectors.toList());
        model.addAttribute("searchResult", books);
        return "search";
    }
}
