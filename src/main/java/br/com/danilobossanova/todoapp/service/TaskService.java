package br.com.danilobossanova.todoapp.service;

import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface de serviço para operações de negócio relacionadas às tarefas.
 * <p>
 * Esta interface define todas as operações de negócio disponíveis
 * para o gerenciamento de tarefas no sistema, incluindo validações
 * e regras específicas do domínio.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
public interface TaskService {

    /**
     * Cria uma nova tarefa no sistema.
     * <p>
     * Valida os dados de entrada, aplica regras de negócio
     * e persiste a tarefa com status inicial OPEN.
     * </p>
     *
     * @param task Dados da tarefa a ser criada
     * @return Tarefa criada com ID gerado
     * @throws IllegalArgumentException se os dados são inválidos
     */
    Task createTask(Task task);

    /**
     * Atualiza uma tarefa existente.
     * <p>
     * Valida se a tarefa pode ser editada e aplica as alterações.
     * Não permite alteração direta do status.
     * </p>
     *
     * @param id ID da tarefa a ser atualizada
     * @param taskData Novos dados da tarefa
     * @return Tarefa atualizada
     * @throws IllegalArgumentException se os dados são inválidos
     * @throws IllegalStateException se a tarefa não pode ser editada
     */
    Task updateTask(Long id, Task taskData);

    /**
     * Busca uma tarefa por ID.
     *
     * @param id ID da tarefa
     * @return Tarefa encontrada
     * @throws RuntimeException se a tarefa não for encontrada
     */
    Task findTaskById(Long id);

    /**
     * Lista todas as tarefas com paginação e filtros.
     * <p>
     * Permite filtros combinados por nome, prioridade e status,
     * com suporte a paginação e ordenação.
     * </p>
     *
     * @param name Filtro por nome (contém, case-insensitive)
     * @param priority Filtro por prioridade
     * @param status Filtro por status
     * @param pageable Configurações de paginação e ordenação
     * @return Página de tarefas que atendem aos critérios
     */
    Page<Task> findTasksWithFilters(String name, Priority priority, Status status, Pageable pageable);

    /**
     * Lista todas as tarefas sem filtros.
     *
     * @return Lista completa de tarefas
     */
    List<Task> findAllTasks();

    /**
     * Marca uma tarefa como concluída.
     * <p>
     * Valida se a transição de status é permitida antes de aplicar.
     * Só permite conclusão de tarefas com status OPEN ou PENDING.
     * </p>
     *
     * @param id ID da tarefa a ser concluída
     * @return Tarefa atualizada
     * @throws IllegalStateException se a transição não é permitida
     */
    Task markTaskAsCompleted(Long id);

    /**
     * Marca uma tarefa como pendente.
     * <p>
     * Valida se a transição de status é permitida antes de aplicar.
     * Só permite marcar como pendente tarefas com status OPEN ou COMPLETED.
     * </p>
     *
     * @param id ID da tarefa a ser marcada como pendente
     * @return Tarefa atualizada
     * @throws IllegalStateException se a transição não é permitida
     */
    Task markTaskAsPending(Long id);

    /**
     * Remove uma tarefa do sistema.
     * <p>
     * Valida se a tarefa pode ser removida antes de executar a operação.
     * </p>
     *
     * @param id ID da tarefa a ser removida
     * @throws IllegalStateException se a tarefa não pode ser removida
     */
    void deleteTask(Long id);

    /**
     * Busca tarefas em atraso.
     * <p>
     * Retorna tarefas não concluídas cuja data prevista de conclusão
     * já passou da data atual.
     * </p>
     *
     * @return Lista de tarefas em atraso
     */
    List<Task> findOverdueTasks();

    /**
     * Conta o número de tarefas por status.
     *
     * @param status Status a ser contado
     * @return Número de tarefas com o status especificado
     */
    long countTasksByStatus(Status status);

    /**
     * Valida se uma data é válida para conclusão de tarefa.
     *
     * @param date Data a ser validada
     * @return true se a data é válida (futura)
     */
    boolean isValidCompletionDate(LocalDate date);

    /**
     * Verifica se existe tarefa com o nome especificado.
     *
     * @param name Nome a ser verificado
     * @return true se existe tarefa com o nome
     */
    boolean existsTaskWithName(String name);
}