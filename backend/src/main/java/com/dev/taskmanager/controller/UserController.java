package com.dev.taskmanager.controller;

import com.dev.taskmanager.dto.UserMinDTO;
import com.dev.taskmanager.dto.UserDTO;
import com.dev.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        Page<UserDTO> dto = service.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<UserMinDTO> insert(@RequestBody UserMinDTO dto) {
        dto = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(dto.getId()).toUri();
        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UserMinDTO> updade(@PathVariable Long id, @RequestBody UserMinDTO dto) {
        dto = service.update(id, dto);
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<UserDTO> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getMe(){
        UserDTO dto = service.getMe();
        return ResponseEntity.ok(dto);
    }

}
