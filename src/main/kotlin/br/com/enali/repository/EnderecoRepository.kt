package br.com.enali.repository

import br.com.enali.model.Endereco
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface EnderecoRepository : JpaRepository<Endereco, Long> {

    @Query(
        """
        SELECT e FROM Endereco e
        WHERE LOWER(e.rua) LIKE LOWER(CONCAT('%', :rua, '%'))
    """
    )
    fun buscarPorRua(@Param("rua") rua: String): List<Endereco>

    @Query(
        """
        SELECT e FROM Endereco e
        WHERE LOWER(e.bairro) LIKE LOWER(CONCAT('%', :bairro, '%'))
    """
    )
    fun buscarPorBairro(@Param("bairro") bairro: String): List<Endereco>

    @Query(
        """
        SELECT e FROM Endereco e
        WHERE LOWER(e.cidade) LIKE LOWER(CONCAT('%', :cidade, '%'))
    """
    )
    fun buscarPorCidade(@Param("cidade") cidade: String): List<Endereco>

    @Query(
        """
        SELECT e FROM Endereco e
        WHERE UPPER(e.siglaEstado) = UPPER(:sigla)
    """
    )
    fun buscarPorSiglaEstado(@Param("sigla") sigla: String): List<Endereco>
}