package br.com.enali.repository

import br.com.enali.model.Administrador
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface AdministradorRepository : JpaRepository<Administrador, Long> {
    fun findByEmail(email: String): Administrador?
}