package br.com.enali.service

import br.com.enali.dto.PedidoImagemResponse
import br.com.enali.model.PedidoImagem
import br.com.enali.repository.PedidoImagemRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@Service
class PedidoImagemService(
    private val imagemRepository: PedidoImagemRepository
) {
    companion object {
        private const val MAX_TAMANHO_BYTES = 5 * 1024 * 1024 // 5 MB
        private const val MAX_FOTOS_POR_PEDIDO = 5
        private val TIPOS_PERMITIDOS = setOf("image/jpeg", "image/png", "image/webp")
    }

    fun adicionar(pedidoId: Long, arquivo: MultipartFile): PedidoImagemResponse {
        if (arquivo.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo de imagem não pode estar vazio.")
        }
        if (arquivo.size > MAX_TAMANHO_BYTES) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A imagem excede o limite máximo de 5 MB.")
        }
        val tipo = arquivo.contentType?.lowercase()
        if (tipo == null || tipo !in TIPOS_PERMITIDOS) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de imagem não suportado. Use JPEG, PNG ou WebP.")
        }
        if (imagemRepository.countByPedidoId(pedidoId) >= MAX_FOTOS_POR_PEDIDO) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O pedido pode ter no máximo $MAX_FOTOS_POR_PEDIDO fotos.")
        }
        val salva = imagemRepository.save(PedidoImagem(pedidoId = pedidoId, dados = arquivo.bytes, tipoMime = tipo))
        return PedidoImagemResponse(salva.id, salva.pedidoId, salva.tipoMime)
    }

    fun listar(pedidoId: Long): List<PedidoImagemResponse> =
        imagemRepository.findAllByPedidoIdOrderByIdAsc(pedidoId).map { PedidoImagemResponse(it.id, it.pedidoId, it.tipoMime) }

    fun buscar(pedidoId: Long, imagemId: Long): PedidoImagem =
        imagemRepository.findByIdAndPedidoId(imagemId, pedidoId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Imagem não encontrada para este pedido.")
}
