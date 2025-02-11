package com.dev.taskmanager.controller;

import com.dev.taskmanager.dto.TaskDTO;
import com.dev.taskmanager.services.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @GetMapping
    public ResponseEntity<Page<TaskDTO>> findAll(Pageable pageable) {
        Page<TaskDTO> dto = taskService.findAll(pageable);
        return ResponseEntity.ok(dto);
    }
}
