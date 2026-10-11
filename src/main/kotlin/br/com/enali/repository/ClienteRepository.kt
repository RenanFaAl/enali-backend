package br.com.enali.repository

import br.com.enali.model.Cliente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ClienteRepository : JpaRepository<Cliente, Long> {

    fun findByEmail(email: String): Cliente?

    fun findByCpf(cpf: String): Cliente?
}