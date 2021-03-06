package recipes.security;

/**
 * Created by Me on 14/08/2017.
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import recipes.CookRepository;

// Cook user authentication
@Configuration
public class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

    @Autowired
    CookRepository cookRepository;

    @Override
    public void init(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Bean
    UserDetailsService userDetailsService() {
        return (username) -> cookRepository
                .findByUsernameIgnoreCase(username)
                .map(a -> new User(a.getUsername(), a.getPassword(), true, true, true, true,
                        AuthorityUtils.createAuthorityList("ROLE_USER", "write")))
                .orElseThrow(
                        () -> new UsernameNotFoundException("could not find the user '"
                                + username + "'"));
    }
}
