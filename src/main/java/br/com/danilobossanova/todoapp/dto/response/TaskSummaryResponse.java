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

/**
 * DTO para respostas resumidas de tarefa.
 * <p>
 * Esta classe representa uma versão simplificada da tarefa,
 * utilizada principalmente em listagens e operações que não
 * requerem todos os detalhes da entidade.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados resumidos de uma tarefa")
public class TaskSummaryResponse {

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
     * Indica se a tarefa está em atraso.
     */
    @Schema(description = "Indica se a tarefa está em atraso", example = "false")
    private Boolean overdue;
}