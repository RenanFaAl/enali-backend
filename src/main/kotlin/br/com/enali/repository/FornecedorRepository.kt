package br.com.enali.repository

import br.com.enali.model.Fornecedor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FornecedorRepository : JpaRepository<Fornecedor, Long> {

    fun findByCnpj(cnpj: String): Fornecedor?

    @Query("SELECT f FROM Fornecedor f WHERE LOWER(f.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    fun buscarPorNome(@Param("nome") nome: String): List<Fornecedor>
}
