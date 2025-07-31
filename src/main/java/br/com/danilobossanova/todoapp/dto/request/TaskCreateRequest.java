package br.com.danilobossanova.todoapp.dto.request;

import br.com.danilobossanova.todoapp.enums.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para requisições de criação de tarefa.
 * <p>
 * Esta classe representa os dados necessários para criar uma nova tarefa,
 * incluindo todas as validações de entrada obrigatórias.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dados para criação de uma nova tarefa")
public class TaskCreateRequest {

    /**
     * Nome da tarefa.
     * <p>
     * Campo obrigatório que identifica a tarefa.
     * Deve conter entre 1 e 100 caracteres.
     * </p>
     */
    @NotBlank(message = "Nome da tarefa é obrigatório")
    @Size(max = 100, message = "Nome da tarefa deve ter no máximo 100 caracteres")
    @Schema(description = "Nome da tarefa", example = "Implementar autenticação JWT", required = true)
    private String name;

    /**
     * Descrição detalhada da tarefa.
     * <p>
     * Campo opcional com informações adicionais sobre a tarefa.
     * Pode conter até 500 caracteres.
     * </p>
     */
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição detalhada da tarefa",
            example = "Implementar sistema de autenticação utilizando JWT tokens para secured endpoints")
    private String description;

    /**
     * Nível de prioridade da tarefa.
     * <p>
     * Campo obrigatório que define a importância da tarefa.
     * Valor padrão sugerido: BAIXA.
     * </p>
     */
    @NotNull(message = "Prioridade é obrigatória")
    @Schema(description = "Nível de prioridade da tarefa", example = "MEDIUM", required = true)
    private Priority priority;

    /**
     * Data prevista para conclusão da tarefa.
     * <p>
     * Campo obrigatório que deve ser uma data futura.
     * Formato esperado: yyyy-MM-dd.
     * </p>
     */
    @NotNull(message = "Data prevista de conclusão é obrigatória")
    @Future(message = "Data prevista de conclusão deve ser uma data futura")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Schema(description = "Data prevista para conclusão da tarefa",
            example = "2025-08-15",
            required = true,
            format = "date")
    private LocalDate expectedCompletionDate;
}
