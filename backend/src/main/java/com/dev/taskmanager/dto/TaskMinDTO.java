package com.dev.taskmanager.dto;

import com.dev.taskmanager.entities.TaskStatus;

public class TaskMinDTO {

    private TaskStatus status;

    public TaskMinDTO() {

    }

    public TaskMinDTO(TaskStatus status) {
        this.status = status;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}

