package com.nurkiewicz.webflux.demo.feed;

import com.rometools.opml.feed.opml.Opml;
import com.rometools.opml.feed.opml.Outline;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.WireFeedInput;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

@Component
public class OpmlReader {

    public final String feedFile;

    public OpmlReader(@Value("${feed-file}") String feedFile) {
        this.feedFile = feedFile;
    }

    public Flux<Outline> allFeedsStream() {
        return Mono.fromCallable(this::allFeeds)
                .flatMapIterable(Function.identity());
    }

    public List<Outline> allFeeds() throws FeedException, IOException {
        WireFeedInput input = new WireFeedInput();
        try (final InputStream inputStream = OpmlReader.class.getResourceAsStream(feedFile)) {
            assert inputStream != null;
            final InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            final Reader reader = new BufferedReader(streamReader);
            Opml feed = (Opml) input.build(reader);
            return feed.getOutlines();
        }
    }

}
