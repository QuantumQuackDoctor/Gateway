package com.smoothstack.SSGateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AuthenticationFilterTest {

    @Value("${jwt.secret}")
    private String secret;

    @Autowired
    private AuthenticationFilter filter;

    @Test
    void testValid() {
        System.out.println(secret);
        //create fake jwt valid
        String validJwt = createJwt((long) -2, "user", new Date(System.currentTimeMillis() + 1000 * 60));

        //create requests to put through filter
        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost:8080/secured")
                .header("Authorization", validJwt).build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain filterChain = mock(GatewayFilterChain.class);

        ArgumentCaptor<ServerWebExchange> captor = ArgumentCaptor.forClass(ServerWebExchange.class);
        when(filterChain.filter(captor.capture())).thenReturn(Mono.empty());

        filter.filter(exchange, filterChain);

        ServerWebExchange resultExchange = captor.getValue();

        assertTrue(resultExchange.getRequest().getHeaders().containsKey("id"));
        assertTrue(resultExchange.getRequest().getHeaders().containsKey("role"));
        assertTrue(resultExchange.getRequest().getHeaders().containsKey("email"));

    }

    @Test
    void testInvalid() {
        //create fake jwt expired
        String invalidJwt = createJwt((long) -2, "user", new Date(System.currentTimeMillis()));

        //create requests to put through filter
        MockServerHttpRequest request = MockServerHttpRequest.get("http://localhost:8080/secured")
                .header("Authorization", invalidJwt).build();

        ServerWebExchange exchange = MockServerWebExchange.from(request);

        GatewayFilterChain filterChain = exchange1 -> { //filter should not call chain
            fail();
            return null;
        };

        filter.filter(exchange, filterChain); //filter returns set complete and sets exchange response

        assertEquals(exchange.getResponse().getStatusCode(), HttpStatus.UNAUTHORIZED);
    }

    private String createJwt(Long id, String role, Date expiration) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        claims.put("role", role);

        return Jwts.builder().setSubject("email")
                .addClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }
}