package br.com.enali.service

import br.com.enali.model.Endereco
import br.com.enali.repository.EnderecoRepository
import org.springframework.stereotype.Service

@Service
class EnderecoService(
    private val enderecoRepository: EnderecoRepository
) {

    fun listarTodos(): List<Endereco> {
        return enderecoRepository.findAll()
    }

    fun procurarPorId(id: Long): Endereco? {
        return enderecoRepository.findById(id).orElse(null)
    }

    fun buscarPorRua(rua: String): List<Endereco> {
        return enderecoRepository.buscarPorRua(rua)
    }

    fun buscarPorBairro(bairro: String): List<Endereco> {
        return enderecoRepository.buscarPorBairro(bairro)
    }

    fun buscarPorCidade(cidade: String): List<Endereco> {
        return enderecoRepository.buscarPorCidade(cidade)
    }

    fun buscarPorSiglaEstado(estado: String): List<Endereco> {
        return enderecoRepository.buscarPorSiglaEstado(estado)
    }

    fun salvar(endereco: Endereco): Endereco {
        return enderecoRepository.save(endereco)
    }


    fun atualizar(id: Long, enderecoAtualizado: Endereco): Endereco {
        val enderecoExistente = enderecoRepository.findById(id).orElse(null)
            ?: throw IllegalArgumentException("Endereço não encontrado.")

        val enderecoParaSalvar = enderecoAtualizado.copy(
            id = enderecoExistente.id,
            ativo = enderecoExistente.ativo,
            dataCadastro = enderecoExistente.dataCadastro
        )

        return enderecoRepository.save(enderecoParaSalvar)
    }


    fun deletar(id: Long) {
        enderecoRepository.deleteById(id)
    }

    fun existe(id: Long): Boolean {
        return enderecoRepository.existsById(id)
    }
}