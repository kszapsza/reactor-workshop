package com.nurkiewicz.webflux.demo.feed;

import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.data.annotation.Id;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

public class Article {

    @Id
    private final URI link;

    private final Instant publishedDate;
    private final String title;

    public Article(URI link, Instant publishedDate, String title) {
        this.link = link;
        this.publishedDate = publishedDate;
        this.title = title;
    }

    public URI getLink() {
        return link;
    }

    public Instant getPublishedDate() {
        return publishedDate;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString() {
        return "Article{" +
                "link=" + link +
                ", publishedDate=" + publishedDate +
                ", title='" + title + '\'' +
                '}';
    }

    public static Mono<Article> fromSyndEntry(SyndEntry entry) {
        return Mono.fromCallable(() -> {
            if (entry == null || entry.getPublishedDate() == null) {
                return null;
            }
            return new Article(new URI(entry.getLink()), entry.getPublishedDate().toInstant(), entry.getTitle());
        });
    }
}
