package by.shipul.jwtappdemo.security;

import by.shipul.jwtappdemo.security.exception.JwtAuthException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${token.secret}")
    private String secretKey;

    @Value("${token.validity}")
    private Integer valititySeconds;


    @Value("${token.header}")
    private String header;

    @Qualifier("userDetailsServiceImpl")
    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init(){
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    public String createToken(String userName, String role){
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("role",role);
        Date now = new Date();
        Date validity = new Date(now.getTime() + valititySeconds * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(validity)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secretKey).compact();
    }

    public boolean validateToken(String token){
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(new Date());
        }catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthException("Auth expired or invalid", HttpStatus.UNAUTHORIZED);
        }
    }

    public Authentication getAuth(String token){
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(getUserName(token));
        return new UsernamePasswordAuthenticationToken(userDetails,"",userDetails.getAuthorities());
    }

    public String resolveToken(HttpServletRequest request){
        return request.getHeader(header);
    }

    public String getUserName(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
