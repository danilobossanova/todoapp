package br.com.danilobossanova.todoapp.service;

import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import br.com.danilobossanova.todoapp.repository.TaskRepository;
import br.com.danilobossanova.todoapp.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceTest {

    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    @BeforeEach
    void setup() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskServiceImpl(taskRepository);
    }

    @Test
    @DisplayName("Deve criar uma nova tarefa com status PENDING")
    void shouldCreateTaskWithPendingStatus() {
        Task taskToCreate = Task.builder()
                .name("Nova tarefa")
                .description("Descrição")
                .expectedCompletionDate(LocalDate.now().plusDays(2))
                .priority(Priority.MEDIUM)
                .build();

        Task savedTask = Task.builder()
                .id(1L)
                .name(taskToCreate.getName())
                .description(taskToCreate.getDescription())
                .expectedCompletionDate(taskToCreate.getExpectedCompletionDate())
                .priority(taskToCreate.getPriority())
                .status(Status.PENDING)
                .build();

        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        Task result = taskService.createTask(taskToCreate);

        assertNotNull(result);
        assertEquals(Status.PENDING, result.getStatus());
        assertEquals("Nova tarefa", result.getName());
        verify(taskRepository, times(1)).save(any(Task.class));
    }
}
