package com.natlex.test_app.service.impl;

import com.natlex.test_app.model.entity.AppUser;
import com.natlex.test_app.repository.AppUserRepository;
import com.natlex.test_app.service.IAppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class AppUserService implements UserDetailsService, IAppUserService {
    private static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);
    @Autowired
    private AppUserRepository appUserRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<AppUser> userByLogin = findByLogin(userName);
        if (!userByLogin.isPresent()) {
            LOG.warn("Unknown user: ", userName);
            throw new UsernameNotFoundException("Unknown user: " + userName);
        }
        List<SimpleGrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("admin"));
        return new User(userByLogin.get().getLogin(), userByLogin.get().getPassword(), authorities);
    }

    @Override
    public Optional<AppUser> findByLogin(String login) {
        return appUserRepository.findByLogin(login);
    }

    @Override
    public Optional<AppUser> getById(long id) {
        return appUserRepository.findById(id);
    }

    @Override
    public AppUser save(AppUser appUser) {
        appUser.setPassword(new BCryptPasswordEncoder().encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    @Override
    public void deleteUser(AppUser appUser) {
        appUserRepository.delete(appUser);
    }

}
