package entities;

import entities.implementations.ConcreteAuthenticationManager;

/*Represents a container for global properties.*/
public class Globals {
    public static final ConcreteAuthenticationManager AuthManager = new ConcreteAuthenticationManager();
}
