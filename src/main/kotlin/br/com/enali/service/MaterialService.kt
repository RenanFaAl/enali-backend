package br.com.enali.service

import br.com.enali.model.Material
import br.com.enali.repository.MaterialRepository
import org.springframework.stereotype.Service

@Service
class MaterialService(
    private val repository: MaterialRepository
) {

    fun listarTodos(): List<Material> {
        return repository.findAll()
    }

    fun listarDisponiveis(): List<Material> {
        return repository.findByDisponivel(true)
    }

    fun procurarPorId(id: Long): Material? {
        return repository.findById(id).orElse(null)
    }

    fun buscarPorNome(nome: String): List<Material> {
        return repository.buscarPorNome(nome)
    }

    fun salvar(material: Material): Material {
        return repository.save(material)
    }

    fun atualizar(id: Long, material: Material): Material {
        val existente = repository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Material não encontrado.")

        return repository.save(material.copy(id = existente.id))
    }

    fun alterarDisponivel(id: Long, disponivel: Boolean): Material {
        val existente = repository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Material não encontrado.")

        return repository.save(existente.copy(disponivel = disponivel))
    }

    fun deletar(id: Long) {
        repository.deleteById(id)
    }

    fun existe(id: Long): Boolean {
        return repository.existsById(id)
    }
}
