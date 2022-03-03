package com.nurkiewicz.webflux.demo.feed;

import com.google.common.io.CharStreams;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class FeedReader {

    private final WebClient webClient;

    FeedReader(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * TODO (3) Return <code>Flux&lt;SyndEntry&gt;</code>
     * Start by replacing {@link #get(URL)} with {@link #getAsync(URL)}.
     */
    public Flux<SyndEntry> fetch(URL url) {
        return getAsync(url)
                .map(feedBody -> new ByteArrayInputStream(applyAtomNamespaceFix(feedBody).getBytes(UTF_8)))
                .flatMap(is -> Mono.fromCallable(() -> DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is)))
                .flatMap(document -> Mono.fromCallable(() -> new SyndFeedInput().build(document)))
                .flatMapIterable(SyndFeed::getEntries);
    }

    private String applyAtomNamespaceFix(String feedBody) {
        return feedBody.replace("https://www.w3.org/2005/Atom", "http://www.w3.org/2005/Atom");
    }

    private String get(URL url) throws IOException {
        final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == HttpStatus.MOVED_PERMANENTLY.value()) {
            return get(new URL(conn.getHeaderField("Location")));
        }
        try (final InputStreamReader reader = new InputStreamReader(conn.getInputStream(), UTF_8)) {
            return CharStreams.toString(reader);
        }
    }

    /**
     * TODO (2) Load data asynchronously using {@link org.springframework.web.reactive.function.client.WebClient}
     *
     * @see <a href="https://stackoverflow.com/questions/47655789/how-to-make-reactive-webclient-follow-3xx-redirects">How to make reactive webclient follow 3XX-redirects?</a>
     */
    Mono<String> getAsync(URL url) {
        return webClient
                .get()
                .uri(url.toString())
                .retrieve()
                .bodyToMono(String.class);
    }

}
