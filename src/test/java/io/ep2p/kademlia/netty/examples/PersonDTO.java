package io.ep2p.kademlia.netty.examples;

import lombok.*;

import java.io.Serializable;
import java.math.BigInteger;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PersonDTO implements Serializable {
    private String name;
    private String surname;

}