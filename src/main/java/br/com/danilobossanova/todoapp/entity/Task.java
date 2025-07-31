package br.com.danilobossanova.todoapp.entity;

import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa uma tarefa no sistema.
 * <p>
 * Esta classe define a estrutura de dados de uma tarefa, incluindo
 * informações como nome, descrição, prioridade, situação e datas relevantes.
 * A responsabilidade desta entidade é apenas mapear os dados para o banco.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"description"})
public class Task {

    /**
     * Identificador único da tarefa.
     * <p>
     * Este campo é gerado automaticamente pelo banco de dados
     * usando a estratégia de identidade.
     * </p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * Nome da tarefa.
     * <p>
     * Campo obrigatório que deve conter entre 1 e 100 caracteres.
     * Não pode ser nulo, vazio ou conter apenas espaços em branco.
     * </p>
     */
    @NotBlank(message = "Nome da tarefa é obrigatório")
    @Size(max = 100, message = "Nome da tarefa deve ter no máximo 100 caracteres")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * Descrição detalhada da tarefa.
     * <p>
     * Campo opcional que pode conter até 500 caracteres
     * com informações adicionais sobre a tarefa.
     * </p>
     */
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Column(name = "description", length = 500)
    private String description;

    /**
     * Nível de prioridade da tarefa.
     * <p>
     * Campo obrigatório que define a importância da tarefa.
     * Valores possíveis: LOW (Baixa), MEDIUM (Média), HIGH (Alta).
     * </p>
     */
    @NotNull(message = "Prioridade é obrigatória")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority;

    /**
     * Status atual da tarefa.
     * <p>
     * Campo que representa o estado atual da tarefa no seu ciclo de vida.
     * Valor padrão é OPEN (Aberta). Valores possíveis: OPEN, PENDING, COMPLETED.
     * Este campo só pode ser alterado através de ações específicas no serviço.
     * </p>
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Status status = Status.OPEN;

    /**
     * Data prevista para conclusão da tarefa.
     * <p>
     * Campo obrigatório que deve ser uma data futura.
     * Não pode ser anterior à data atual.
     * </p>
     */
    @NotNull(message = "Data prevista de conclusão é obrigatória")
    @Future(message = "Data prevista de conclusão deve ser uma data futura")
    @Column(name = "expected_completion_date", nullable = false)
    private LocalDate expectedCompletionDate;

    /**
     * Data e hora de criação da tarefa.
     * <p>
     * Campo gerado automaticamente no momento da criação da tarefa.
     * Utiliza o timestamp do banco de dados.
     * </p>
     */
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    /**
     * Construtor para criação de tarefa com campos obrigatórios.
     * <p>
     * Utilizado principalmente em testes e criação programática de tarefas.
     * </p>
     *
     * @param name Nome da tarefa
     * @param priority Prioridade da tarefa
     * @param expectedCompletionDate Data prevista para conclusão
     */
    public Task(String name, Priority priority, LocalDate expectedCompletionDate) {
        this.name = name;
        this.priority = priority;
        this.expectedCompletionDate = expectedCompletionDate;
        this.status = Status.OPEN;
    }

    /**
     * Construtor para criação de tarefa com descrição.
     * <p>
     * Utilizado principalmente em testes e criação programática de tarefas.
     * </p>
     *
     * @param name Nome da tarefa
     * @param description Descrição da tarefa
     * @param priority Prioridade da tarefa
     * @param expectedCompletionDate Data prevista para conclusão
     */
    public Task(String name, String description, Priority priority, LocalDate expectedCompletionDate) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.expectedCompletionDate = expectedCompletionDate;
        this.status = Status.OPEN;
    }
}