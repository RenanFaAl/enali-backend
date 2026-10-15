
package br.com.enali.mapper

import br.com.enali.dto.ProdutoRequestDTO
import br.com.enali.dto.ProdutoResponseDTO
import br.com.enali.model.Produto
import org.springframework.stereotype.Component

@Component
class ProdutoMapper {

    fun toEntity(request: ProdutoRequestDTO): Produto {
        return Produto(
            nome = request.nome.trim(), 
            descricao = request.descricao.trim(), 
            preco = request.preco,
            disponivel = request.disponivel,   
            desconto = request.desconto

        )
    }

    fun toResponse(produto: Produto): ProdutoResponseDTO {
         
        return ProdutoResponseDTO( 
            id = produto.id, 
            nome = produto.nome, 
            descricao = produto.descricao,
            preco = produto.preco,
            disponivel = produto.disponivel,
            desconto = produto.desconto

        )

    }       
}
