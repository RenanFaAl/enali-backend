package br.com.enali.mapper

import br.com.enali.dto.EnderecoRequestDTO
import br.com.enali.dto.EnderecoResponseDTO
import br.com.enali.model.Endereco

object EnderecoMapper {

    fun toModel(dto: EnderecoRequestDTO): Endereco {
        return Endereco(
            rua = dto.rua.trim(),
            numero = dto.numero.trim(),
            complemento = dto.complemento?.trim()?.takeIf { it.isNotEmpty() },
            bairro = dto.bairro.trim(),
            cidade = dto.cidade.trim(),
            estado = dto.estado.trim(),
            siglaEstado = dto.siglaEstado.trim().uppercase(),
            cep = dto.cep.trim(),
            pais = dto.pais.trim(),
            referencia = dto.referencia?.trim()?.takeIf { it.isNotEmpty() },
            principal = dto.principal
        )
    }

    fun toDTO(endereco: Endereco): EnderecoResponseDTO {
        return EnderecoResponseDTO(
            id = endereco.id,
            rua = endereco.rua,
            numero = endereco.numero,
            complemento = endereco.complemento,
            bairro = endereco.bairro,
            cidade = endereco.cidade,
            estado = endereco.estado,
            siglaEstado = endereco.siglaEstado,
            cep = endereco.cep,
            pais = endereco.pais,
            referencia = endereco.referencia,
            principal = endereco.principal,
            ativo = endereco.ativo,
            dataCadastro = endereco.dataCadastro
        )
    }
}
