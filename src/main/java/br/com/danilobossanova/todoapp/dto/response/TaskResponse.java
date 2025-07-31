package br.com.danilobossanova.todoapp.dto.response;

import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO para respostas contendo dados de tarefa.
 * <p>
 * Esta classe representa uma tarefa completa retornada pelas APIs,
 * incluindo todos os campos e metadados do sistema.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados completos de uma tarefa")
public class TaskResponse {

    /**
     * Identificador único da tarefa.
     */
    @Schema(description = "Identificador único da tarefa", example = "1")
    private Long id;

    /**
     * Nome da tarefa.
     */
    @Schema(description = "Nome da tarefa", example = "Implementar autenticação JWT")
    private String name;

    /**
     * Descrição detalhada da tarefa.
     */
    @Schema(description = "Descrição detalhada da tarefa",
            example = "Implementar sistema de autenticação utilizando JWT tokens")
    private String description;

    /**
     * Nível de prioridade da tarefa.
     */
    @Schema(description = "Nível de prioridade da tarefa", example = "MEDIUM")
    private Priority priority;

    /**
     * Descrição legível da prioridade.
     */
    @Schema(description = "Descrição legível da prioridade", example = "Média")
    private String priorityDescription;

    /**
     * Status atual da tarefa.
     */
    @Schema(description = "Status atual da tarefa", example = "OPEN")
    private Status status;

    /**
     * Descrição legível do status.
     */
    @Schema(description = "Descrição legível do status", example = "Aberta")
    private String statusDescription;

    /**
     * Data prevista para conclusão da tarefa.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Data prevista para conclusão da tarefa", example = "2025-08-15", format = "date")
    private LocalDate expectedCompletionDate;

    /**
     * Data e hora de criação da tarefa.
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Data e hora de criação da tarefa", example = "2025-07-31T19:30:00", format = "date-time")
    private LocalDateTime createdDate;

    /**
     * Indica se a tarefa está em atraso.
     * <p>
     * Campo calculado baseado na data prevista de conclusão
     * e no status atual da tarefa.
     * </p>
     */
    @Schema(description = "Indica se a tarefa está em atraso", example = "false")
    private Boolean overdue;

    /**
     * Indica se a tarefa pode ser editada.
     * <p>
     * Campo calculado baseado no status atual da tarefa.
     * </p>
     */
    @Schema(description = "Indica se a tarefa pode ser editada", example = "true")
    private Boolean canBeEdited;

    /**
     * Lista de ações disponíveis para a tarefa.
     * <p>
     * Campo calculado baseado no status atual e regras de transição.
     * </p>
     */
    @Schema(description = "Lista de ações disponíveis para a tarefa",
            example = "[\"marcar-como-concluida\", \"marcar-como-pendente\", \"editar\", \"excluir\"]")
    private java.util.List<String> availableActions;
}