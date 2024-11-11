package Online.Shopping.Platform.api.gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final String jwtSecret = "secret@1234secret@1234secret@1234";  // Use the same key for signing JWTs
    private final RestTemplate restTemplate;
    private String id;
    private Double cost;

    @Autowired
    private UserRoleService userRoleService;

    public AuthController(AuthenticationManager authenticationManager,
                          RestTemplate restTemplate) {
        this.authenticationManager = authenticationManager;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/authenticate")
    public Map<String, String> authenticate(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            List<String> roles = userRoleService.getRolesByUsername(username);

            // Generate JWT token
            String token = Jwts.builder()
                    .setSubject(((User) authentication.getPrincipal()).getUsername())
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24-hour expiration
                    .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                    .compact();

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            return tokenMap;

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

    @PostMapping("/smartphones")
    public Map<String, String> smartphones(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            List<String> roles = userRoleService.getRolesByUsername(username);
            // Generate JWT token
            String token = Jwts.builder()
                    .setSubject(((User) authentication.getPrincipal()).getUsername())
                    .claim("roles", roles)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24-hour expiration
                    .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                    .compact();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            // Replace with the URL of the SmartphoneController API
            String url = "http://localhost:8083/api/smartphones";


            HttpEntity<String> entity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate
                    .exchange(url, HttpMethod.GET, entity, String.class);

            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            tokenMap.put("smartphoneResponse", response.getBody());
            return tokenMap;

        } catch (AuthenticationException e) {
            throw new RuntimeException("Invalid username or password");
        }
    }

        @PutMapping("/smartphones/{id}/update-cost")
        public Map<String, String> updateCost(@PathVariable String id,@RequestParam Double cost,
                                              @RequestParam String username, @RequestParam String password) {
        this.id=id;
        this.cost=cost;
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(username, password)
                );

                List<String> roles = userRoleService.getRolesByUsername(username);
                // Generate JWT token
                String token = Jwts.builder()
                        .setSubject(((User) authentication.getPrincipal()).getUsername())
                        .claim("roles", roles)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 24-hour expiration
                        .signWith(SignatureAlgorithm.HS256, jwtSecret.getBytes())
                        .compact();

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + token);

                // Replace with the URL of the SmartphoneController API
                String url = "http://localhost:8083/api/smartphones/"+this.id+"/update-cost?cost="+this.cost;


                HttpEntity<String> entity = new HttpEntity<>(headers);
                ResponseEntity<String> response = restTemplate
                        .exchange(url, HttpMethod.PUT, entity, String.class);

                Map<String, String> tokenMap = new HashMap<>();
                tokenMap.put("token", token);
                tokenMap.put("smartphoneResponse", response.getBody());
                return tokenMap;

            } catch (AuthenticationException e) {
                throw new RuntimeException("Invalid username or password");
            }
    }
}
