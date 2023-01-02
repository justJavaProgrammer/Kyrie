package com.odeyalo.kyrie.core.oauth2.client;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default Oauth2ClientRepository implementation that uses memory as client store.
 * NOTE: The InMemoryOauth2ClientRepository clears after application stops and should be used only for development.
 *
 * @see Oauth2ClientRepository
 */
public class InMemoryOauth2ClientRepository implements Oauth2ClientRepository, Iterable<Oauth2Client> {
    private final Map<String, Oauth2Client> clients;

    /**
     * Initialize repository with array of clients
     * In this case as id will be used clientId property
     * @param clients - clients to register in repository
     */
    public InMemoryOauth2ClientRepository(Oauth2Client... clients) {
        this(Arrays.asList(clients));
    }

    /**
     * Initialize repository with existing clients
     *
     * @param clients - default clients to set
     */
    public InMemoryOauth2ClientRepository(Map<String, Oauth2Client> clients) {
        this.clients = clients;
    }

    /**
     * Initialize repository with list of clients
     * In this case clientId will be used as id for 'clients'
     * @param clients - clients to register in repository
     */
    public InMemoryOauth2ClientRepository(@Nonnull List<Oauth2Client> clients) {
        this.clients = clients.stream().collect(Collectors.toMap(Oauth2Client::getClientId, Function.identity()));
    }

    @Override
    public Oauth2Client findOauth2ClientById(String clientId) {
        return clients.get(clientId);
    }

    /**
     * Iterator to iterate through all clients
     *
     * @return - iterator with Oauth2Client(s)
     */
    @Override
    @Nonnull
    public Iterator<Oauth2Client> iterator() {
        return clients.values().iterator();
    }
}
