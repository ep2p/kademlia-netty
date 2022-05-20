package configuration;

import server.DefaultKademliaNodeServerInitializer;
import server.KademliaNodeServerInitializerAPI;

import java.io.Serializable;

public class KademliaNodeServerInitializerAPIFactory {
    public <K extends Serializable, V extends Serializable> KademliaNodeServerInitializerAPI<K, V> getKademliaNodeServerInitializerAPI(){
        return new DefaultKademliaNodeServerInitializer<K, V>();
    }
}
