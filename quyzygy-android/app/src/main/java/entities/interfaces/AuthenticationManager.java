package entities.interfaces;

import entities.AuthenticationResult;
import entities.RegistrationResult;
import entities.UserType;

/*
Represents a class that provides authentication functionality.
 */
public interface AuthenticationManager {
    /*Returns a boolean indicating whether the user is authenticated.*/
    boolean isAuthenticated();

    /*Tries to authenticate a user using an email acdress and a password.*/
    AuthenticationResult signIn(String email, String password);

    /*Tries to register a user and returns a RegistrationResult indicating whether the action was successful.*/
    RegistrationResult registerUser(String email, String password, String name, UserType userType);
}
