package br.com.danilobossanova.todoapp.controller;

import br.com.danilobossanova.todoapp.dto.request.TaskCreateRequest;
import br.com.danilobossanova.todoapp.dto.response.TaskResponse;
import br.com.danilobossanova.todoapp.dto.response.TaskSummaryResponse;
import br.com.danilobossanova.todoapp.dto.request.TaskUpdateRequest;
import br.com.danilobossanova.todoapp.dto.mapper.TaskMapper;
import br.com.danilobossanova.todoapp.entity.Task;
import br.com.danilobossanova.todoapp.enums.Priority;
import br.com.danilobossanova.todoapp.enums.Status;
import br.com.danilobossanova.todoapp.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller responsável por gerenciar as operações REST para tarefas.
 *
 * Implementa todos os endpoints necessários para o CRUD completo de tarefas,
 * incluindo filtros, paginação e ações específicas de mudança de status.
 *
 * Padrões aplicados:
 * - Controller Pattern: Centralizacao das requisicoes HTTP
 * - RESTful API Design: Endpoints seguem convencoes REST
 * - Response Entity Pattern: Controle completo das respostas HTTP
 *
 * @author Danilo Bossanova
 * @version 1.0
 * @since 2024-12-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Operações para gerenciamento de tarefas")
public class TaskController {

    private final TaskService taskService;
    private final TaskMapper taskMapper;

    /**
     * Lista todas as tarefas com filtros opcionais e paginação.
     *
     * @param nome Filtro por nome da tarefa (busca parcial, case-insensitive)
     * @param prioridade Filtro por prioridade (LOW, MEDIUM, HIGH)
     * @param situacao Filtro por situação/status (OPEN, PENDING, COMPLETED)
     * @param pagina Número da página (começa em 0)
     * @param tamanho Quantidade de registros por página
     * @param ordenarPor Campo para ordenação (nome, priority, status, expectedCompletionDate, createdDate)
     * @param direcao Direção da ordenação (ASC ou DESC)
     * @return Lista paginada de tarefas com resumo das informações
     */
    @GetMapping
    @Operation(summary = "Listar tarefas",
            description = "Recupera uma lista paginada de tarefas com filtros opcionais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarefas recuperada com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskSummaryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Parâmetros de consulta inválidos",
                    content = @Content)
    })
    @CrossOrigin(
            origins = {"http://localhost:3000", "http://localhost:4200"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
            allowedHeaders = "*",
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<Page<TaskSummaryResponse>> listarTarefas(
            @Parameter(description = "Filtro por nome (busca parcial)")
            @RequestParam(required = false) String nome,

            @Parameter(description = "Filtro por prioridade")
            @RequestParam(required = false) Priority prioridade,

            @Parameter(description = "Filtro por situação")
            @RequestParam(required = false) Status situacao,

            @Parameter(description = "Número da página (inicia em 0)")
            @RequestParam(defaultValue = "0") int pagina,

            @Parameter(description = "Tamanho da página")
            @RequestParam(defaultValue = "10") int tamanho,

            @Parameter(description = "Campo para ordenação")
            @RequestParam(defaultValue = "createdDate") String ordenarPor,

            @Parameter(description = "Direção da ordenação (ASC/DESC)")
            @RequestParam(defaultValue = "DESC") String direcao) {

        log.info("Solicitação de listagem de tarefas - Filtros: nome={}, prioridade={}, situacao={}, página={}, tamanho={}",
                nome, prioridade, situacao, pagina, tamanho);

        // Criação do objeto Pageable com ordenação
        Sort.Direction sortDirection = Sort.Direction.fromString(direcao);
        Pageable pageable = PageRequest.of(pagina, tamanho, Sort.by(sortDirection, ordenarPor));

        // Busca as tarefas através do service
        Page<Task> tarefasPage = taskService.findTasksWithFilters(nome, prioridade, situacao, pageable);

        // Converte para DTO usando o mapper
        Page<TaskSummaryResponse> tarefasResponse = tarefasPage.map(taskMapper::toSummaryResponse);

        log.info("Listagem concluída - {} tarefas encontradas de {} total",
                tarefasResponse.getNumberOfElements(), tarefasResponse.getTotalElements());

        return ResponseEntity.ok(tarefasResponse);
    }

    /**
     * Busca uma tarefa específica pelo ID.
     *
     * @param id Identificador único da tarefa
     * @return Dados completos da tarefa
     */
    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID",
            description = "Recupera os dados completos de uma tarefa específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa encontrada com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content)
    })
    @CrossOrigin(
            origins = {"http://localhost:3000", "http://localhost:4200"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
            allowedHeaders = "*",
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<TaskResponse> buscarTarefaPorId(
            @Parameter(description = "ID da tarefa")
            @PathVariable Long id) {

        log.info("Buscando tarefa com ID: {}", id);

        Task tarefa = taskService.findTaskById(id);
        TaskResponse response = taskMapper.toResponse(tarefa);

        log.info("Tarefa encontrada: {}", tarefa.getName());

        return ResponseEntity.ok(response);
    }

    /**
     * Cria uma nova tarefa.
     *
     * @param request Dados para criação da tarefa
     * @return Dados da tarefa criada
     */
    @PostMapping
    @Operation(summary = "Criar nova tarefa",
            description = "Cria uma nova tarefa com status inicial OPEN")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarefa criada com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                    content = @Content)
    })
    @CrossOrigin(
            origins = {"http://localhost:3000", "http://localhost:4200"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
            allowedHeaders = "*",
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<TaskResponse> criarTarefa(
            @Parameter(description = "Dados para criação da tarefa")
            @Valid @RequestBody TaskCreateRequest request) {

        log.info("Criando nova tarefa: {}", request.getName());

        // Converte DTO para entidade
        Task task = taskMapper.toEntity(request);

        // Cria a tarefa através do service
        Task tarefaCriada = taskService.createTask(task);

        // Converte entidade para DTO de resposta
        TaskResponse response = taskMapper.toResponse(tarefaCriada);

        log.info("Tarefa criada com sucesso - ID: {}", tarefaCriada.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Atualiza uma tarefa existente.
     *
     * @param id Identificador da tarefa a ser atualizada
     * @param request Dados para atualização
     * @return Dados da tarefa atualizada
     */
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa",
            description = "Atualiza os dados de uma tarefa existente (exceto status)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa atualizada com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "400", description = "Dados de entrada inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Tarefa não pode ser editada (status COMPLETED)",
                    content = @Content)
    })
    @CrossOrigin(
            origins = {"http://localhost:3000", "http://localhost:4200"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
            allowedHeaders = "*",
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<TaskResponse> atualizarTarefa(
            @Parameter(description = "ID da tarefa")
            @PathVariable Long id,

            @Parameter(description = "Dados para atualização da tarefa")
            @Valid @RequestBody TaskUpdateRequest request) {

        log.info("Atualizando tarefa ID: {} - Novo nome: {}", id, request.getName());

        // Busca a tarefa existente
        Task tarefaExistente = taskService.findTaskById(id);

        // Atualiza a entidade com os dados do request usando o mapper
        Task taskAtualizada = taskMapper.updateEntity(tarefaExistente, request);

        // Salva as alterações através do service
        Task tarefaSalva = taskService.updateTask(id, taskAtualizada);

        // Converte entidade para DTO de resposta
        TaskResponse response = taskMapper.toResponse(tarefaSalva);

        log.info("Tarefa atualizada com sucesso - ID: {}", tarefaSalva.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Remove uma tarefa.
     *
     * @param id Identificador da tarefa a ser removida
     * @return Resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Remover tarefa",
            description = "Remove uma tarefa do sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Tarefa removida com sucesso"),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content)
    })
    @CrossOrigin(
            origins = {"http://localhost:3000", "http://localhost:4200"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
            allowedHeaders = "*",
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<Void> removerTarefa(
            @Parameter(description = "ID da tarefa")
            @PathVariable Long id) {

        log.info("Removendo tarefa ID: {}", id);

        taskService.deleteTask(id);

        log.info("Tarefa removida com sucesso - ID: {}", id);

        return ResponseEntity.noContent().build();
    }

    /**
     * Marca uma tarefa como concluída.
     * Transição permitida: OPEN → COMPLETED ou PENDING → COMPLETED
     *
     * @param id Identificador da tarefa
     * @return Dados da tarefa atualizada
     */
    @PatchMapping("/{id}/concluir")
    @Operation(summary = "Concluir tarefa",
            description = "Marca uma tarefa como concluída (COMPLETED)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa concluída com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida",
                    content = @Content)
    })
    @CrossOrigin(
            origins = {"http://localhost:3000", "http://localhost:4200"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE,
                    RequestMethod.PATCH},
            allowedHeaders = "*",
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<TaskResponse> concluirTarefa(
            @Parameter(description = "ID da tarefa")
            @PathVariable Long id) {

        log.info("Concluindo tarefa ID: {}", id);

        Task tarefaConcluida = taskService.markTaskAsCompleted(id);
        TaskResponse response = taskMapper.toResponse(tarefaConcluida);

        log.info("Tarefa concluída com sucesso - ID: {}", tarefaConcluida.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Marca uma tarefa como pendente.
     * Transição permitida: OPEN → PENDING ou COMPLETED → PENDING
     *
     * @param id Identificador da tarefa
     * @return Dados da tarefa atualizada
     */
    @PatchMapping("/{id}/pendente")
    @Operation(summary = "Marcar como pendente",
            description = "Marca uma tarefa como pendente (PENDING)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa marcada como pendente com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida",
                    content = @Content)
    })
    @CrossOrigin(
            origins = {"http://localhost:3000", "http://localhost:4200"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH},
            allowedHeaders = "*",
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<TaskResponse> marcarComoPendente(
            @Parameter(description = "ID da tarefa")
            @PathVariable Long id) {

        log.info("Marcando tarefa como pendente - ID: {}", id);

        Task tarefaPendente = taskService.markTaskAsPending(id);
        TaskResponse response = taskMapper.toResponse(tarefaPendente);

        log.info("Tarefa marcada como pendente com sucesso - ID: {}", tarefaPendente.getId());

        return ResponseEntity.ok(response);
    }

    /**
     * Reabrir uma tarefa (volta para status OPEN).
     * Transição permitida: PENDING → OPEN
     *
     * @param id Identificador da tarefa
     * @return Dados da tarefa atualizada
     */
    @PatchMapping("/{id}/reabrir")
    @Operation(summary = "Reabrir tarefa",
            description = "Reabrir uma tarefa pendente (PENDING → OPEN)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarefa reaberta com sucesso",
                    content = @Content(schema = @Schema(implementation = TaskResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarefa não encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Transição de status inválida",
                    content = @Content)
    })
    @CrossOrigin(
            origins = {"http://localhost:3000", "http://localhost:4200"},
            methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.PATCH},
            allowedHeaders = "*",
            allowCredentials = "true",
            maxAge = 3600
    )
    public ResponseEntity<TaskResponse> reabrirTarefa(
            @Parameter(description = "ID da tarefa")
            @PathVariable Long id) {

        log.info("Reabrindo tarefa ID: {}", id);

        // Busca a tarefa atual
        Task task = taskService.findTaskById(id);

        // Valida se a transição é permitida (PENDING → OPEN)
        if (task.getStatus() != Status.PENDING) {
            throw new IllegalStateException("Apenas tarefas pendentes podem ser reabertas");
        }

        // Cria uma cópia da tarefa com status OPEN para atualização
        Task taskParaAtualizacao = Task.builder()
                .name(task.getName())
                .description(task.getDescription())
                .priority(task.getPriority())
                .status(Status.OPEN)
                .expectedCompletionDate(task.getExpectedCompletionDate())
                .build();

        // Atualiza através do service
        Task tarefaReaberta = taskService.updateTask(id, taskParaAtualizacao);
        TaskResponse response = taskMapper.toResponse(tarefaReaberta);

        log.info("Tarefa reaberta com sucesso - ID: {}", tarefaReaberta.getId());

        return ResponseEntity.ok(response);
    }
}