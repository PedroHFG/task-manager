package com.dev.taskmanager.services;

import com.dev.taskmanager.dto.UserDTO;
import com.dev.taskmanager.dto.UserMinDTO;
import com.dev.taskmanager.entities.Role;
import com.dev.taskmanager.entities.User;

import com.dev.taskmanager.projections.UserDetailsProjection;
import com.dev.taskmanager.repositories.RoleRepository;
import com.dev.taskmanager.repositories.UserRepository;
import com.dev.taskmanager.services.exceptions.DatabaseException;
import com.dev.taskmanager.services.exceptions.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private Role defaultRole; // Armazena a ROLE_USER na memória

    @PostConstruct
    public void init() {
        defaultRole = roleRepository.findByAuthority("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Role ROLE_USER not found!"));
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> findAll(Pageable pageable) {
        Page<User> list = userRepository.findAll(pageable);
        return list.map(x -> new UserDTO(x));
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        Optional<User> obj =  userRepository.findById(id);
        User entity = obj.orElseThrow(() -> new ResourceNotFoundException("User not found " + id));
        return new UserDTO(entity);
    }

    @Transactional
    public UserMinDTO insert(UserMinDTO dto) {
        User entity = new User();
        copyDtoToEntity(dto, entity);

        /*Role role = roleRepository.findByAuthority("ROLE_USER").orElseThrow(() -> new ResourceNotFoundException("Role ROLE_USER not found!"));
        entity.addRole(role);*/

        // Reutiliza a Role carregada previamente, sem acessar o banco
        entity.addRole(defaultRole);

        entity = userRepository.save(entity);
        return new UserMinDTO(entity);
    }

    @Transactional
    public UserMinDTO update(Long id, UserMinDTO dto) {
        try {
            User entity = userRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = userRepository.save(entity);
            return new UserMinDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("User not found " + id);
        }

    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found " + id);
        }

        try {
            userRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure!");
        }
    }

    private void copyDtoToEntity(UserMinDTO dto, User entity) {
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setPassword(passwordEncoder.encode(dto.getPassword()));
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

    protected User authenticated() {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Jwt jwtPrincipal = (Jwt) authentication.getPrincipal();
            String username = jwtPrincipal.getClaim("username");

            User user = userRepository.findByEmail(username).get();
            return user;
        }
        catch (Exception e) {
            throw new UsernameNotFoundException("Email not found");
        }
    }

    @Transactional(readOnly = true)
    public UserDTO getMe() {
        User user = authenticated();
        return new UserDTO(user);
    }
}
