package br.com.enali.service

import br.com.enali.dto.ProdutoImagemResponseDTO
import br.com.enali.dto.ProdutoRequestDTO
import br.com.enali.dto.ProdutoResponseDTO
import br.com.enali.mapper.ProdutoMapper
import br.com.enali.model.Produto
import br.com.enali.model.ProdutoImagem
import br.com.enali.repository.EstoqueRepository
import br.com.enali.repository.ProdutoImagemRepository
import br.com.enali.repository.ProdutoRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException

@Service
class ProdutoService(
    private val repository: ProdutoRepository,
    private val imagemRepository: ProdutoImagemRepository,
    private val estoqueRepository: EstoqueRepository,
    private val mapper: ProdutoMapper
) {

    companion object {
        private const val MAX_TAMANHO_BYTES = 5 * 1024 * 1024 // 5 MB
        private val TIPOS_PERMITIDOS = setOf("image/jpeg", "image/png", "image/webp")
    }

    @Transactional
    fun criar(request: ProdutoRequestDTO): ProdutoResponseDTO {
        val produto = repository.save(mapper.toEntity(request))
        return mapper.toResponse(produto, emptyList())
    }
    
    @Transactional
    fun criarComImagens(
        request: ProdutoRequestDTO,
        imagens: List<MultipartFile>?
    ): ProdutoResponseDTO {
        val produtoSalvo = repository.save(mapper.toEntity(request))

        val imagensValidas = imagens?.filter { !it.isEmpty } ?: emptyList()
        if (imagensValidas.isNotEmpty()) {
            imagensValidas.forEachIndexed { index, arquivo ->
                validarArquivoImagem(arquivo)
                imagemRepository.save(
                    ProdutoImagem(
                        produto = produtoSalvo,
                        dados = arquivo.bytes,
                        tipoMime = arquivo.contentType!!.lowercase(),
                        ordem = index
                    )
                )
            }
        }

        return mapper.toResponse(produtoSalvo, buscarUrlsDasImagens(produtoSalvo.id))
    }
    
    private fun validarArquivoImagem(arquivo: MultipartFile) {
        if (arquivo.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo de imagem não pode estar vazio.")
        }

        if (arquivo.size > MAX_TAMANHO_BYTES) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A imagem excede o limite máximo de 5 MB.")
        }

        val contentType = arquivo.contentType?.lowercase()
        if (contentType == null || contentType !in TIPOS_PERMITIDOS) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Formato de imagem não suportado. Use JPEG, PNG ou WebP."
            )
        }
    }

    fun listarTodos(): List<ProdutoResponseDTO> {
        return repository.findAll().map { produto ->
            val urls = buscarUrlsDasImagens(produto.id)
            mapper.toResponse(produto, urls)
        }
    }

    fun buscarPorId(id: Long): ProdutoResponseDTO {
        val produto = buscarProdutoOuFalhar(id)
        val urls = buscarUrlsDasImagens(produto.id)
        return mapper.toResponse(produto, urls)
    }

    fun buscarPorNome(nome: String): List<ProdutoResponseDTO> {
        return repository.buscarPorNome(nome.trim()).map { produto ->
            val urls = buscarUrlsDasImagens(produto.id)
            mapper.toResponse(produto, urls)
        }
    }

    @Transactional
    fun atualizar(id: Long, request: ProdutoRequestDTO): ProdutoResponseDTO {
        val existente = buscarProdutoOuFalhar(id)

        val atualizado = existente.copy(
            nome = request.nome.trim(),
            descricao = request.descricao.trim(),
            preco = request.preco,
            disponivel = request.disponivel,
            desconto = request.desconto
        )

        val salvo = repository.save(atualizado)
        return mapper.toResponse(salvo, buscarUrlsDasImagens(salvo.id))
    }

    @Transactional
    fun remover(id: Long) {
        if (!repository.existsById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado.")
        }
        imagemRepository.deleteAllByProdutoId(id)
        estoqueRepository.deleteAllByProdutoId(id)
        repository.deleteById(id)
    }

    @Transactional
    fun adicionarImagem(
        produtoId: Long,
        arquivo: MultipartFile,
        ordem: Int? = null
    ): ProdutoImagemResponseDTO {
        val produto = buscarProdutoOuFalhar(produtoId)

        if (arquivo.isEmpty) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo de imagem não pode estar vazio.")
        }

        if (arquivo.size > MAX_TAMANHO_BYTES) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "A imagem excede o limite máximo de 5 MB.")
        }

        val contentType = arquivo.contentType?.lowercase()
        if (contentType == null || contentType !in TIPOS_PERMITIDOS) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Formato de imagem não suportado. Use JPEG, PNG ou WebP."
            )
        }

        val imagensExistentes = imagemRepository.findAllByProdutoIdOrderByOrdemAscIdAsc(produtoId)
        val ordemDefinida = ordem ?: ((imagensExistentes.maxOfOrNull { it.ordem } ?: -1) + 1)

        val imagemSalva = imagemRepository.save(
            ProdutoImagem(
                produto = produto,
                dados = arquivo.bytes,
                tipoMime = contentType,
                ordem = ordemDefinida
            )
        )

        return toImagemResponse(imagemSalva)
    }

    fun listarImagens(produtoId: Long): List<ProdutoImagemResponseDTO> {
        buscarProdutoOuFalhar(produtoId)
        return imagemRepository.findAllByProdutoIdOrderByOrdemAscIdAsc(produtoId)
            .map { toImagemResponse(it) }
    }

    fun buscarImagemBinaria(produtoId: Long, imagemId: Long): ProdutoImagem {
        buscarProdutoOuFalhar(produtoId)
        return imagemRepository.findByIdAndProdutoId(imagemId, produtoId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Imagem não encontrada para este produto.")
    }

    @Transactional
    fun removerImagem(produtoId: Long, imagemId: Long) {
        buscarProdutoOuFalhar(produtoId)
        val imagem = imagemRepository.findByIdAndProdutoId(imagemId, produtoId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Imagem não encontrada para este produto.")
        imagemRepository.delete(imagem)
    }

    private fun buscarProdutoOuFalhar(id: Long): Produto {
        return repository.findById(id).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado.")
        }
    }

    private fun buscarUrlsDasImagens(produtoId: Long): List<String> {
        return imagemRepository.findAllByProdutoIdOrderByOrdemAscIdAsc(produtoId)
            .map { "/produtos/$produtoId/imagens/${it.id}" }
    }

    private fun toImagemResponse(imagem: ProdutoImagem): ProdutoImagemResponseDTO {
        val produtoId = imagem.produto.id
        return ProdutoImagemResponseDTO(
            id = imagem.id,
            produtoId = produtoId,
            url = "/produtos/$produtoId/imagens/${imagem.id}",
            tipoMime = imagem.tipoMime,
            ordem = imagem.ordem,
            tamanhoBytes = imagem.dados.size
        )
    }
}