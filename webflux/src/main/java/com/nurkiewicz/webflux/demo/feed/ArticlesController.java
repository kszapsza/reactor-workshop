package com.nurkiewicz.webflux.demo.feed;

import org.springframework.data.domain.Sort;
import reactor.core.publisher.Flux;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/articles")
public class ArticlesController {

    private final ArticlesStream articlesStream;
    private final ArticleRepository articleRepository;

    public ArticlesController(ArticlesStream articlesStream, ArticleRepository articleRepository) {
        this.articlesStream = articlesStream;
        this.articleRepository = articleRepository;
    }

    /**
     * TODO (6) Return newest articles
     */
    @GetMapping("/newest/{limit}")
    Flux<Article> newest(@PathVariable int limit) {
        return articleRepository
                .findAll()
                .sort(Comparator.comparing(Article::getPublishedDate, Comparator.reverseOrder()))
                .take(limit);
    }

    /**
     * TODO (8) Create an SSE stream of newest articles
     */
    @GetMapping(value = "/newest-stream", produces = TEXT_EVENT_STREAM_VALUE)
    Flux<Article> streamNew() {
        return Flux.empty();
    }

}
