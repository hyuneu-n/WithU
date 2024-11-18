package com.danpoong.withu.config.auth.jwt;

import com.danpoong.withu.user.domain.User;
import com.danpoong.withu.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserRepository userRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
          throws ServletException, IOException {
    try {
      final String authorizationHeader = request.getHeader("Authorization");
      String email = null;
      String jwt = null;

      if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
        jwt = authorizationHeader.substring(7);
        email = jwtUtil.extractEmail(jwt);
      }

      if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        User user = userRepository.findByEmail(email).orElse(null);

        if (user != null && jwtUtil.validateToken(jwt, user.getEmail())) {
          UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                  user.getEmail(),
                  "",
                  Collections.singletonList(new SimpleGrantedAuthority(user.getRole()))
          );

          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                  userDetails, null, userDetails.getAuthorities());
          authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
          log.warn("Invalid JWT Token for user: {}", email);
        }
      }
    } catch (Exception e) {
      log.warn("JWT Authentication Error: {}", e.getMessage());
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }

    chain.doFilter(request, response);
  }
}
