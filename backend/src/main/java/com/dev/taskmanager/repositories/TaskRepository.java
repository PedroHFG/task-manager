package com.dev.taskmanager.repositories;

import com.dev.taskmanager.dto.TaskDTO;
import com.dev.taskmanager.entities.Task;
import com.dev.taskmanager.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT obj FROM Task obj
            WHERE obj.user.id = :userId
            """)
    Page<Task> searchById(Long userId, Pageable pageable);
}
