package klu.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import klu.repository.UsersRepository;

@Service
public class UsersManager {
	
	@Autowired
	UsersRepository UR;
	
	@Autowired
	EmailManager EM;
	
	@Autowired
	JWTManager JWT;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	public String addUser(Users U) {    
	    if(UR.validateEmail(U.getEmail()) > 0)
	      return "401::Email already exist";    
	    
	    U.setPassword(passwordEncoder.encode(U.getPassword()));

        if (U.getRole() == null || U.getRole().isEmpty()) {
            U.setRole("USER"); 
        }

	    UR.save(U);
	    return "200::User Registered Successfully";
	}
	
	public String recoverPassword(String email) {
		Users U = UR.findById(email).orElse(null);
        if (U == null) {
            return "404::User not found";
        }
        String message = String.format("Dear %s, \n \n Password recovery was requested for your account. For security reasons, we cannot send your actual password. Please use the password reset feature if available, or contact support.", U.getFullname());
		return EM.sendEmail(U.getEmail(),"CarRental : Password Recovery",message);
	}
	
	public String validateCredentials(String email, String password) {
	    Users user = UR.findById(email).orElse(null);
        
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            String token = JWT.generateToken(email, user.getRole());
			return "200::"+token;
        } else {
            return "401:: INVALID CREDENTIALS - ( Check Username and Password )";
        }
    }
	
	public String getFullname(String token) {
		String email = JWT.validateToken(token);
		if(email.compareTo("401")==0)
			return "401::Token Expired! ";
        Users U = UR.findById(email).orElseThrow(() -> new RuntimeException("User not found for token: " + token)); 
		return U.getFullname();
	}
}

