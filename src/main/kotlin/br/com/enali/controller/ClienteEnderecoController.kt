package br.com.enali.controller

import br.com.enali.dto.EnderecoRequestDTO
import br.com.enali.dto.EnderecoResponseDTO
import br.com.enali.mapper.EnderecoMapper
import br.com.enali.model.Cliente
import br.com.enali.service.ClienteService
import br.com.enali.service.EnderecoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/clientes/me/enderecos")
class ClienteEnderecoController(
    private val enderecoService: EnderecoService,
    private val clienteService: ClienteService
) {

    @GetMapping
    fun listarMeusEnderecos(authentication: Authentication): List<EnderecoResponseDTO> {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        val clienteId = clienteResponse.id

        return enderecoService.listarTodos()
            .filter { it.cliente?.id == clienteId }
            .map { EnderecoMapper.toDTO(it) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criarMeuEndereco(
        authentication: Authentication,
        @Valid @RequestBody dto: EnderecoRequestDTO
    ): EnderecoResponseDTO {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        
        val cliente = Cliente(
            id = clienteResponse.id,
            nome = clienteResponse.nome,
            email = clienteResponse.email,
            senha = "",
            cpf = clienteResponse.cpf,
            dataNascimento = clienteResponse.dataNascimento,
            ativo = clienteResponse.ativo
        )

        val endereco = EnderecoMapper.toModel(dto)
        val enderecoSalvo = enderecoService.salvarParaCliente(cliente, endereco)

        return EnderecoMapper.toDTO(enderecoSalvo)
    }

    @PutMapping("/{id}")
    fun atualizarMeuEndereco(
        authentication: Authentication,
        @PathVariable id: Long,
        @Valid @RequestBody dto: EnderecoRequestDTO
    ): EnderecoResponseDTO {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        val enderecoExistente = enderecoService.procurarPorId(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado.")

        if (enderecoExistente.cliente?.id != clienteResponse.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para alterar este endereço.")
        }

        val enderecoAtualizado = EnderecoMapper.toModel(dto)
        val enderecoSalvo = enderecoService.atualizar(id, enderecoAtualizado)

        return EnderecoMapper.toDTO(enderecoSalvo)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletarMeuEndereco(
        authentication: Authentication,
        @PathVariable id: Long
    ) {
        val clienteResponse = clienteService.buscarMeusDados(authentication.name)
        val enderecoExistente = enderecoService.procurarPorId(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado.")

        if (enderecoExistente.cliente?.id != clienteResponse.id) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Você não tem permissão para excluir este endereço.")
        }

        enderecoService.deletar(id)
    }
}