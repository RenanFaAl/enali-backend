package br.com.enali.mapper

import br.com.enali.Fixtures
import br.com.enali.model.ItemPedido
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Testes de unidade do PedidoMapper. O mapper é puro (sem dependências), então é usado real, sem mocks.
 */
class PedidoMapperTest {

    private val mapper = PedidoMapper()

    @Test
    fun `toEntity cria pedido PENDENTE com cliente, itens e valor total calculado`() {
        val cliente = Fixtures.cliente()
        val itens = listOf(ItemPedido.de(Fixtures.produto(preco = "50.00"), 2))

        val pedido = mapper.toEntity(Fixtures.pedidoRequest(100L to 2), "2026111200001", cliente, Fixtures.endereco(), itens)

        assertEquals("2026111200001", pedido.numeroPedido)
        assertEquals("PENDENTE", pedido.status)
        assertEquals(cliente, pedido.cliente)
        assertEquals(itens, pedido.itens)
        assertEquals(100.00, pedido.valorTotal)
        assertEquals("Embrulhar para presente", pedido.observacoes)
    }

    @Test
    fun `RN27 e RN28 - endereco de entrega e copiado campo a campo para o pedido`() {
        // A alteração posterior do endereço no banco é verificada em PedidoPersistenciaTest
        val pedido = mapper.toEntity(Fixtures.pedidoRequest(100L to 1), "1", Fixtures.cliente(), Fixtures.endereco(), emptyList())

        assertEquals("Rua das Flores", pedido.ruaEntrega)
        assertEquals("100", pedido.numeroEntrega)
        assertEquals("Centro", pedido.bairroEntrega)
        assertEquals("São Paulo", pedido.cidadeEntrega)
        assertEquals("SP", pedido.estadoEntrega)
        assertEquals("01001-000", pedido.cepEntrega)
    }

    @Test
    fun `toResponse expoe itens, cliente e endereco de entrega`() {
        val itens = listOf(ItemPedido.de(Fixtures.produto(id = 100, nome = "Cesta de Palha", preco = "30.00"), 2))
        val pedido = mapper.toEntity(Fixtures.pedidoRequest(100L to 2), "2026111200001", Fixtures.cliente(id = 7), Fixtures.endereco(), itens)

        val resposta = mapper.toResponse(pedido)

        assertEquals(7L, resposta.clienteId)
        assertEquals(60.00, resposta.valorTotal)
        assertEquals(1, resposta.itens.size)
        assertEquals(100L, resposta.itens[0].produtoId)
        assertEquals("Cesta de Palha", resposta.itens[0].produtoNome)
        assertEquals(2, resposta.itens[0].quantidade)
        assertEquals(30.00, resposta.itens[0].precoUnitario)
        assertEquals("01001-000", resposta.cepEntrega)
    }
}
