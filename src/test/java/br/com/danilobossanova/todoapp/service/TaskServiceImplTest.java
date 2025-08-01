package br.com.danilobossanova.todoapp.service;

import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import br.com.danilobossanova.todoapp.exception.TaskNotFoundException;
import br.com.danilobossanova.todoapp.repository.TaskRepository;
import br.com.danilobossanova.todoapp.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TaskServiceImplTest {

    private TaskRepository taskRepository;
    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp() {
        taskRepository = mock(TaskRepository.class);
        taskService = new TaskServiceImpl(taskRepository);
    }

    @Test
    @DisplayName("Deve criar tarefa válida com status OPEN")
    void shouldCreateValidTask() {
        Task task = Task.builder()
                .name("Tarefa válida")
                .description("Descrição ok")
                .priority(Priority.MEDIUM)
                .expectedCompletionDate(LocalDate.now().plusDays(2))
                .build();

        Task saved = Task.builder()
                .id(1L)
                .name(task.getName())
                .description(task.getDescription())
                .priority(task.getPriority())
                .expectedCompletionDate(task.getExpectedCompletionDate())
                .status(Status.OPEN)
                .build();

        when(taskRepository.existsByNameIgnoreCase(any())).thenReturn(false);
        when(taskRepository.save(any())).thenReturn(saved);

        Task result = taskService.createTask(task);

        assertEquals(Status.OPEN, result.getStatus());
        assertEquals("Tarefa válida", result.getName());
        verify(taskRepository).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar tarefa com nome duplicado")
    void shouldThrowWhenDuplicateName() {
        Task task = Task.builder()
                .name("Tarefa duplicada")
                .priority(Priority.HIGH)
                .expectedCompletionDate(LocalDate.now().plusDays(1))
                .build();

        when(taskRepository.existsByNameIgnoreCase(any())).thenReturn(true);

        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
        assertTrue(ex.getMessage().toLowerCase().contains("já existe"));
    }

    @Test
    @DisplayName("Deve retornar tarefa por ID")
    void shouldFindTaskById() {
        Task task = Task.builder()
                .id(10L)
                .name("Buscar por ID")
                .priority(Priority.LOW)
                .expectedCompletionDate(LocalDate.now().plusDays(3))
                .status(Status.OPEN)
                .build();

        when(taskRepository.findById(10L)).thenReturn(Optional.of(task));

        Task result = taskService.findTaskById(10L);

        assertEquals("Buscar por ID", result.getName());
        verify(taskRepository).findById(10L);
    }

    @Test
    @DisplayName("Deve lançar TaskNotFoundException se ID não existir")
    void shouldThrowIfTaskNotFound() {
        when(taskRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.findTaskById(999L));
    }

    @Test
    @DisplayName("Deve retornar lista paginada ao buscar com filtros")
    void shouldFindTasksWithFilters() {
        Pageable pageable = PageRequest.of(0, 5);

        @SuppressWarnings("unchecked")
        Specification<Task> spec = (Specification<Task>) any(Specification.class);

        when(taskRepository.findAll(spec, eq(pageable)))
                .thenReturn(new PageImpl<>(List.of()));

        taskService.findTasksWithFilters("doc", Priority.HIGH, Status.PENDING, pageable);

        verify(taskRepository).findAll(any(Specification.class), eq(pageable));
    }



    @Test
    @DisplayName("Deve validar dados inválidos corretamente")
    void shouldFailValidation() {
        Task task = Task.builder()
                .name("") // inválido
                .expectedCompletionDate(LocalDate.now().minusDays(1)) // passado
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            taskService.createTask(task);
        });

        assertTrue(ex.getMessage().contains("Nome da tarefa é obrigatório"));
    }

    @Test
    @DisplayName("Deve atualizar tarefa existente com dados válidos")
    void shouldUpdateExistingTask() {
        Task originalTask = Task.builder()
                .id(1L)
                .name("Tarefa Original")
                .description("Descrição")
                .priority(Priority.LOW)
                .status(Status.OPEN)
                .expectedCompletionDate(LocalDate.now().plusDays(3))
                .build();

        Task updatedData = Task.builder()
                .name("Tarefa Atualizada")
                .description("Nova descrição")
                .priority(Priority.HIGH)
                .expectedCompletionDate(LocalDate.now().plusDays(5))
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(originalTask));
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.updateTask(1L, updatedData);

        assertEquals("Tarefa Atualizada", result.getName());
        assertEquals(Priority.HIGH, result.getPriority());
        verify(taskRepository).save(any());
    }


    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar tarefa concluída")
    void shouldThrowWhenUpdatingCompletedTask() {
        Task completedTask = Task.builder()
                .id(2L)
                .name("Fechada")
                .status(Status.COMPLETED)
                .build();

        when(taskRepository.findById(2L)).thenReturn(Optional.of(completedTask));

        Task updatedData = Task.builder()
                .name("Atualizada")
                .priority(Priority.HIGH)
                .expectedCompletionDate(LocalDate.now().plusDays(2))
                .build();

        assertThrows(IllegalStateException.class, () -> taskService.updateTask(2L, updatedData));
    }

    @Test
    @DisplayName("Deve marcar tarefa como pendente se transição for válida")
    void shouldMarkAsPendingWhenAllowed() {
        Task task = Task.builder()
                .id(3L)
                .name("Tarefa X")
                .status(Status.OPEN) // Suponha que OPEN permita voltar para PENDING
                .expectedCompletionDate(LocalDate.now().plusDays(1))
                .priority(Priority.MEDIUM)
                .build();

        when(taskRepository.findById(3L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = taskService.markTaskAsPending(3L);

        assertEquals(Status.PENDING, result.getStatus());
        verify(taskRepository).save(any());
    }


    @Test
    @DisplayName("Deve deletar tarefa que não está pendente")
    void shouldDeleteTaskSuccessfully() {
        Task task = Task.builder()
                .id(4L)
                .status(Status.COMPLETED)
                .build();

        when(taskRepository.findById(4L)).thenReturn(Optional.of(task));
        doNothing().when(taskRepository).delete(task);

        taskService.deleteTask(4L);

        verify(taskRepository).delete(task);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar tarefa pendente")
    void shouldThrowWhenDeletingPendingTask() {
        Task task = Task.builder()
                .id(5L)
                .status(Status.PENDING)
                .build();

        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));

        assertThrows(IllegalStateException.class, () -> taskService.deleteTask(5L));
    }

    @Test
    @DisplayName("Deve retornar true se tarefa com nome existir")
    void shouldReturnTrueIfTaskWithNameExists() {
        when(taskRepository.existsByNameIgnoreCase("teste")).thenReturn(true);

        boolean result = taskService.existsTaskWithName("teste");

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve validar se data de conclusão é futura")
    void shouldValidateFutureDate() {
        assertTrue(taskService.isValidCompletionDate(LocalDate.now().plusDays(1)));
        assertFalse(taskService.isValidCompletionDate(LocalDate.now().minusDays(1)));
        assertFalse(taskService.isValidCompletionDate(null));
    }

    @Test
    @DisplayName("Deve lançar exceção se nome for nulo")
    void shouldFailValidationWithNullName() {
        Task task = Task.builder()
                .name(null)
                .priority(Priority.HIGH)
                .expectedCompletionDate(LocalDate.now().plusDays(1))
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
        assertTrue(ex.getMessage().contains("Nome da tarefa é obrigatório"));
    }


    @Test
    @DisplayName("Deve lançar exceção se tarefa for nula")
    void shouldFailIfTaskIsNull() {
        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(null));
        assertTrue(ex.getMessage().contains("Dados da tarefa não podem ser nulos"));
    }

    @Test
    @DisplayName("Deve lançar exceção se nome for vazio")
    void shouldFailIfNameIsBlank() {
        Task task = Task.builder()
                .name("   ")
                .priority(Priority.HIGH)
                .expectedCompletionDate(LocalDate.now().plusDays(2))
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
        assertTrue(ex.getMessage().contains("Nome da tarefa é obrigatório"));
    }

    @Test
    @DisplayName("Deve lançar exceção se nome ultrapassar 100 caracteres")
    void shouldFailIfNameTooLong() {
        String longName = "A".repeat(101);

        Task task = Task.builder()
                .name(longName)
                .priority(Priority.LOW)
                .expectedCompletionDate(LocalDate.now().plusDays(2))
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
        assertTrue(ex.getMessage().contains("Nome da tarefa deve ter no máximo"));
    }

    @Test
    @DisplayName("Deve lançar exceção se descrição ultrapassar 500 caracteres")
    void shouldFailIfDescriptionTooLong() {
        String longDesc = "D".repeat(501);

        Task task = Task.builder()
                .name("Valida")
                .description(longDesc)
                .priority(Priority.MEDIUM)
                .expectedCompletionDate(LocalDate.now().plusDays(1))
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
        assertTrue(ex.getMessage().contains("Descrição deve ter no máximo"));
    }

    @Test
    @DisplayName("Deve lançar exceção se prioridade for nula")
    void shouldFailIfPriorityIsNull() {
        Task task = Task.builder()
                .name("Tarefa X")
                .expectedCompletionDate(LocalDate.now().plusDays(1))
                .build();

        Exception ex = assertThrows(IllegalArgumentException.class, () -> taskService.createTask(task));
        assertTrue(ex.getMessage().contains("Prioridade é obrigatória"));
    }

    @Test
    @DisplayName("Deve lançar exceção se data prevista for nula ou no passado")
    void shouldFailIfDateIsInvalid() {
        Task nullDate = Task.builder()
                .name("Sem data")
                .priority(Priority.LOW)
                .build();

        Task pastDate = Task.builder()
                .name("Data passada")
                .priority(Priority.LOW)
                .expectedCompletionDate(LocalDate.now().minusDays(2))
                .build();

        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(nullDate));
        assertThrows(IllegalArgumentException.class, () -> taskService.createTask(pastDate));
    }

    @Test
    @DisplayName("Deve aceitar tarefa com dados válidos sem lançar exceção")
    void shouldAcceptValidTask() {
        Task task = Task.builder()
                .name("Tarefa válida")
                .description("Descrição opcional")
                .priority(Priority.MEDIUM)
                .expectedCompletionDate(LocalDate.now().plusDays(3))
                .status(Status.OPEN)
                .build();

        when(taskRepository.existsByNameIgnoreCase(any())).thenReturn(false);
        when(taskRepository.save(any())).thenReturn(task);

        assertDoesNotThrow(() -> taskService.createTask(task));
    }

    @Test
    @DisplayName("Deve contar tarefas com status especificado")
    void shouldCountTasksByStatus() {
        when(taskRepository.countByStatus(Status.PENDING)).thenReturn(3L);

        long count = taskService.countTasksByStatus(Status.PENDING);

        assertEquals(3L, count);
    }

    @Test
    @DisplayName("Deve retornar tarefas em atraso")
    void shouldFindOverdueTasks() {
        List<Task> overdueTasks = List.of(
                Task.builder()
                        .id(1L)
                        .name("Atrasada")
                        .expectedCompletionDate(LocalDate.now().minusDays(2))
                        .status(Status.PENDING)
                        .build()
        );

        when(taskRepository.findOverdueTasks(any(), eq(Status.COMPLETED)))
                .thenReturn(overdueTasks);

        List<Task> result = taskService.findOverdueTasks();

        assertEquals(1, result.size());
        assertEquals("Atrasada", result.get(0).getName());
    }




}
