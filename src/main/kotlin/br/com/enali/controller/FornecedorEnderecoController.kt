package br.com.enali.controller

import br.com.enali.dto.EnderecoRequestDTO
import br.com.enali.dto.EnderecoResponseDTO
import br.com.enali.mapper.EnderecoMapper
import br.com.enali.service.FornecedorService
import br.com.enali.service.EnderecoService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/fornecedores/{fornecedorId}/enderecos")
class FornecedorEnderecoController(
    private val fornecedorService: FornecedorService,
    private val enderecoService: EnderecoService
) {

    @GetMapping
    fun listarEnderecosDoFornecedor(@PathVariable fornecedorId: Long): List<EnderecoResponseDTO> {
        val fornecedor = fornecedorService.procurarPorId(fornecedorId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado.")

        return enderecoService.listarTodos()
            .filter { it.fornecedor?.id == fornecedor.id }
            .map { EnderecoMapper.toDTO(it) }
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun criarEnderecoParaFornecedor(
        @PathVariable fornecedorId: Long,
        @Valid @RequestBody dto: EnderecoRequestDTO
    ): EnderecoResponseDTO {
        val fornecedor = fornecedorService.procurarPorId(fornecedorId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Fornecedor não encontrado.")

        val endereco = EnderecoMapper.toModel(dto)
        val enderecoSalvo = enderecoService.salvarParaFornecedor(fornecedor, endereco)

        return EnderecoMapper.toDTO(enderecoSalvo)
    }

    @PutMapping("/{enderecoId}")
    fun atualizarEnderecoDoFornecedor(
        @PathVariable fornecedorId: Long,
        @PathVariable enderecoId: Long,
        @Valid @RequestBody dto: EnderecoRequestDTO
    ): EnderecoResponseDTO {
        val enderecoExistente = enderecoService.procurarPorId(enderecoId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado.")

        if (enderecoExistente.fornecedor?.id != fornecedorId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Este endereço não pertence ao fornecedor informado.")
        }

        val enderecoAtualizado = EnderecoMapper.toModel(dto)
        val enderecoSalvo = enderecoService.atualizar(enderecoId, enderecoAtualizado)

        return EnderecoMapper.toDTO(enderecoSalvo)
    }
    
    @DeleteMapping("/{enderecoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deletarEnderecoDoFornecedor(
        @PathVariable fornecedorId: Long,
        @PathVariable enderecoId: Long
    ) {
        val endereco = enderecoService.procurarPorId(enderecoId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado.")

        if (endereco.fornecedor?.id != fornecedorId) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Este endereço não pertence ao fornecedor informado.")
        }

        enderecoService.deletar(enderecoId)
    }
}