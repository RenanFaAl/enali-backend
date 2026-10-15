package br.com.enali.mapper

import br.com.enali.dto.FornecedorRequestDTO
import br.com.enali.dto.FornecedorResponseDTO
import br.com.enali.model.Fornecedor
import org.springframework.stereotype.Component

@Component
class FornecedorMapper {

    fun toModel(dto: FornecedorRequestDTO): Fornecedor {
        return Fornecedor(
            nome = dto.nome.trim(),
            cnpj = somenteDigitos(dto.cnpj),
            email = dto.email.trim().lowercase(),
            telefone = somenteDigitos(dto.telefone),
            ativo = true
        )
    }

    fun toDTO(fornecedor: Fornecedor): FornecedorResponseDTO {
        return FornecedorResponseDTO(
            id = fornecedor.id,
            nome = fornecedor.nome,
            cnpj = fornecedor.cnpj,
            email = fornecedor.email,
            telefone = fornecedor.telefone,
            ativo = fornecedor.ativo
        )
    }

    fun somenteDigitos(valor: String): String {
        return valor.filter { it.isDigit() }
    }
}
