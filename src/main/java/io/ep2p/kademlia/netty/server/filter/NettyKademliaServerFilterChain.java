package io.ep2p.kademlia.netty.server.filter;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class NettyKademliaServerFilterChain<K extends Serializable, V extends Serializable> {

    private final List<NettyKademliaServerFilter<K, V>> filters = new CopyOnWriteArrayList<>();

    public void addFilter(NettyKademliaServerFilter<K, V> kademliaServerFilter){
        filters.add(kademliaServerFilter);
        if (filters.size() - 1 != 0){
            filters.get(filters.size() - 2).setNext(kademliaServerFilter);
        }
    }

    public void addFilterAfter(Class<? extends NettyKademliaServerFilter<K, V>> clazz, NettyKademliaServerFilter<K, V> kademliaServerFilter){
        int indexOfClass = this.findIndexOfClass(clazz);
        int new_index = indexOfClass + 1;
        this.filters.add(new_index, kademliaServerFilter);
        if (new_index != 0){
            this.filters.get(new_index - 1).setNext(kademliaServerFilter);
        }
    }

    private int findIndexOfClass(Class<? extends NettyKademliaServerFilter<K, V>> clazz){
        for(int i=0; i<filters.size();i++){
            if (filters.get(i).getClass().equals(clazz)){
                return i;
            }
        }
        return filters.size() - 1;
    }

    public List<NettyKademliaServerFilter<K, V>> getFilters() {
        return filters;
    }
}
