package com.nurkiewicz.webflux.demo.feed;

import com.rometools.opml.feed.opml.Outline;
import com.rometools.rome.io.FeedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Component
public class FeedAggregator {

    private static final Logger log = LoggerFactory.getLogger(FeedAggregator.class);

    private final OpmlReader opmlReader;
    private final FeedReader feedReader;
    private final ArticleRepository articleRepository;

    public FeedAggregator(
            OpmlReader opmlReader,
            FeedReader feedReader,
            ArticleRepository articleRepository
    ) {
        this.opmlReader = opmlReader;
        this.feedReader = feedReader;
        this.articleRepository = articleRepository;
    }

    /**
     * TODO (4) Read all feeds and store them into database
     * TODO (5) Repeat periodically, do not store duplicates
     */
    @PostConstruct
    public void init() throws IOException, FeedException {
        Flux.interval(Duration.ZERO, Duration.ofSeconds(30))
                .flatMap(e -> opmlReader.allFeedsStream())
                .map(Outline::getXmlUrl)
                .flatMap(url -> Mono.fromCallable(() -> new URL(url)))
                .subscribe(url -> feedReader.fetch(url)
                        .flatMap(Article::fromSyndEntry)
                        .filterWhen(article -> articleRepository.existsById(article.getLink()).map(it -> !it))
                        .subscribe(entity -> articleRepository.insert(entity)
                                .subscribe(e -> log.info("Stored to db :: {}: {} at {}", e.getPublishedDate(), e.getTitle(), e.getLink()))
                        )
                );
    }
}
