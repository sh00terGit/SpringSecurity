package by.shipul.jwtappdemo.rest;

import by.shipul.jwtappdemo.dto.AuthenticationRequestDto;
import by.shipul.jwtappdemo.model.User;
import by.shipul.jwtappdemo.repository.UserRepository;
import by.shipul.jwtappdemo.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthRestControllerV1 {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    @PostMapping("/login")
    public ResponseEntity<?> auth (@RequestBody AuthenticationRequestDto request) {
        try{
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(()->new UsernameNotFoundException("user not found"));
            String token = tokenProvider.createToken(user.getEmail(),user.getRole().name());
            Map<Object,Object> response = new HashMap<>();
            response.put("token",token);
            response.put("email",user.getEmail());
            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return new ResponseEntity<>("invalid email/password combination", HttpStatus.FORBIDDEN);
        }
    }


    @PostMapping("/logout")
    public void authLogin (HttpServletRequest request, HttpServletResponse response) {
        SecurityContextLogoutHandler handler = new SecurityContextLogoutHandler();
        handler.logout(request,response,null);
    }
}
