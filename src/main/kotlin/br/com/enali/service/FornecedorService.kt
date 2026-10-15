package br.com.enali.service

import br.com.enali.model.Fornecedor
import br.com.enali.repository.FornecedorRepository
import org.springframework.stereotype.Service

@Service
class FornecedorService(
    private val repository: FornecedorRepository
) {

    fun listarTodos(): List<Fornecedor> {
        return repository.findAll()
    }

    fun procurarPorId(id: Long): Fornecedor? {
        return repository.findById(id).orElse(null)
    }

    fun procurarPorCnpj(cnpj: String): Fornecedor? {
        return repository.findByCnpj(cnpj)
    }

    fun buscarPorNome(nome: String): List<Fornecedor> {
        return repository.buscarPorNome(nome)
    }

    fun salvar(fornecedor: Fornecedor): Fornecedor {
        if (repository.findByCnpj(fornecedor.cnpj) != null) {
            throw IllegalArgumentException("Já existe um fornecedor com este CNPJ.")
        }

        return repository.save(fornecedor)
    }

    fun atualizar(id: Long, fornecedor: Fornecedor): Fornecedor {
        val existente = repository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Fornecedor não encontrado.")

        val fornecedorComMesmoCnpj = repository.findByCnpj(fornecedor.cnpj)

        if (fornecedorComMesmoCnpj != null && fornecedorComMesmoCnpj.id != id) {
            throw IllegalArgumentException("Já existe outro fornecedor com este CNPJ.")
        }

        val fornecedorParaSalvar = fornecedor.copy(
            id = existente.id,
            ativo = existente.ativo
        )

        return repository.save(fornecedorParaSalvar)
    }

    fun alterarAtivo(id: Long, ativo: Boolean): Fornecedor {
        val existente = repository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Fornecedor não encontrado.")

        return repository.save(existente.copy(ativo = ativo))
    }

    fun deletar(id: Long) {
        repository.deleteById(id)
    }

    fun existe(id: Long): Boolean {
        return repository.existsById(id)
    }
}
