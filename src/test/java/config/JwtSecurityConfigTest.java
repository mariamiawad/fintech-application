package config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.axis.fintech.config.JwtSecurityConfig;
import com.axis.fintech.security.JwtAuthenticationFilter;
import com.axis.fintech.security.JwtTokenProvider;

class JwtSecurityConfigTest {

    private JwtSecurityConfig config;

    @BeforeEach
    void setUp() {
        config = new JwtSecurityConfig(mock(JwtTokenProvider.class));
    }

    @Test
    void shouldProvidePasswordEncoder() {
        PasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isNotNull();
        assertThat(encoder.matches("raw", encoder.encode("raw"))).isTrue();
    }

    @Test
    void shouldProvideAuthenticationProvider() {
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        AuthenticationProvider provider = config.authenticationProvider(userDetailsService);
        assertThat(provider).isNotNull();
    }

    @Test
    void shouldProvideAuthenticationManager() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager manager = mock(AuthenticationManager.class);
        org.mockito.Mockito.when(authConfig.getAuthenticationManager()).thenReturn(manager);

        AuthenticationManager result = config.authenticationManager(authConfig);
        assertThat(result).isSameAs(manager);
    }

    @Test
    void shouldProvideCorsConfigurer() {
        WebMvcConfigurer corsConfigurer = config.corsConfigurer();
        assertThat(corsConfigurer).isNotNull();

        // Simulate applying it
        CorsRegistry registry = new CorsRegistry();
        corsConfigurer.addCorsMappings(registry);
    }

    @Test
    void shouldProvideFirewall() {
        HttpFirewall firewall = config.allowUrlEncodedCarriageReturnHttpFirewall();
        assertThat(firewall).isNotNull();
    }

    @Test
    void shouldProvideOpenAPI() {
        assertThat(config.customOpenAPI()).isNotNull();
        assertThat(config.customOpenAPI().getInfo().getTitle()).isEqualTo("Fintech REST API");
    }

    @Test
    void shouldProvideSecurityFilterChain() throws Exception {
        HttpSecurity http = mock(HttpSecurity.class, org.mockito.Mockito.RETURNS_DEEP_STUBS);
        JwtAuthenticationFilter jwtFilter = mock(JwtAuthenticationFilter.class);

        // We can't test real filter chain here, just call the method and check not null
        SecurityFilterChain chain = config.securityFilterChain(http, jwtFilter);
        assertThat(chain).isNotNull();
    }
}
