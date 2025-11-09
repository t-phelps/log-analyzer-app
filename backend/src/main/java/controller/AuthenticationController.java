package controller;

import dto.LoginRequest;
import dto.CreatAccountRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import service.AuthenticationService;

@Controller
@RestController("/auth")
public class AuthenticationController {

    AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    /**
     * Endpoint for logging a user in
     * @param loginRequest - login request containing credentials of the user
     * @return - response containing success or failure and their signed jwt if passed
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if(loginRequest.username().isEmpty() || loginRequest.password().isEmpty()) {
            return ResponseEntity.badRequest().body("Failed Login: A field within the request is empty");
        }

        try{
            ResponseCookie cookie = authenticationService.authenticateUser(loginRequest);
            return  ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body("User login successful");
        }catch(Exception e){
            return ResponseEntity.badRequest().build();
        }
    }


    @PostMapping("/create")
    public ResponseEntity<?> createAccount(@RequestBody CreatAccountRequest request){
        if(request.email().isEmpty() || request.username().isEmpty() || request.password().isEmpty()) {
            return ResponseEntity.badRequest().body("Failed To Create Account: A field within the request is empty");
        }

        try{
            ResponseCookie cookie =
        }catch(Exception e){
            return ResponseEntity.badRequest().body("Failed To Create Account");
        }
    }
}
