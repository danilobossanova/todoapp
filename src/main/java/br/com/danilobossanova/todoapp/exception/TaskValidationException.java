package br.com.danilobossanova.todoapp.exception;

import java.util.List;
import java.util.Set;

/**
 * Exceção lançada quando dados de entrada para operações de tarefa são inválidos.
 * <p>
 * Esta exceção é específica para violações de regras de validação
 * de dados de entrada, podendo conter múltiplas mensagens de erro.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
public class TaskValidationException extends RuntimeException {

    private final List<String> validationErrors;

    /**
     * Construtor padrão com mensagem genérica.
     */
    public TaskValidationException() {
        super("Dados da tarefa são inválidos");
        this.validationErrors = List.of();
    }

    /**
     * Construtor com mensagem personalizada.
     *
     * @param message Mensagem de erro específica
     */
    public TaskValidationException(String message) {
        super(message);
        this.validationErrors = List.of();
    }

    /**
     * Construtor com mensagem e causa raiz.
     *
     * @param message Mensagem de erro específica
     * @param cause Causa original da exceção
     */
    public TaskValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = List.of();
    }

    /**
     * Construtor com múltiplos erros de validação.
     *
     * @param validationErrors Lista de erros de validação
     */
    public TaskValidationException(List<String> validationErrors) {
        super(String.format("Dados da tarefa são inválidos: %s", String.join(", ", validationErrors)));
        this.validationErrors = List.copyOf(validationErrors);
    }

    /**
     * Construtor com mensagem personalizada e múltiplos erros.
     *
     * @param message Mensagem principal
     * @param validationErrors Lista de erros de validação
     */
    public TaskValidationException(String message, List<String> validationErrors) {
        super(message);
        this.validationErrors = List.copyOf(validationErrors);
    }

    /**
     * Obtém a lista de erros de validação.
     *
     * @return Lista imutável de erros de validação
     */
    public List<String> getValidationErrors() {
        return validationErrors;
    }

    /**
     * Verifica se existem erros de validação específicos.
     *
     * @return true se existem erros de validação
     */
    public boolean hasValidationErrors() {
        return !validationErrors.isEmpty();
    }

    /**
     * Construtor de conveniência para campo obrigatório não informado.
     *
     * @param fieldName Nome do campo obrigatório
     * @return Nova instância da exceção
     */
    public static TaskValidationException requiredField(String fieldName) {
        return new TaskValidationException(String.format("Campo '%s' é obrigatório", fieldName));
    }

    /**
     * Construtor de conveniência para valor inválido de campo.
     *
     * @param fieldName Nome do campo
     * @param value Valor inválido
     * @param expectedFormat Formato esperado
     * @return Nova instância da exceção
     */
    public static TaskValidationException invalidFieldValue(String fieldName, Object value, String expectedFormat) {
        return new TaskValidationException(
                String.format("Campo '%s' com valor '%s' é inválido. Formato esperado: %s",
                        fieldName, value, expectedFormat)
        );
    }

    /**
     * Construtor de conveniência para data inválida.
     *
     * @param fieldName Nome do campo de data
     * @param reason Motivo da invalidez
     * @return Nova instância da exceção
     */
    public static TaskValidationException invalidDate(String fieldName, String reason) {
        return new TaskValidationException(
                String.format("Data do campo '%s' é inválida: %s", fieldName, reason)
        );
    }
}