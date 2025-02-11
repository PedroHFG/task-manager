package com.dev.taskmanager.services;

import com.dev.taskmanager.dto.TaskDTO;
import com.dev.taskmanager.entities.Task;
import com.dev.taskmanager.entities.User;
import com.dev.taskmanager.repositories.TaskRepository;
import com.dev.taskmanager.services.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthService authService;

    @Transactional(readOnly = true)
    public Page<TaskDTO> findAll(Pageable pageable) {
        User me = userService.authenticated();
//        Page<Task> result = taskRepository.searchByUserId(me.getId(), pageable);
        Page<Task> result = taskRepository.searchAllTasksByUserId(me.getId(), pageable);
        return result.map(x -> new TaskDTO(x));
    }

    @Transactional(readOnly = true)
    public TaskDTO findById(Long id) {
//        Task result = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found " + id));
        Task result = taskRepository.searchById(id).orElseThrow(() -> new ResourceNotFoundException("Task not found " + id));
        authService.validateSelfOrAdmin(result.getUser().getId());
        return new TaskDTO(result);
    }
}
