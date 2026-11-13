package br.com.enali.model

import br.com.enali.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * Testes de unidade da entidade Pedido (camada de domínio): cálculo do valor total (passo 15 do UC05).
 */
class PedidoTest {

    private fun pedidoCom(vararg itens: ItemPedido) = Pedido(
        numeroPedido = "2026111200001",
        valorTotal = 0.0,
        status = "PENDENTE",
        itens = itens.toList()
    )

    @Test
    fun `calcularTotal soma os subtotais de todos os itens`() {
        val pedido = pedidoCom(
            ItemPedido.de(Fixtures.produto(id = 1, preco = "50.00"), 2),
            ItemPedido.de(Fixtures.produto(id = 2, preco = "19.90"), 3)
        )

        assertEquals(159.70, pedido.calcularTotal())
    }

    @Test
    fun `calcularTotal nao acumula erro de ponto flutuante (3 x 0,10 = 0,30)`() {
        val pedido = pedidoCom(ItemPedido.de(Fixtures.produto(preco = "0.10"), 3))

        assertEquals(0.30, pedido.calcularTotal())
    }

    @Test
    fun `calcularTotal de pedido sem itens e zero`() {
        assertEquals(0.0, pedidoCom().calcularTotal())
    }
}
