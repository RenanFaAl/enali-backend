
package br.com.enali.mapper

import br.com.enali.dto.EstoqueRequestDTO
import br.com.enali.dto.EstoqueResponseDTO
import br.com.enali.model.Estoque
import br.com.enali.model.Produto
import org.springframework.stereotype.Component

@Component
class EstoqueMapper {

    fun toEntity(
        request: EstoqueRequestDTO,
        produto: Produto
    ): Estoque {
        return Estoque(
            produto = produto,
            quantidadeDisponivel = request.quantidadeDisponivel,
            quantidadeReservada = request.quantidadeReservada,
            estoqueMinimo = request.estoqueMinimo,
            localizacao = request.localizacao.trim(),
            ativo = request.ativo
        )
    }

    fun toResponse(estoque: Estoque): EstoqueResponseDTO {
        return EstoqueResponseDTO(
            id = estoque.id,
            produtoId = estoque.produto.id,
            produtoNome = estoque.produto.nome,
            quantidadeDisponivel = estoque.quantidadeDisponivel,
            quantidadeReservada = estoque.quantidadeReservada,
            estoqueMinimo = estoque.estoqueMinimo,
            localizacao = estoque.localizacao,
            ativo = estoque.ativo,
            dataAtualizacao = estoque.dataAtualizacao
        )
    }
}
