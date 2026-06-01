package com.PetShop;

import com.PetShop.Servicios.UsuarioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(usuarioServicio)
            .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers(
                    "/css/**", "/js/**", "/img/**", "/imagenes/**", "/webjars/**",
                    "/*.css", "/*.js", "/*.jpg", "/*.jpeg", "/*.png", "/*.webp", "/*.gif"
                ).permitAll()

                .antMatchers(
                    "/", "/login", "/registro", "/usuario/validar/**",
                    "/contacto", "/quienes_somos", "/main/**", "/glosario/**",
                    "/alimento", "/accesorio", "/imagen/**", "/login/contraseñaOlvidada",
                    "/login/olvido", "/login/restablecer/**"
                ).permitAll()

                .antMatchers(
                    "/Carrito/**", "/compra/**", "/Compra/**", "/mi-cuenta/**"
                ).hasAnyRole("GENERAL", "ADMIN")

                .antMatchers(
                    "/admin/**", "/actividad/**", "/lista/**",
                    "/alimento/crear/**", "/alimento/editar/**", "/alimento/eliminar/**",
                    "/accesorio/crear/**", "/accesorio/editar/**", "/accesorio/eliminar/**", "/accesorio/ingresarAccesorio/**"
                ).hasRole("ADMIN")

                .anyRequest().authenticated()

            .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/logincheck")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login?error=error")
                    .permitAll()

            .and()
                .logout()
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login?logout=logout")
                    .permitAll()

            .and()
                .csrf()
                    .disable();
    }
}
