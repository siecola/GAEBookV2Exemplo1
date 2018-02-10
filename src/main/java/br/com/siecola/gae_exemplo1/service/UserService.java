package br.com.siecola.gae_exemplo1.service;

import br.com.siecola.gae_exemplo1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.logging.Logger;

@Service("userDetailsService")
public class UserService implements UserDetailsService {

    private static final Logger log = Logger.getLogger("UserService");

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        Optional<br.com.siecola.gae_exemplo1.model.User> optUser =
                userRepository.getByEmail(email);
        if (optUser.isPresent()) {
            return optUser.get();
        } else {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }
    }
}