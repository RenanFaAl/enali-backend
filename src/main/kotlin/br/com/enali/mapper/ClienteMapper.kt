
package br.com.enali.mapper

import br.com.enali.dto.ClienteRequest
import br.com.enali.dto.ClienteResponse
import br.com.enali.model.Cliente
import org.springframework.stereotype.Component

@Component
class ClienteMapper {

    fun toEntity(request: ClienteRequest): Cliente {
        return Cliente(
            nome = request.nome,
            email = request.email,
            senha = request.senha,
            cpf = request.cpf,
            dataNascimento = request.dataNascimento,
            ativo = request.ativo
        )
    }

    fun toResponse(cliente: Cliente): ClienteResponse {
        return ClienteResponse(
            id = cliente.id,
            nome = cliente.nome,
            email = cliente.email,
            cpf = cliente.cpf,
            dataNascimento = cliente.dataNascimento,
            ativo = cliente.ativo
        )
    }
}
