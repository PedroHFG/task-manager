package com.dev.taskmanager.services;

import com.dev.taskmanager.dto.TaskDTO;
import com.dev.taskmanager.entities.Task;
import com.dev.taskmanager.entities.TaskStatus;
import com.dev.taskmanager.entities.User;
import com.dev.taskmanager.repositories.TaskRepository;
import com.dev.taskmanager.services.exceptions.DatabaseException;
import com.dev.taskmanager.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
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

    @Transactional
    public TaskDTO insert(TaskDTO dto) {
        Task entity = new Task();
        copyDtoToEntity(dto, entity);
        entity.setStatus(TaskStatus.PENDING);

        User user =userService.authenticated();
        entity.setUser(user);

        entity = taskRepository.save(entity);
        return new TaskDTO(entity);
    }

    @Transactional
    public TaskDTO update(Long id, TaskDTO dto) {
        try {
            Task entity = taskRepository.getReferenceById(id);
            authService.validateSelfOrAdmin(entity.getUser().getId());
            copyDtoToEntity(dto, entity);
            entity = taskRepository.save(entity);
            return new TaskDTO(entity);
        }
        catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Task not found " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Task not found " + id));
        authService.validateSelfOrAdmin(task.getUser().getId());

        try {
            taskRepository.deleteById(id);
        }
        catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Referential integrity failure!");
        }
    }

    private void copyDtoToEntity(TaskDTO dto, Task entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setDueDate(dto.getDueDate());
    }
}
