package br.com.danilobossanova.todoapp.service.impl;

import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import br.com.danilobossanova.todoapp.exception.TaskNotFoundException;
import br.com.danilobossanova.todoapp.exception.InvalidTaskStatusException;
import br.com.danilobossanova.todoapp.repository.TaskRepository;
import br.com.danilobossanova.todoapp.service.TaskService;
import br.com.danilobossanova.todoapp.specification.TaskSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Implementação do serviço de tarefas.
 * <p>
 * Esta classe contém toda a lógica de negócio relacionada ao gerenciamento
 * de tarefas, incluindo validações, regras de transição de status e
 * operações de persistência.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Task createTask(Task task) {
        log.info("Criando nova tarefa: {}", task.getName());

        // Validações de negócio
        validateTaskData(task);

        // Define status inicial se não informado
        if (task.getStatus() == null) {
            task.setStatus(Status.OPEN);
        }

        // Valida se o status inicial é OPEN
        if (task.getStatus() != Status.OPEN) {
            throw new IllegalArgumentException("Novas tarefas devem ser criadas com status 'Aberta'");
        }

        // Verifica duplicidade de nome (se necessário)
        if (existsTaskWithName(task.getName())) {
            log.warn("Tentativa de criar tarefa com nome duplicado: {}", task.getName());
            throw new IllegalArgumentException("Já existe uma tarefa com este nome");
        }

        Task savedTask = taskRepository.save(task);
        log.info("Tarefa criada com sucesso. ID: {}", savedTask.getId());

        return savedTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task updateTask(Long id, Task taskData) {
        log.info("Atualizando tarefa ID: {}", id);

        Task existingTask = findTaskById(id);

        // Valida se a tarefa pode ser editada
        if (existingTask.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("Tarefas concluídas não podem ser editadas");
        }

        // Valida os novos dados
        validateTaskData(taskData);

        // Atualiza apenas os campos permitidos
        existingTask.setName(taskData.getName());
        existingTask.setDescription(taskData.getDescription());
        existingTask.setPriority(taskData.getPriority());
        existingTask.setExpectedCompletionDate(taskData.getExpectedCompletionDate());

        // Não permite alteração direta do status
        // Status só pode ser alterado através dos métodos específicos

        Task updatedTask = taskRepository.save(existingTask);
        log.info("Tarefa atualizada com sucesso. ID: {}", updatedTask.getId());

        return updatedTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Task findTaskById(Long id) {
        log.debug("Buscando tarefa por ID: {}", id);

        return taskRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Tarefa não encontrada com ID: {}", id);
                    return new TaskNotFoundException("Tarefa não encontrada com ID: " + id);
                });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Task> findTasksWithFilters(String name, Priority priority, Status status, Pageable pageable) {
        log.debug("Buscando tarefas com filtros - Nome: {}, Prioridade: {}, Status: {}", name, priority, status);

        return taskRepository.findAll(TaskSpecification.withFilters(name, priority, status), pageable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Task> findAllTasks() {
        log.debug("Buscando todas as tarefas");
        return taskRepository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task markTaskAsCompleted(Long id) {
        log.info("Marcando tarefa como concluída. ID: {}", id);

        Task task = findTaskById(id);

        // Valida se a transição é permitida
        if (!task.getStatus().canBeCompleted()) {
            throw new InvalidTaskStatusException(
                    String.format("Não é possível concluir tarefa com status '%s'",
                            task.getStatus().getDescription())
            );
        }

        task.setStatus(Status.COMPLETED);
        Task updatedTask = taskRepository.save(task);

        log.info("Tarefa marcada como concluída com sucesso. ID: {}", updatedTask.getId());
        return updatedTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Task markTaskAsPending(Long id) {
        log.info("Marcando tarefa como pendente. ID: {}", id);

        Task task = findTaskById(id);

        // Valida se a transição é permitida
        if (!task.getStatus().canBePending()) {
            throw new InvalidTaskStatusException(
                    String.format("Não é possível marcar como pendente tarefa com status '%s'",
                            task.getStatus().getDescription())
            );
        }

        task.setStatus(Status.PENDING);
        Task updatedTask = taskRepository.save(task);

        log.info("Tarefa marcada como pendente com sucesso. ID: {}", updatedTask.getId());
        return updatedTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTask(Long id) {
        log.info("Removendo tarefa. ID: {}", id);

        Task task = findTaskById(id);

        // Valida se a tarefa pode ser removida
        // Por exemplo, tarefas em andamento podem não ser removidas
        if (task.getStatus() == Status.PENDING) {
            log.warn("Tentativa de remover tarefa pendente. ID: {}", id);
            throw new IllegalStateException("Tarefas pendentes não podem ser removidas");
        }

        taskRepository.delete(task);
        log.info("Tarefa removida com sucesso. ID: {}", id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public List<Task> findOverdueTasks() {
        log.debug("Buscando tarefas em atraso");

        LocalDate today = LocalDate.now();
        return taskRepository.findOverdueTasks(today, Status.COMPLETED);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public long countTasksByStatus(Status status) {
        log.debug("Contando tarefas por status: {}", status);
        return taskRepository.countByStatus(status);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidCompletionDate(LocalDate date) {
        if (date == null) {
            return false;
        }
        return date.isAfter(LocalDate.now());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsTaskWithName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return taskRepository.existsByNameIgnoreCase(name.trim());
    }

    /**
     * Valida os dados básicos de uma tarefa.
     * <p>
     * Aplica validações de negócio que não são cobertas pelas
     * anotações de validação da entidade.
     * </p>
     *
     * @param task Tarefa a ser validada
     * @throws IllegalArgumentException se os dados são inválidos
     */
    private void validateTaskData(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Dados da tarefa não podem ser nulos");
        }

        // Valida nome
        if (task.getName() == null || task.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome da tarefa é obrigatório");
        }

        if (task.getName().trim().length() > 100) {
            throw new IllegalArgumentException("Nome da tarefa deve ter no máximo 100 caracteres");
        }

        // Valida descrição
        if (task.getDescription() != null && task.getDescription().length() > 500) {
            throw new IllegalArgumentException("Descrição deve ter no máximo 500 caracteres");
        }

        // Valida prioridade
        if (task.getPriority() == null) {
            throw new IllegalArgumentException("Prioridade é obrigatória");
        }

        // Valida data de conclusão
        if (task.getExpectedCompletionDate() == null) {
            throw new IllegalArgumentException("Data prevista de conclusão é obrigatória");
        }

        if (!isValidCompletionDate(task.getExpectedCompletionDate())) {
            throw new IllegalArgumentException("Data prevista de conclusão deve ser uma data futura");
        }

        log.debug("Validação dos dados da tarefa concluída com sucesso");
    }
}