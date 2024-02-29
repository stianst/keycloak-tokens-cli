package org.keycloak.cli.mock;

import io.quarkus.test.Mock;
import jakarta.enterprise.context.ApplicationScoped;
import org.keycloak.cli.interact.InteractService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Mock
@ApplicationScoped
public class MockInteractService extends InteractService {

    private BlockingQueue<String> messages = new LinkedBlockingQueue<>();

    @Override
    public void println(String message) {
        System.out.println(message);
        this.messages.add(message);
    }

    public String poll(long timeout) throws InterruptedException {
        return messages.poll(timeout, TimeUnit.SECONDS);
    }

}
