package br.com.enali.repository

import br.com.enali.Fixtures
import br.com.enali.dto.ItemPedidoRequest
import br.com.enali.dto.PedidoRequest
import br.com.enali.mapper.PedidoMapper
import br.com.enali.model.Cliente
import br.com.enali.model.Endereco
import br.com.enali.model.Produto
import br.com.enali.service.PedidoService
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest
import org.springframework.context.annotation.Import

/**
 * Teste de integração de classes: PedidoService + PedidoMapper + repositórios REAIS sobre H2 em memória.
 * Sem mocks: verifica o mapeamento JPA das entidades do UC05 e as consultas novas dos repositórios.
 * Cada teste roda em uma transação desfeita ao final (@DataJpaTest).
 */
@DataJpaTest(properties = ["spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"])
@Import(PedidoService::class, PedidoMapper::class)
class PedidoPersistenciaTest @Autowired constructor(
    private val service: PedidoService,
    private val clienteRepository: ClienteRepository,
    private val enderecoRepository: EnderecoRepository,
    private val produtoRepository: ProdutoRepository,
    private val estoqueRepository: EstoqueRepository,
    private val pedidoRepository: PedidoRepository,
    private val entityManager: EntityManager
) {

    private lateinit var cliente: Cliente
    private lateinit var endereco: Endereco
    private lateinit var vaso: Produto

    @BeforeEach
    fun gravarDados() {
        cliente = clienteRepository.save(Fixtures.cliente(id = 0))
        endereco = enderecoRepository.save(Fixtures.endereco(id = 0, cliente = cliente))
        vaso = produtoRepository.save(Fixtures.produto(id = 0))
        estoqueRepository.save(Fixtures.estoque(vaso, disponivel = 10))
    }

    // Força ida ao banco: grava o que está pendente e esvazia o cache do Hibernate
    private fun limparCache() {
        entityManager.flush()
        entityManager.clear()
    }

    private fun realizarPedido(quantidade: Int) =
        service.criar(cliente.id, PedidoRequest(endereco.id, listOf(ItemPedidoRequest(vaso.id, quantidade))))

    @Test
    fun `RN27 - pedido e gravado com cliente, itens e valores, e o estoque fica reservado`() {
        val resposta = realizarPedido(3)
        limparCache()

        val pedido = pedidoRepository.findById(resposta.id).get()
        assertEquals(cliente.id, pedido.cliente?.id)
        assertEquals(1, pedido.itens.size)
        assertEquals(3, pedido.itens[0].quantidade)
        assertEquals(150.00, pedido.valorTotal)

        val estoque = estoqueRepository.findFirstByProdutoIdOrderByIdAsc(vaso.id)!!
        assertEquals(7, estoque.quantidadeDisponivel)
        assertEquals(3, estoque.quantidadeReservada)
    }

    @Test
    fun `RN28 - alterar o endereco do cliente nao altera o endereco gravado no pedido`() {
        val resposta = realizarPedido(1)
        enderecoRepository.save(endereco.copy(rua = "Rua Nova"))
        limparCache()

        assertEquals("Rua Nova", enderecoRepository.findById(endereco.id).get().rua)
        assertEquals("Rua das Flores", pedidoRepository.findById(resposta.id).get().ruaEntrega)
    }

    @Test
    fun `cancelar grava CANCELADO e devolve a reserva ao estoque`() {
        val resposta = realizarPedido(3)
        limparCache()

        service.cancelar(resposta.id)
        limparCache()

        val pedido = pedidoRepository.findById(resposta.id).get()
        assertEquals("CANCELADO", pedido.status)
        assertEquals(1, pedido.itens.size)
        val estoque = estoqueRepository.findFirstByProdutoIdOrderByIdAsc(vaso.id)!!
        assertEquals(10, estoque.quantidadeDisponivel)
        assertEquals(0, estoque.quantidadeReservada)
    }

    @Test
    fun `RN18 - endereco so e encontrado para o proprio cliente`() {
        val outro = clienteRepository.save(Fixtures.cliente(id = 0).copy(email = "outro@enali.com", cpf = "99999999999"))

        assertNotNull(enderecoRepository.findByIdAndClienteIdAndAtivoTrue(endereco.id, cliente.id))
        assertNull(enderecoRepository.findByIdAndClienteIdAndAtivoTrue(endereco.id, outro.id))
    }

    @Test
    fun `RN17 - cliente inativo nao e encontrado para realizar pedido`() {
        val inativo = clienteRepository.save(Fixtures.cliente(id = 0, ativo = false).copy(email = "inativo@enali.com", cpf = "88888888888"))

        assertNull(clienteRepository.findByIdAndAtivoTrue(inativo.id))
        assertNotNull(clienteRepository.findByIdAndAtivoTrue(cliente.id))
    }
}
