package com.example.ecommerce.controller

import com.example.ecommerce.dto.ClienteRequest
import com.example.ecommerce.dto.ClienteResponse
import com.example.ecommerce.model.Cliente
import com.example.ecommerce.repository.ClienteRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clientes")
class ClienteController(
    private val clienteRepository: ClienteRepository
) {

    @PostMapping
    fun criar(@RequestBody request: ClienteRequest): ResponseEntity<ClienteResponse> {

        val cliente = Cliente(
            nome = request.nome,
            email = request.email,
            senha = request.senha,
            cpf = request.cpf,
            dataNascimento = request.dataNascimento
        )

        val clienteSalvo = clienteRepository.save(cliente)

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(clienteSalvo.toResponse())
    }

    @GetMapping
    fun listar(): ResponseEntity<List<ClienteResponse>> {

        val clientes = clienteRepository.findAll()
            .map { it.toResponse() }

        return ResponseEntity.ok(clientes)
    }

    @GetMapping("/{id}")
    fun buscarPorId(@PathVariable id: Long): ResponseEntity<ClienteResponse> {

        val cliente = clienteRepository.findById(id)

        if (cliente.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        return ResponseEntity.ok(cliente.get().toResponse())
    }

    @DeleteMapping("/{id}")
    fun excluir(@PathVariable id: Long): ResponseEntity<Void> {

        if (!clienteRepository.existsById(id)) {
            return ResponseEntity.notFound().build()
        }

        clienteRepository.deleteById(id)

        return ResponseEntity.noContent().build()
    }

    private fun Cliente.toResponse(): ClienteResponse {
        return ClienteResponse(
            id = id,
            nome = nome,
            email = email,
            cpf = cpf,
            dataNascimento = dataNascimento,
            ativo = ativo
        )
    }
}