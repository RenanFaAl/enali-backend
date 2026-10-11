package br.com.enali.service

import br.com.enali.model.Administrador
import br.com.enali.repository.AdministradorRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AdministradorService(
    private val repository: AdministradorRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun listarTodos(): List<Administrador> {
        return repository.findAll()
    }

    fun procurarPorId(id: Long): Administrador? {
        return repository.findById(id).orElse(null)
    }

    fun procurarPorEmail(email: String): Administrador? {
        return repository.findByEmail(email)
    }

    fun buscarPorNome(nome: String): List<Administrador> {
        return repository.buscarPorNome(nome)
    }

    fun salvar(administrador: Administrador): Administrador {
        val email = administrador.email.trim().lowercase()

        val existente = repository.findByEmail(email)

        if (existente != null) {
            throw IllegalArgumentException("Já existe um administrador com este e-mail.")
        }

        val administradorProtegido = administrador.copy(
            email = email,
            senha = requireNotNull(passwordEncoder.encode(administrador.senha))
        )

        return repository.save(administradorProtegido)
    }

    fun atualizar(
        id: Long,
        administrador: Administrador
    ): Administrador {
        val existente = repository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Administrador não encontrado.")

        val email = administrador.email.trim().lowercase()
        val adminComMesmoEmail = repository.findByEmail(email)

        if (adminComMesmoEmail != null && adminComMesmoEmail.id != id) {
            throw IllegalArgumentException(
                "Já existe outro administrador com este e-mail."
            )
        }

        val adminParaSalvar = administrador.copy(
            id = existente.id,
            email = email,
            senha = existente.senha,
            ativo = existente.ativo,
            nivelAcesso = existente.nivelAcesso,
            dataCriacao = existente.dataCriacao
        )

        return repository.save(adminParaSalvar)
    }

    fun alterarSenha(
        id: Long,
        senhaAtual: String,
        senhaNova: String
    ) {
        val administrador = repository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Administrador não encontrado.")

        if (!passwordEncoder.matches(senhaAtual, administrador.senha)) {
            throw IllegalArgumentException("A senha atual está incorreta.")
        }

        val novaSenhaHash = requireNotNull(
            passwordEncoder.encode(senhaNova)
        )

        repository.save(
            administrador.copy(senha = novaSenhaHash)
        )
    }

    fun deletar(id: Long) {
        repository.deleteById(id)
    }

    fun existe(id: Long): Boolean {
        return repository.existsById(id)
    }
}
