package br.com.enali.model

import br.com.enali.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

/**
 * Testes de unidade da entidade ItemPedido (camada de domínio).
 * Regra: RN27 (o pedido registra produto, quantidade e valor praticado na compra).
 */
class ItemPedidoTest {

    @Test
    fun `RN27 - item guarda produto, quantidade e preco sem desconto`() {
        val produto = Fixtures.produto(preco = "50.00")

        val item = ItemPedido.de(produto, 2)

        assertEquals(produto, item.produto)
        assertEquals(2, item.quantidade)
        assertEquals(50.00, item.precoUnitario)
    }

    @Test
    fun `preco unitario aplica o desconto percentual do produto`() {
        val item = ItemPedido.de(Fixtures.produto(preco = "50.00", desconto = 10.0), 1)

        assertEquals(45.00, item.precoUnitario)
    }

    @Test
    fun `preco unitario e arredondado para duas casas (9,99 com 15 porcento = 8,49)`() {
        val item = ItemPedido.de(Fixtures.produto(preco = "9.99", desconto = 15.0), 1)

        assertEquals(8.49, item.precoUnitario)
    }

    // Valor limite do desconto: 100% é o máximo aceito pelo ProdutoRequestDTO
    @Test
    fun `desconto de 100 porcento resulta em preco zero`() {
        val item = ItemPedido.de(Fixtures.produto(preco = "50.00", desconto = 100.0), 1)

        assertEquals(0.0, item.precoUnitario)
    }

    @Test
    fun `subtotal e quantidade vezes preco unitario`() {
        val item = ItemPedido.de(Fixtures.produto(preco = "12.50"), 3)

        assertEquals(0, BigDecimal("37.50").compareTo(item.calcularSubtotal()))
    }
}
