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
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @EntityGraph(attributePaths = {"user.roles"})
    @Query("""
            SELECT t FROM Task t
            WHERE t.id = :taskId
            """
    )
    Optional<Task> searchById(Long taskId);

    @Query(value = """
            SELECT * FROM tb_task
            WHERE user_id = :userId
            """,
            nativeQuery = true
    )
    Page<Task> searchAllTasksByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user.roles"})
    Optional<Task> findById(Long id);
}
