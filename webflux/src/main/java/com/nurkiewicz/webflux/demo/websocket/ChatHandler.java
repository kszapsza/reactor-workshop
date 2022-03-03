package com.nurkiewicz.webflux.demo.websocket;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

/**
 * TODO
 * <ol>
 *     <li>Use single sink to publish incoming messages</li>
 *     <li>Broadcast that sink to all listening subscribers</li>
 *     <li>New subscriber should receive last 5 messages before joining</li>
 *     <li>Add some logging: connecting/disconnecting, how many subscribers</li>
 * </ol>
 * Hint: Sink should hold {@link String}s, not {@link WebSocketMessage}s
 */
public class ChatHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ChatHandler.class);

	private final Sinks.Many<String> sink = Sinks.many().replay().limit(5);

    @Override
    public @NotNull Mono<Void> handle(WebSocketSession session) {
        //noinspection CallingSubscribeInNonBlockingScope
        session.receive()
				.map(WebSocketMessage::getPayloadAsText)
				.doOnNext(x -> log.info("[{}] Received: '{}'", session.getId(), x))
				.subscribe(sink::tryEmitNext);

        final Flux<WebSocketMessage> outMessages = sink.asFlux()
                .map(session::textMessage)
                .doOnSubscribe(s -> log.info("Got new connection {}", session))
                .doOnComplete(() -> log.info("Connection completed {}", session));

        return session.send(outMessages);
    }

}
