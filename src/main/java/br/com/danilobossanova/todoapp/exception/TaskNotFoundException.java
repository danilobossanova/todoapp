package br.com.danilobossanova.todoapp.exception;

/**
 * Exceção lançada quando uma tarefa não é encontrada no sistema.
 * <p>
 * Esta exceção é uma RuntimeException específica para casos onde
 * uma tarefa é solicitada por ID mas não existe no banco de dados.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
public class TaskNotFoundException extends RuntimeException {

    /**
     * Construtor padrão com mensagem genérica.
     */
    public TaskNotFoundException() {
        super("Tarefa não encontrada");
    }

    /**
     * Construtor com mensagem personalizada.
     *
     * @param message Mensagem de erro específica
     */
    public TaskNotFoundException(String message) {
        super(message);
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem de erro específica
     * @param cause Causa original da exceção
     */
    public TaskNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Construtor de conveniência para tarefa não encontrada por ID.
     *
     * @param taskId ID da tarefa que não foi encontrada
     * @return Nova instância da exceção com mensagem formatada
     */
    public static TaskNotFoundException withId(Long taskId) {
        return new TaskNotFoundException(String.format("Tarefa não encontrada com ID: %d", taskId));
    }

    /**
     * Construtor de conveniência para tarefa não encontrada por nome.
     *
     * @param taskName Nome da tarefa que não foi encontrada
     * @return Nova instância da exceção com mensagem formatada
     */
    public static TaskNotFoundException withName(String taskName) {
        return new TaskNotFoundException(String.format("Tarefa não encontrada com nome: '%s'", taskName));
    }
}