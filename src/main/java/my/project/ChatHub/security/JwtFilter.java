package my.project.ChatHub.security;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import my.project.ChatHub.serviceImpl.CustomUserDetailsServiceImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final CustomUserDetailsServiceImpl userDetailsService;
    private final JwtUtil jwtUtil;

    public JwtFilter(CustomUserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    //detyra e filterit qe shef cdo kerkese dhe shijkon nese personi ka nje token valid apo jo
    //pra do thirret per nje kerkese dhe shikohet nese tokeni i kaluar eshte ne rregull , nese ky token i perket nje emaili
    //por useri me ket email jo i auten , merre userin nga databaza dhe autentikoje
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        //nga client side ajo cfaer e di qe do marr kur do bej kerkesen do jete : Bearer + tokenin e gjeneruar

//        String authHeader = request.getHeader("Authorization");
//        String email = null;
//        String jwt = null;


        String jwt = null;

        // Extract JWT from "accessToken" cookie
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    jwt = cookie.getValue();
                }
            }
        }

        if (jwt != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            String email = jwtUtil.getEmailFromToken(jwt);

//nqs kemi nje email  nuk eshte i autentikuar , i loguar
            if (email != null) {

                //kam vendosur me posjte si principal custom user details , do e perdor per id
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);

                if (jwtUtil.validateJwtToken(jwt)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    //i thote spring qe ky user eshte tashme i loguar per kete kerkese
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        filterChain.doFilter(request, response);
    }

}