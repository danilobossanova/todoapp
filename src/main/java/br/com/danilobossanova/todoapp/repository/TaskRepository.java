package br.com.danilobossanova.todoapp.repository;

import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para operações de acesso a dados da entidade Task.
 * <p>
 * Esta interface estende JpaRepository para operações CRUD básicas
 * e JpaSpecificationExecutor para consultas dinâmicas com filtros.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    /**
     * Busca tarefas por status específico.
     *
     * @param status Status da tarefa a ser filtrado
     * @return Lista de tarefas com o status especificado
     */
    List<Task> findByStatus(Status status);

    /**
     * Busca tarefas por prioridade específica.
     *
     * @param priority Prioridade da tarefa a ser filtrada
     * @return Lista de tarefas com a prioridade especificada
     */
    List<Task> findByPriority(Priority priority);

    /**
     * Busca tarefas que contenham o texto especificado no nome.
     * <p>
     * Busca case-insensitive utilizando LIKE com wildcards.
     * </p>
     *
     * @param name Texto a ser procurado no nome da tarefa
     * @return Lista de tarefas cujo nome contém o texto especificado
     */
    List<Task> findByNameContainingIgnoreCase(String name);

    /**
     * Busca tarefas criadas em um período específico.
     *
     * @param startDate Data inicial do período
     * @param endDate Data final do período
     * @return Lista de tarefas criadas no período especificado
     */
    List<Task> findByCreatedDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Busca tarefas com data prevista de conclusão vencida.
     * <p>
     * Retorna tarefas não concluídas cuja data prevista já passou.
     * </p>
     *
     * @param currentDate Data atual para comparação
     * @return Lista de tarefas em atraso
     */
    @Query("SELECT t FROM Task t WHERE t.expectedCompletionDate < :currentDate AND t.status != :completedStatus")
    List<Task> findOverdueTasks(@Param("currentDate") LocalDate currentDate,
                                @Param("completedStatus") Status completedStatus);

    /**
     * Busca tarefas por prioridade e status combinados.
     *
     * @param priority Prioridade da tarefa
     * @param status Status da tarefa
     * @return Lista de tarefas que atendem aos critérios
     */
    List<Task> findByPriorityAndStatus(Priority priority, Status status);

    /**
     * Conta o número de tarefas por status.
     *
     * @param status Status a ser contado
     * @return Número de tarefas com o status especificado
     */
    long countByStatus(Status status);

    /**
     * Verifica se existe tarefa com o nome especificado.
     * <p>
     * Útil para validações de duplicidade.
     * </p>
     *
     * @param name Nome da tarefa a ser verificado
     * @return true se existe tarefa com o nome especificado
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Busca tarefa por ID com validação de existência.
     * <p>
     * Método utilitário que pode ser usado no service para
     * buscar tarefas com tratamento de exceção personalizado.
     * </p>
     *
     * @param id ID da tarefa
     * @return Optional contendo a tarefa se encontrada
     */
    Optional<Task> findById(Long id);

    /**
     * Busca todas as tarefas com paginação e ordenação utilizando Specifications.
     * <p>
     * Este método é herdado de JpaSpecificationExecutor e permite
     * filtros dinâmicos combinados com paginação.
     * </p>
     *
     * @param spec Specification contendo os critérios de filtro
     * @param pageable Configuração de paginação e ordenação
     * @return Página de tarefas que atendem aos critérios
     */
    Page<Task> findAll(Specification<Task> spec, Pageable pageable);
}