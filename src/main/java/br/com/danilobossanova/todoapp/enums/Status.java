package br.com.danilobossanova.todoapp.enums;

import java.util.Set;

/**
 * Enumeração que representa os possíveis status das tarefas.
 * <p>
 * Este enum define os estados do ciclo de vida de uma tarefa e inclui
 * a lógica de negócio para transições válidas de status.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
public enum Status {

    /**
     * Tarefa recém-criada e ainda não iniciada.
     * <p>
     * Este é o status inicial atribuído a todas as novas tarefas.
     * A partir deste status, as tarefas podem ser movidas para PENDING ou COMPLETED.
     * </p>
     */
    OPEN("Aberta"),

    /**
     * Tarefa em andamento ou aguardando conclusão.
     * <p>
     * Tarefas neste status estão sendo trabalhadas ativamente ou
     * estão na fila para trabalho. Podem ser movidas para COMPLETED ou de volta para OPEN.
     * </p>
     */
    PENDING("Pendente"),

    /**
     * Tarefa foi finalizada com sucesso.
     * <p>
     * Tarefas neste status foram concluídas e verificadas.
     * Podem ser movidas de volta para PENDING se retrabalho for necessário.
     * </p>
     */
    COMPLETED("Concluída");

    /**
     * Descrição legível do status.
     */
    private final String description;

    /**
     * Construtor do enum Status.
     *
     * @param description Descrição legível do status
     */
    Status(String description) {
        this.description = description;
    }

    /**
     * Obtém a descrição legível do status.
     *
     * @return A descrição do status
     */
    public String getDescription() {
        return description;
    }

    /**
     * Verifica se a transição do status atual para o status alvo é válida.
     * <p>
     * Regras de negócio para transições de status:
     * <ul>
     *   <li>OPEN → PENDING, COMPLETED</li>
     *   <li>PENDING → OPEN, COMPLETED</li>
     *   <li>COMPLETED → PENDING</li>
     * </ul>
     * </p>
     *
     * @param targetStatus O status para o qual se deseja fazer a transição
     * @return true se a transição é válida, false caso contrário
     */
    public boolean canTransitionTo(Status targetStatus) {
        if (targetStatus == null || targetStatus == this) {
            return false;
        }

        return switch (this) {
            case OPEN -> targetStatus == PENDING || targetStatus == COMPLETED;
            case PENDING -> targetStatus == OPEN || targetStatus == COMPLETED;
            case COMPLETED -> targetStatus == PENDING;
        };
    }

    /**
     * Obtém todos os status de destino válidos a partir do status atual.
     *
     * @return Conjunto de status de destino válidos
     */
    public Set<Status> getValidTransitions() {
        return switch (this) {
            case OPEN -> Set.of(PENDING, COMPLETED);
            case PENDING -> Set.of(OPEN, COMPLETED);
            case COMPLETED -> Set.of(PENDING);
        };
    }

    /**
     * Verifica se o status atual permite que a tarefa seja marcada como concluída.
     *
     * @return true se a tarefa pode ser concluída a partir deste status
     */
    public boolean canBeCompleted() {
        return this == OPEN || this == PENDING;
    }

    /**
     * Verifica se o status atual permite que a tarefa seja marcada como pendente.
     *
     * @return true se a tarefa pode ser marcada como pendente a partir deste status
     */
    public boolean canBePending() {
        return this == OPEN || this == COMPLETED;
    }

    /**
     * Retorna a representação em string do status.
     * <p>
     * Este método retorna a descrição ao invés do nome do enum
     * para melhor experiência do usuário em APIs e interfaces.
     * </p>
     *
     * @return A descrição do status
     */
    @Override
    public String toString() {
        return description;
    }
}