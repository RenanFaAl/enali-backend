package br.com.enali.mapper

import br.com.enali.dto.MaterialRequestDTO
import br.com.enali.dto.MaterialResponseDTO
import br.com.enali.model.Material
import org.springframework.stereotype.Component

@Component
class MaterialMapper {

    fun toModel(dto: MaterialRequestDTO): Material {
        return Material(
            nome = dto.nome.trim(),
            descricao = dto.descricao?.trim()?.takeIf { it.isNotEmpty() },
            quantidade = requireNotNull(dto.quantidade),
            custo = requireNotNull(dto.custo),
            disponivel = dto.disponivel
        )
    }

    fun toDTO(material: Material): MaterialResponseDTO {
        return MaterialResponseDTO(
            id = material.id,
            nome = material.nome,
            descricao = material.descricao,
            quantidade = material.quantidade,
            custo = material.custo,
            disponivel = material.disponivel
        )
    }
}
