package br.com.enali.mapper

import br.com.enali.dto.AdministradorRequestDTO
import br.com.enali.dto.AdministradorResponseDTO
import br.com.enali.model.Administrador
import org.springframework.stereotype.Component

@Component
class AdministradorMapper {

    fun toModel(dto: AdministradorRequestDTO): Administrador {
        return Administrador(
            id = 0,
            nome = dto.nome,
            email = dto.email,
            senha = dto.senha,
            ativo = true,
            nivelAcesso = "ADMIN_COMUM"
        )
    }

    fun toDTO(administrador: Administrador): AdministradorResponseDTO {
        return AdministradorResponseDTO(
            id = administrador.id,
            nome = administrador.nome,
            email = administrador.email,
            ativo = administrador.ativo,
            nivelAcesso = administrador.nivelAcesso
        )
    }
}