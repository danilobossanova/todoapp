package br.com.danilobossanova.todoapp.specification;

import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitária para criar Specifications dinâmicas para a entidade Task.
 * <p>
 * Esta classe implementa o padrão Specification para permitir consultas
 * dinâmicas e combinadas na entidade Task, facilitando filtros complexos.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
public class TaskSpecification {

    /**
     * Cria uma Specification para filtrar tarefas por nome.
     * <p>
     * Utiliza busca case-insensitive com LIKE para encontrar
     * tarefas cujo nome contenha o texto especificado.
     * </p>
     *
     * @param name Texto a ser procurado no nome da tarefa
     * @return Specification para filtro por nome
     */
    public static Specification<Task> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase().trim() + "%"
            );
        };
    }

    /**
     * Cria uma Specification para filtrar tarefas por prioridade.
     *
     * @param priority Prioridade da tarefa a ser filtrada
     * @return Specification para filtro por prioridade
     */
    public static Specification<Task> hasPriority(Priority priority) {
        return (root, query, criteriaBuilder) -> {
            if (priority == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("priority"), priority);
        };
    }

    /**
     * Cria uma Specification para filtrar tarefas por status.
     *
     * @param status Status da tarefa a ser filtrado
     * @return Specification para filtro por status
     */
    public static Specification<Task> hasStatus(Status status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }

    /**
     * Cria uma Specification para filtrar tarefas criadas após uma data específica.
     *
     * @param date Data mínima de criação
     * @return Specification para filtro por data de criação
     */
    public static Specification<Task> createdAfter(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(
                    criteriaBuilder.function("DATE", LocalDate.class, root.get("createdDate")),
                    date
            );
        };
    }

    /**
     * Cria uma Specification para filtrar tarefas criadas antes de uma data específica.
     *
     * @param date Data máxima de criação
     * @return Specification para filtro por data de criação
     */
    public static Specification<Task> createdBefore(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(
                    criteriaBuilder.function("DATE", LocalDate.class, root.get("createdDate")),
                    date
            );
        };
    }

    /**
     * Cria uma Specification para filtrar tarefas em atraso.
     * <p>
     * Retorna tarefas não concluídas cuja data prevista de conclusão
     * é anterior à data atual.
     * </p>
     *
     * @return Specification para filtro de tarefas em atraso
     */
    public static Specification<Task> isOverdue() {
        return (root, query, criteriaBuilder) -> {
            LocalDate today = LocalDate.now();
            return criteriaBuilder.and(
                    criteriaBuilder.lessThan(root.get("expectedCompletionDate"), today),
                    criteriaBuilder.notEqual(root.get("status"), Status.COMPLETED)
            );
        };
    }

    /**
     * Cria uma Specification combinada para filtros múltiplos.
     * <p>
     * Este método implementa o padrão Builder para combinar
     * múltiplos filtros de forma dinâmica e flexível.
     * </p>
     *
     * @param name Filtro por nome (opcional)
     * @param priority Filtro por prioridade (opcional)
     * @param status Filtro por status (opcional)
     * @return Specification combinada com todos os filtros aplicados
     */
    public static Specification<Task> withFilters(String name, Priority priority, Status status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filtro por nome
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("name")),
                        "%" + name.toLowerCase().trim() + "%"
                ));
            }

            // Filtro por prioridade
            if (priority != null) {
                predicates.add(criteriaBuilder.equal(root.get("priority"), priority));
            }

            // Filtro por status
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            // Combina todos os predicados com AND
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Cria uma Specification para busca avançada com múltiplos critérios.
     * <p>
     * Permite filtros por período de criação, data de conclusão e texto livre.
     * </p>
     *
     * @param name Filtro por nome (opcional)
     * @param priority Filtro por prioridade (opcional)
     * @param status Filtro por status (opcional)
     * @param createdFrom Data inicial de criação (opcional)
     * @param createdTo Data final de criação (opcional)
     * @param includeOverdue Se deve incluir apenas tarefas em atraso
     * @return Specification com filtros avançados
     */
    public static Specification<Task> withAdvancedFilters(String name, Priority priority, Status status,
                                                          LocalDate createdFrom, LocalDate createdTo,
                                                          Boolean includeOverdue) {
        return Specification.where(hasName(name))
                .and(hasPriority(priority))
                .and(hasStatus(status))
                .and(createdFrom != null ? createdAfter(createdFrom) : null)
                .and(createdTo != null ? createdBefore(createdTo) : null)
                .and(Boolean.TRUE.equals(includeOverdue) ? isOverdue() : null);
    }
}