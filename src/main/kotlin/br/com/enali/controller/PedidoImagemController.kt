package br.com.enali.controller

import br.com.enali.dto.PedidoImagemResponse
import br.com.enali.service.ClienteService
import br.com.enali.service.PedidoImagemService
import br.com.enali.service.PedidoService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

/** Fotos das observações do pedido. As rotas seguem as regras de segurança já existentes de /pedidos. */
@RestController
@RequestMapping("/pedidos")
class PedidoImagemController(
    private val imagemService: PedidoImagemService,
    private val pedidoService: PedidoService,
    private val clienteService: ClienteService
) {

    // Garante que o pedido é do cliente logado (lança 404 se não for)
    private fun validarDono(authentication: Authentication, pedidoId: Long) {
        val cliente = clienteService.buscarMeusDados(authentication.name)
        pedidoService.buscarPorIdECliente(pedidoId, cliente.id)
    }

    @PostMapping("/clientes/me/pedidos/{id}/imagens", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun adicionar(
        authentication: Authentication,
        @PathVariable id: Long,
        @RequestParam("arquivo") arquivo: MultipartFile
    ): ResponseEntity<PedidoImagemResponse> {
        validarDono(authentication, id)
        return ResponseEntity.status(HttpStatus.CREATED).body(imagemService.adicionar(id, arquivo))
    }

    @GetMapping("/clientes/me/pedidos/{id}/imagens")
    fun listarMinhas(authentication: Authentication, @PathVariable id: Long): List<PedidoImagemResponse> {
        validarDono(authentication, id)
        return imagemService.listar(id)
    }

    @GetMapping("/clientes/me/pedidos/{id}/imagens/{imagemId}")
    fun buscarMinha(
        authentication: Authentication,
        @PathVariable id: Long,
        @PathVariable imagemId: Long
    ): ResponseEntity<ByteArray> {
        validarDono(authentication, id)
        return bytes(id, imagemId)
    }

    // Administrador
    @GetMapping("/{id}/imagens")
    fun listar(@PathVariable id: Long): List<PedidoImagemResponse> = imagemService.listar(id)

    @GetMapping("/{id}/imagens/{imagemId}")
    fun buscar(@PathVariable id: Long, @PathVariable imagemId: Long): ResponseEntity<ByteArray> = bytes(id, imagemId)

    private fun bytes(pedidoId: Long, imagemId: Long): ResponseEntity<ByteArray> {
        val imagem = imagemService.buscar(pedidoId, imagemId)
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(imagem.tipoMime)).body(imagem.dados)
    }
}
