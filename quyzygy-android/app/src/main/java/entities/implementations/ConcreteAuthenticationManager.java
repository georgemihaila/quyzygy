package entities.implementations;

import entities.AuthenticationResult;
import entities.RegistrationResult;
import entities.UserType;
import entities.interfaces.AuthenticationManager;

/*
Represents a class that provides authentication functionality.
 */
public class ConcreteAuthenticationManager implements AuthenticationManager {
    /*Returns a boolean indicating whether the user is authenticated.*/
    public boolean isAuthenticated(){
        return false;
    }

    /*Tries to authenticate a user using an email acdress and a password.*/
    public AuthenticationResult signIn(String email, String password){
        return AuthenticationResult.AUTHENTICATED;
    }

    /*Tries to register a user and returns a RegistrationResult indicating whether the action was successful.*/
    public RegistrationResult registerUser(String email, String password, String name, UserType userType){
        return RegistrationResult.SUCCESS;
    }
}
