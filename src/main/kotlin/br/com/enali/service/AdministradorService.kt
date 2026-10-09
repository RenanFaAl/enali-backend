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

    fun buscarPorNome(nome: String): List<Administrador> {
        return administradorRepository.buscarPorNome(nome)
    }

    fun salvar(administrador: Administrador): Administrador {
        val existente = administradorRepository.findByEmail(administrador.email)
        if (existente != null) {
            throw IllegalArgumentException("Já existe um administrador com este e-mail.")
        }
        return administradorRepository.save(administrador)
    }


    fun atualizar(
        id: Long,
        administradorAtualizado: Administrador
    ): Administrador {
        val existente = administradorRepository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Administrador não encontrado.")

        val adminComMesmoEmail =
            administradorRepository.findByEmail(administradorAtualizado.email)

        if (adminComMesmoEmail != null && adminComMesmoEmail.id != id) {
            throw IllegalArgumentException(
                "Já existe outro administrador com este e-mail."
            )
        }

        val adminParaSalvar = administradorAtualizado.copy(id = existente.id)

        return administradorRepository.save(adminParaSalvar)
    }


    fun deletar(id: Long) {
        administradorRepository.deleteById(id)
    }

    fun existe(id: Long): Boolean {
        return administradorRepository.existsById(id)
    }
}