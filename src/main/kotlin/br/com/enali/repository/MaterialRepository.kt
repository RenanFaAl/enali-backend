package br.com.enali.repository

import br.com.enali.model.Material
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MaterialRepository : JpaRepository<Material, Long> {

    fun findByDisponivel(disponivel: Boolean): List<Material>

    @Query("SELECT m FROM Material m WHERE LOWER(m.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    fun buscarPorNome(@Param("nome") nome: String): List<Material>
}
