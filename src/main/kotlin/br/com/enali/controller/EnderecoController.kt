package br.com.enali.controller

import br.com.enali.dto.EnderecoResponseDTO
import br.com.enali.mapper.EnderecoMapper
import br.com.enali.service.EnderecoService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/enderecos")
class EnderecoController(
    private val enderecoService: EnderecoService
) {

    @GetMapping
    fun listarTodos(): List<EnderecoResponseDTO> {
        return enderecoService.listarTodos().map {
            EnderecoMapper.toDTO(it)
        }
    }

    @GetMapping("/{id}")
    fun procurarPorId(@PathVariable id: Long): EnderecoResponseDTO {
        val endereco = enderecoService.procurarPorId(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Endereço não encontrado.")

        return EnderecoMapper.toDTO(endereco)
    }

    @GetMapping("/buscar/rua")
    fun buscarPorRua(@RequestParam rua: String): List<EnderecoResponseDTO> {
        return enderecoService.buscarPorRua(rua).map {
            EnderecoMapper.toDTO(it)
        }
    }

    @GetMapping("/buscar/bairro")
    fun buscarPorBairro(@RequestParam bairro: String): List<EnderecoResponseDTO> {
        return enderecoService.buscarPorBairro(bairro).map {
            EnderecoMapper.toDTO(it)
        }
    }

    @GetMapping("/buscar/cidade")
    fun buscarPorCidade(@RequestParam cidade: String): List<EnderecoResponseDTO> {
        return enderecoService.buscarPorCidade(cidade).map {
            EnderecoMapper.toDTO(it)
        }
    }

    @GetMapping("/buscar/estado")
    fun buscarPorSiglaEstado(@RequestParam sigla: String): List<EnderecoResponseDTO> {
        return enderecoService.buscarPorSiglaEstado(sigla).map {
            EnderecoMapper.toDTO(it)
        }
    }
}