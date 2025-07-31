package br.com.danilobossanova.todoapp.enums;

/**
 * Enumeração que representa os níveis de prioridade das tarefas.
 * <p>
 * Este enum define três níveis de prioridade que podem ser atribuídos às tarefas
 * para auxiliar na organização e agendamento.
 * </p>
 *
 * @author Danilo Bossanova
 * @since 1.0.0
 */
public enum Priority {

    /**
     * Tarefa de prioridade baixa.
     * <p>
     * Tarefas com prioridade baixa podem ser concluídas quando o tempo permitir
     * e não são urgentes ou críticas para conclusão imediata.
     * </p>
     */
    LOW("Baixa"),

    /**
     * Tarefa de prioridade média.
     * <p>
     * Tarefas com prioridade média devem ser concluídas em um prazo razoável
     * e têm importância moderada no fluxo de trabalho geral.
     * </p>
     */
    MEDIUM("Média"),

    /**
     * Tarefa de prioridade alta.
     * <p>
     * Tarefas com prioridade alta requerem atenção imediata e
     * devem ser concluídas o mais rápido possível devido à sua
     * natureza crítica ou prazos apertados.
     * </p>
     */
    HIGH("Alta");

    /**
     * Descrição legível da prioridade.
     */
    private final String description;

    /**
     * Construtor do enum Priority.
     *
     * @param description Descrição legível do nível de prioridade
     */
    Priority(String description) {
        this.description = description;
    }

    /**
     * Obtém a descrição legível do nível de prioridade.
     *
     * @return A descrição do nível de prioridade
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retorna a representação em string da prioridade.
     * <p>
     * Este método retorna a descrição ao invés do nome do enum
     * para melhor experiência do usuário em APIs e interfaces.
     * </p>
     *
     * @return A descrição do nível de prioridade
     */
    @Override
    public String toString() {
        return description;
    }
}