package br.com.danilobossanova.todoapp.exception;

import br.com.danilobossanova.todoapp.enums.Status;

/**
 * Exceção lançada quando uma transição de status inválida é tentada.
 * <p>
 * Esta exceção é específica para violações das regras de negócio
 * relacionadas às transições de status das tarefas.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
public class InvalidTaskStatusException extends RuntimeException {

    /**
     * Construtor padrão com mensagem genérica.
     */
    public InvalidTaskStatusException() {
        super("Transição de status inválida");
    }

    /**
     * Construtor com mensagem personalizada.
     *
     * @param message Mensagem de erro específica
     */
    public InvalidTaskStatusException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem de erro específica
     * @param cause Causa original da exceção
     */
    public InvalidTaskStatusException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor de conveniência para transição inválida entre status específicos.
     *
     * @param fromStatus Status atual da tarefa
     * @param toStatus Status desejado para transição
     * @return Nova instância da exceção com mensagem formatada
     */
    public static InvalidTaskStatusException invalidTransition(Status fromStatus, Status toStatus) {
        return new InvalidTaskStatusException(
                String.format("Transição inválida de '%s' para '%s'",
                        fromStatus.getDescription(),
                        toStatus.getDescription())
        );
    }

    /**
     * Construtor de conveniência para operação não permitida no status atual.
     *
     * @param operation Operação que foi tentada
     * @param currentStatus Status atual da tarefa
     * @return Nova instância da exceção com mensagem formatada
     */
    public static InvalidTaskStatusException operationNotAllowed(String operation, Status currentStatus) {
        return new InvalidTaskStatusException(
                String.format("Operação '%s' não permitida para tarefa com status '%s'",
                        operation,
                        currentStatus.getDescription())
        );
    }

    /**
     * Construtor de conveniência para tentativa de conclusão inválida.
     *
     * @param currentStatus Status atual da tarefa
     * @return Nova instância da exceção com mensagem formatada
     */
    public static InvalidTaskStatusException cannotComplete(Status currentStatus) {
        return new InvalidTaskStatusException(
                String.format("Não é possível concluir tarefa com status '%s'",
                        currentStatus.getDescription())
        );
    }

    /**
     * Construtor de conveniência para tentativa de marcar como pendente inválida.
     *
     * @param currentStatus Status atual da tarefa
     * @return Nova instância da exceção com mensagem formatada
     */
    public static InvalidTaskStatusException cannotMarkAsPending(Status currentStatus) {
        return new InvalidTaskStatusException(
                String.format("Não é possível marcar como pendente tarefa com status '%s'",
                        currentStatus.getDescription())
        );
    }
}