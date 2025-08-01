package br.com.danilobossanova.todoapp.controller;

import br.com.danilobossanova.todoapp.dto.mapper.TaskMapper;
import br.com.danilobossanova.todoapp.dto.request.TaskCreateRequest;
import br.com.danilobossanova.todoapp.dto.request.TaskUpdateRequest;
import br.com.danilobossanova.todoapp.dto.response.TaskResponse;
import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import br.com.danilobossanova.todoapp.exception.InvalidTaskStatusException;
import br.com.danilobossanova.todoapp.exception.TaskNotFoundException;
import br.com.danilobossanova.todoapp.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste de integração para o endpoint de criação de tarefas no TaskController.
 * Verifica a resposta HTTP e os dados retornados com base em um mock da TaskService.
 */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Test
    @DisplayName("Deve criar uma tarefa com sucesso e retornar os dados formatados")
    void shouldCreateTaskSuccessfully() throws Exception {
        TaskCreateRequest request = TaskCreateRequest.builder()
                .name("Reunião com equipe")
                .description("Discutir metas trimestrais")
                .expectedCompletionDate(LocalDate.now().plusDays(3))
                .priority(Priority.HIGH)
                .build();

        Task fakeTask = Task.builder()
                .id(1L)
                .name(request.getName())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(Status.PENDING)
                .expectedCompletionDate(request.getExpectedCompletionDate())
                .createdDate(LocalDateTime.now())
                .build();

        TaskResponse fakeResponse = TaskResponse.builder()
                .id(fakeTask.getId())
                .name(fakeTask.getName())
                .description(fakeTask.getDescription())
                .priority(fakeTask.getPriority())
                .status(fakeTask.getStatus())
                .expectedCompletionDate(fakeTask.getExpectedCompletionDate())
                .createdDate(fakeTask.getCreatedDate())
                .build();

        Mockito.when(taskService.createTask(any(Task.class))).thenReturn(fakeTask);
        Mockito.when(taskMapper.toEntity(any(TaskCreateRequest.class))).thenReturn(fakeTask);
        Mockito.when(taskMapper.toResponse(fakeTask)).thenReturn(fakeResponse);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Reunião com equipe"))
                .andExpect(jsonPath("$.description").value("Discutir metas trimestrais"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao criar tarefa com nome vazio")
    void shouldReturnValidationErrorForBlankName() throws Exception {
        TaskCreateRequest request = TaskCreateRequest.builder()
                .name("") // inválido
                .description("Descrição")
                .expectedCompletionDate(LocalDate.now().plusDays(1))
                .priority(Priority.MEDIUM)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("Deve retornar uma tarefa existente por ID")
    void shouldReturnTaskById() throws Exception {
        Task fakeTask = Task.builder()
                .id(10L)
                .name("Finalizar relatório")
                .description("Preparar entrega do mês")
                .priority(Priority.MEDIUM)
                .status(Status.PENDING)
                .expectedCompletionDate(LocalDate.now().plusDays(5))
                .createdDate(LocalDateTime.now())
                .build();

        TaskResponse response = TaskResponse.builder()
                .id(fakeTask.getId())
                .name(fakeTask.getName())
                .description(fakeTask.getDescription())
                .priority(fakeTask.getPriority())
                .status(fakeTask.getStatus())
                .expectedCompletionDate(fakeTask.getExpectedCompletionDate())
                .createdDate(fakeTask.getCreatedDate())
                .build();

        Mockito.when(taskService.findTaskById(10L)).thenReturn(fakeTask);
        Mockito.when(taskMapper.toResponse(fakeTask)).thenReturn(response);

        mockMvc.perform(get("/api/v1/tasks/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Finalizar relatório"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Deve atualizar uma tarefa existente com sucesso")
    void shouldUpdateTaskSuccessfully() throws Exception {
        TaskUpdateRequest updateRequest = TaskUpdateRequest.builder()
                .name("Atualizar documento")
                .description("Nova versão do contrato")
                .expectedCompletionDate(LocalDate.now().plusDays(2))
                .priority(Priority.HIGH)
                .build();

        Task updatedTask = Task.builder()
                .id(15L)
                .name(updateRequest.getName())
                .description(updateRequest.getDescription())
                .expectedCompletionDate(updateRequest.getExpectedCompletionDate())
                .priority(updateRequest.getPriority())
                .status(Status.PENDING)
                .createdDate(LocalDateTime.now())
                .build();

        TaskResponse response = TaskResponse.builder()
                .id(updatedTask.getId())
                .name(updatedTask.getName())
                .description(updatedTask.getDescription())
                .priority(updatedTask.getPriority())
                .status(updatedTask.getStatus())
                .expectedCompletionDate(updatedTask.getExpectedCompletionDate())
                .createdDate(updatedTask.getCreatedDate())
                .build();

        Mockito.when(taskService.updateTask(Mockito.eq(15L), any(Task.class))).thenReturn(updatedTask);
        Mockito.when(taskMapper.toEntity(any(TaskUpdateRequest.class))).thenReturn(updatedTask);
        Mockito.when(taskMapper.toResponse(updatedTask)).thenReturn(response);

        mockMvc.perform(put("/api/v1/tasks/15")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.name").value("Atualizar documento"))
                .andExpect(jsonPath("$.priority").value("HIGH"));
    }

    @Test
    @DisplayName("Deve concluir uma tarefa com sucesso")
    void shouldMarkTaskAsCompleted() throws Exception {
        Task task = Task.builder()
                .id(22L)
                .name("Entrega final")
                .description("Finalizar entregas")
                .priority(Priority.HIGH)
                .status(Status.COMPLETED)
                .expectedCompletionDate(LocalDate.now().plusDays(1))
                .createdDate(LocalDateTime.now())
                .build();

        TaskResponse response = TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(task.getStatus())
                .expectedCompletionDate(task.getExpectedCompletionDate())
                .createdDate(task.getCreatedDate())
                .build();

        Mockito.when(taskService.markTaskAsCompleted(22L)).thenReturn(task);
        Mockito.when(taskMapper.toResponse(task)).thenReturn(response);

        mockMvc.perform(patch("/api/v1/tasks/22/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(22))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("Deve deletar uma tarefa com sucesso")
    void shouldDeleteTaskSuccessfully() throws Exception {
        Mockito.doNothing().when(taskService).deleteTask(99L);

        mockMvc.perform(delete("/api/v1/tasks/99"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar 404 ao buscar tarefa inexistente")
    void shouldReturnNotFoundWhenTaskDoesNotExist() throws Exception {
        Long nonExistentId = 999L;

        given(taskService.findTaskById(nonExistentId))
                .willThrow(TaskNotFoundException.withId(nonExistentId));

        mockMvc.perform(get("/api/v1/tasks/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Tarefa Não Encontrada"))
                .andExpect(jsonPath("$.message").value("Tarefa não encontrada com ID: 999"));
    }

    @Test
    @DisplayName("Deve retornar 422 ao tentar concluir uma tarefa com status inválido")
    void shouldReturnUnprocessableEntityWhenInvalidStatusTransition() throws Exception {
        Mockito.when(taskService.markTaskAsCompleted(888L))
                .thenThrow(new InvalidTaskStatusException("Status inválido para conclusão"));

        mockMvc.perform(patch("/api/v1/tasks/888/concluir"))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.error").value("Transição de status inválida"))
                .andExpect(jsonPath("$.message").value("Status inválido para conclusão"))
                .andExpect(jsonPath("$.path").value("/api/v1/tasks/888/concluir"));
    }

    @Test
    @DisplayName("Deve retornar 400 ao tentar criar tarefa com data passada")
    void shouldReturnBadRequestForPastDate() throws Exception {
        TaskCreateRequest request = TaskCreateRequest.builder()
                .name("Tarefa atrasada")
                .description("Descrição inválida")
                .expectedCompletionDate(LocalDate.now().minusDays(1)) // data no passado
                .priority(Priority.LOW)
                .build();

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }


}
