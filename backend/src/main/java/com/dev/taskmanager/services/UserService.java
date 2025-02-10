package com.dev.taskmanager.services;

import com.dev.taskmanager.dto.UserMinDTO;
import com.dev.taskmanager.entities.Role;
import com.dev.taskmanager.entities.User;

import com.dev.taskmanager.projections.UserDetailsProjection;
import com.dev.taskmanager.repositories.UserRepository;
import com.dev.taskmanager.services.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserMinDTO> findAll(Pageable pageable) {
        Page<User> list = userRepository.findAll(pageable);
        return list.map(x -> new UserMinDTO(x));
    }

    @Transactional
    public UserMinDTO findById(Long id) {
        Optional<User> obj =  userRepository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        return new UserMinDTO(entity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailsProjection> result = userRepository.searchUserAndRolesByEmail(username);

        if (result.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = new User();
        user.setEmail(username);
        user.setPassword(result.get(0).getPassword());

        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }

        return user;
    }

}
