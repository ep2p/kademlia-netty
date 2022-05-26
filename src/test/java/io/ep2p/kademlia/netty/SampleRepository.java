package io.ep2p.kademlia.netty;

import io.ep2p.kademlia.repository.KademliaRepository;

import java.util.HashMap;
import java.util.Map;

public class SampleRepository implements KademliaRepository<String, String> {
    protected final Map<String, String> data = new HashMap<>();

    @Override
    public void store(String key, String value) {
        data.putIfAbsent(key, value);
    }

    @Override
    public String get(String key) {
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
