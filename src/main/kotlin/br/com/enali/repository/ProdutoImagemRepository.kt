package br.com.enali.repository

import br.com.enali.model.ProdutoImagem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProdutoImagemRepository : JpaRepository<ProdutoImagem, Long> {

    fun findAllByProdutoIdOrderByOrdemAscIdAsc(produtoId: Long): List<ProdutoImagem>

    fun findByIdAndProdutoId(id: Long, produtoId: Long): ProdutoImagem?

    fun deleteAllByProdutoId(produtoId: Long)
}