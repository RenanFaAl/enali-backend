package br.com.enali.service

import br.com.enali.model.Administrador
import br.com.enali.repository.AdministradorRepository
import org.springframework.stereotype.Service

@Service
class AdministradorService(
    private val administradorRepository: AdministradorRepository
) {

    fun listarTodos(): List<Administrador> {
        return administradorRepository.findAll()
    }

    fun procurarPorId(id: Long): Administrador? {
        return administradorRepository.findById(id).orElse(null)
    }

    fun procurarPorEmail(email: String): Administrador? {
        return administradorRepository.findByEmail(email)
    }

    fun salvar(administrador: Administrador): Administrador {
        val existente = administradorRepository.findByEmail(administrador.email)
        if (existente != null) {
            throw IllegalArgumentException("Já existe um administrador com este e-mail.")
        }
        return administradorRepository.save(administrador)
    }

    fun atualizar(id: Long, administradorAtualizado: Administrador): Administrador {
        if (!administradorRepository.existsById(id)) {
            throw IllegalArgumentException("Administrador não encontrado.")
        }
        val adminParaSalvar = administradorAtualizado.copy(id = id)
        return administradorRepository.save(adminParaSalvar)
    }

    fun deletar(id: Long) {
        administradorRepository.deleteById(id)
    }

    fun existe(id: Long): Boolean {
        return administradorRepository.existsById(id)
    }
}