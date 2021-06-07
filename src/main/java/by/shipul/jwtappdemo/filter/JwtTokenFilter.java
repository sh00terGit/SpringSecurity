package by.shipul.jwtappdemo.filter;

import by.shipul.jwtappdemo.security.JwtTokenProvider;
import by.shipul.jwtappdemo.security.exception.JwtAuthException;
import lombok.RequiredArgsConstructor;
import org.omg.IOP.ServiceContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends GenericFilterBean {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = tokenProvider.resolveToken((HttpServletRequest) request);
        try {
            if (Objects.nonNull(token) && tokenProvider.validateToken(token)) {
                Authentication authentication = tokenProvider.getAuth(token);
                if (Objects.nonNull(authentication)) {
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtAuthException e) {
            SecurityContextHolder.clearContext();
            ((HttpServletResponse) response).sendError(e.getStatus().value());
            throw new JwtAuthException("JWT token is expired or invalid");
        }

        chain.doFilter(request, response);
    }
}
