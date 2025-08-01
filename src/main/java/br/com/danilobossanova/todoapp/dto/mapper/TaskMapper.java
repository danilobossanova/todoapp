package br.com.danilobossanova.todoapp.dto.mapper;

import br.com.danilobossanova.todoapp.dto.request.TaskCreateRequest;
import br.com.danilobossanova.todoapp.dto.request.TaskUpdateRequest;
import br.com.danilobossanova.todoapp.dto.response.TaskResponse;
import br.com.danilobossanova.todoapp.dto.response.TaskSummaryResponse;
import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Status;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para conversão entre entidades e DTOs.
 * <p>
 * Esta classe implementa o padrão Mapper para converter
 * objetos entre as diferentes camadas da aplicação.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
@Component
public class TaskMapper {

    /**
     * Converte um TaskCreateRequest em uma entidade Task.
     *
     * @param request DTO de criação de tarefa
     * @return Entidade Task pronta para persistência
     */
    public Task toEntity(TaskCreateRequest request) {
        if (request == null) {
            return null;
        }

        return Task.builder()
                .name(request.getName())
                .description(request.getDescription())
                .priority(request.getPriority())
                .status(Status.OPEN) // Sempre inicia como OPEN
                .expectedCompletionDate(request.getExpectedCompletionDate())
                .build();
    }

    /**
     * Atualiza uma entidade Task com dados de TaskUpdateRequest.
     *
     * @param existingTask Entidade existente a ser atualizada
     * @param request DTO com novos dados
     * @return Entidade atualizada
     */
    public Task updateEntity(Task existingTask, TaskUpdateRequest request) {
        if (existingTask == null || request == null) {
            return existingTask;
        }

        existingTask.setName(request.getName());
        existingTask.setDescription(request.getDescription());
        existingTask.setPriority(request.getPriority());
        existingTask.setExpectedCompletionDate(request.getExpectedCompletionDate());

        // Nota: Status não é atualizado através deste método
        // Ele deve ser alterado apenas através dos métodos específicos do serviço

        return existingTask;
    }

    /**
     * Converte um TaskUpdateRequest em uma nova entidade Task.
     * Esta conversão é útil para testes unitários isolados.
     *
     * @param request DTO de atualização de tarefa
     * @return Nova instância de Task com os dados do request
     */
    public Task toEntity(TaskUpdateRequest request) {
        if (request == null) {
            return null;
        }

        return Task.builder()
                .name(request.getName())
                .description(request.getDescription())
                .expectedCompletionDate(request.getExpectedCompletionDate())
                .priority(request.getPriority())
                .build();
    }


    /**
     * Converte uma entidade Task em TaskResponse completo.
     *
     * @param task Entidade Task
     * @return DTO de resposta completo
     */
    public TaskResponse toResponse(Task task) {
        if (task == null) {
            return null;
        }

        return TaskResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .priority(task.getPriority())
                .priorityDescription(task.getPriority().getDescription())
                .status(task.getStatus())
                .statusDescription(task.getStatus().getDescription())
                .expectedCompletionDate(task.getExpectedCompletionDate())
                .createdDate(task.getCreatedDate())
                .overdue(isTaskOverdue(task))
                .canBeEdited(canTaskBeEdited(task))
                .availableActions(getAvailableActions(task))
                .build();
    }

    /**
     * Converte uma entidade Task em TaskSummaryResponse resumido.
     *
     * @param task Entidade Task
     * @return DTO de resposta resumido
     */
    public TaskSummaryResponse toSummaryResponse(Task task) {
        if (task == null) {
            return null;
        }

        return TaskSummaryResponse.builder()
                .id(task.getId())
                .name(task.getName())
                .priority(task.getPriority())
                .priorityDescription(task.getPriority().getDescription())
                .status(task.getStatus())
                .statusDescription(task.getStatus().getDescription())
                .expectedCompletionDate(task.getExpectedCompletionDate())
                .overdue(isTaskOverdue(task))
                .build();
    }

    /**
     * Converte uma lista de entidades em lista de respostas completas.
     *
     * @param tasks Lista de entidades
     * @return Lista de DTOs de resposta
     */
    public List<TaskResponse> toResponseList(List<Task> tasks) {
        if (tasks == null) {
            return new ArrayList<>();
        }

        return tasks.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Converte uma lista de entidades em lista de respostas resumidas.
     *
     * @param tasks Lista de entidades
     * @return Lista de DTOs de resposta resumidos
     */
    public List<TaskSummaryResponse> toSummaryResponseList(List<Task> tasks) {
        if (tasks == null) {
            return new ArrayList<>();
        }

        return tasks.stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    /**
     * Verifica se uma tarefa está em atraso.
     *
     * @param task Entidade Task
     * @return true se a tarefa está em atraso
     */
    private boolean isTaskOverdue(Task task) {
        if (task.getStatus() == Status.COMPLETED) {
            return false;
        }
        return task.getExpectedCompletionDate().isBefore(LocalDate.now());
    }

    /**
     * Verifica se uma tarefa pode ser editada.
     *
     * @param task Entidade Task
     * @return true se a tarefa pode ser editada
     */
    private boolean canTaskBeEdited(Task task) {
        return task.getStatus() != Status.COMPLETED;
    }

    /**
     * Obtém as ações disponíveis para uma tarefa baseadas no seu status atual.
     *
     * @param task Entidade Task
     * @return Lista de ações disponíveis
     */
    private List<String> getAvailableActions(Task task) {
        List<String> actions = new ArrayList<>();

        // Ações baseadas no status atual
        if (task.getStatus().canBeCompleted()) {
            actions.add("marcar-como-concluida");
        }

        if (task.getStatus().canBePending()) {
            actions.add("marcar-como-pendente");
        }

        // Ações sempre disponíveis (com suas próprias validações)
        if (canTaskBeEdited(task)) {
            actions.add("editar");
        }

        // Regra de negócio: tarefas pendentes não podem ser excluídas
        if (task.getStatus() != Status.PENDING) {
            actions.add("excluir");
        }

        return actions;
    }
}