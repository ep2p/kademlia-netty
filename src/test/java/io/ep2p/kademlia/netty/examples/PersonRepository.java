package io.ep2p.kademlia.netty.examples;

import io.ep2p.kademlia.repository.KademliaRepository;

import java.util.HashMap;
import java.util.Map;

public class PersonRepository implements KademliaRepository<String, PersonDTO> {
    protected final Map<String, PersonDTO> data = new HashMap<>();

    @Override
    public void store(String key, PersonDTO value) {
        data.putIfAbsent(key, value);
    }

    @Override
    public PersonDTO get(String key) {
        return data.get(key);
    }

    @Override
    public void remove(String key) {
        data.remove(key);
    }

    @Override
    public boolean contains(String key) {
        return data.containsKey(key);
    }
}
