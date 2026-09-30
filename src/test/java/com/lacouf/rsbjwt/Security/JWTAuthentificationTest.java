package com.lacouf.rsbjwt.Security;

import com.lacouf.rsbjwt.model.Student;
import com.lacouf.rsbjwt.model.auth.Role;
import com.lacouf.rsbjwt.repository.UserAppRepository;
import com.lacouf.rsbjwt.security.AuthProvider;
import com.lacouf.rsbjwt.security.JwtTokenProvider;
import com.lacouf.rsbjwt.security.exception.AuthenticationException;
import com.lacouf.rsbjwt.security.exception.InvalidJwtTokenException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JWTAuthentificationTest {

    private static final String JWT_SECRET = "2B7E151628AED2A6ABF7158809CF4F3C2B7E151628AED2A6ABF7158809CF4F3C";

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() throws Exception {
        jwtTokenProvider = new JwtTokenProvider();
        setField(jwtTokenProvider, "expirationInMs", 600_000);
        setField(jwtTokenProvider, "jwtSecret", JWT_SECRET);
    }

    @Test
    @DisplayName("JWT token round trip returns the user email")
    void generateToken_shouldReturnSubject() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "student@example.com",
                "secret",
                java.util.List.of(new SimpleGrantedAuthority("STUDENT"))
        );

        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertEquals("student@example.com", jwtTokenProvider.getEmailFromJWT(token));
    }

    @Test
    @DisplayName("JWT validation accepts a valid token")
    void validateToken_shouldAcceptValidToken() {
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "student@example.com",
                "secret",
                java.util.List.of(new SimpleGrantedAuthority("STUDENT"))
        );

        String token = jwtTokenProvider.generateToken(authentication);

        assertDoesNotThrow(() -> jwtTokenProvider.validateToken(token));
    }

    @Test
    @DisplayName("JWT validation rejects malformed and expired tokens")
    void validateToken_shouldRejectInvalidTokens() {
        String malformedToken = "not-a-jwt";
        String expiredToken = buildToken("expired@example.com", Date.from(Instant.now().minusSeconds(120)), Date.from(Instant.now().minusSeconds(10)));

        assertThrows(InvalidJwtTokenException.class, () -> jwtTokenProvider.validateToken(malformedToken));
        assertThrows(InvalidJwtTokenException.class, () -> jwtTokenProvider.validateToken(expiredToken));
    }

    @Test
    @DisplayName("AuthProvider accepts valid credentials")
    void authenticate_shouldAcceptValidCredentials() {
        UserAppRepository userAppRepository = mock(UserAppRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthProvider authProvider = new AuthProvider(passwordEncoder, userAppRepository);

        Student user = Student.builder()
                .id(1L)
                .firstName("Alice")
                .lastName("Martin")
                .email("alice@example.com")
                .password("encodedPassword")
                .matricule("A123")
                .programe(null)
                .build();

        when(userAppRepository.findUserAppByEmail("alice@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("plainPassword", "encodedPassword")).thenReturn(true);

        Authentication authentication = authProvider.authenticate(
                new UsernamePasswordAuthenticationToken("alice@example.com", "plainPassword")
        );

        assertEquals("alice@example.com", authentication.getName());
        assertTrue(authentication.getAuthorities().contains(new SimpleGrantedAuthority("STUDENT")));
    }

    @Test
    @DisplayName("AuthProvider rejects invalid credentials")
    void authenticate_shouldRejectInvalidCredentials() {
        UserAppRepository userAppRepository = mock(UserAppRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        AuthProvider authProvider = new AuthProvider(passwordEncoder, userAppRepository);

        Student user = Student.builder()
                .id(2L)
                .firstName("Bob")
                .lastName("Lemoine")
                .email("bob@example.com")
                .password("encodedPassword")
                .matricule("B456")
                .programe(null)
                .build();

        when(userAppRepository.findUserAppByEmail("bob@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(AuthenticationException.class, () ->
                authProvider.authenticate(new UsernamePasswordAuthenticationToken("bob@example.com", "wrongPassword"))
        );
    }

    private String buildToken(String email, Date issuedAt, Date expiration) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(java.util.HexFormat.of().parseHex(JWT_SECRET)), SignatureAlgorithm.HS256)
                .compact();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = JwtTokenProvider.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}

