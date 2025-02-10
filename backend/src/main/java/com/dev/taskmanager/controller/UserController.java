package com.dev.taskmanager.controller;

import com.dev.taskmanager.dto.UserMinDTO;
import com.dev.taskmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<Page<UserMinDTO>> findAll(Pageable pageable) {
        Page<UserMinDTO> dto = service.findAll(pageable);
        return ResponseEntity.ok(dto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserMinDTO> findById(@PathVariable Long id) {
        UserMinDTO dto = service.findById(id);
        return ResponseEntity.ok(dto);
    }
}
