package br.com.enali.model

import br.com.enali.Fixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Testes de unidade da entidade Estoque (camada de domínio).
 * Regras: RN26 (verificar disponibilidade) e RN54 (estoque nunca negativo).
 * Não há mocks: Estoque é um objeto puro, o próprio teste atua como driver.
 */
class EstoqueTest {

    // Valor limite: saldo 10 -> 10 é o maior valor válido, 11 o menor inválido
    @Test
    fun `RN26 - quantidade igual ao saldo esta disponivel (limite superior valido)`() {
        assertTrue(Fixtures.estoque(disponivel = 10).possuiDisponibilidade(10))
    }

    @Test
    fun `RN26 - quantidade uma unidade acima do saldo nao esta disponivel (limite invalido)`() {
        assertFalse(Fixtures.estoque(disponivel = 10).possuiDisponibilidade(11))
    }

    @Test
    fun `RN26 - estoque inativo nunca esta disponivel`() {
        assertFalse(Fixtures.estoque(disponivel = 10, ativo = false).possuiDisponibilidade(1))
    }

    @Test
    fun `reservar move a quantidade do disponivel para o reservado`() {
        val reservado = Fixtures.estoque(disponivel = 10, reservada = 2).reservar(3)

        assertEquals(7, reservado.quantidadeDisponivel)
        assertEquals(5, reservado.quantidadeReservada)
    }

    @Test
    fun `RN54 - reservar todo o saldo zera o disponivel sem ficar negativo`() {
        val reservado = Fixtures.estoque(disponivel = 4).reservar(4)

        assertEquals(0, reservado.quantidadeDisponivel)
        assertEquals(4, reservado.quantidadeReservada)
    }

    @Test
    fun `RN54 - reservar acima do saldo e rejeitado`() {
        val erro = assertThrows<IllegalArgumentException> {
            Fixtures.estoque(disponivel = 4).reservar(5)
        }
        assertEquals("Estoque insuficiente para o produto Vaso de Cerâmica.", erro.message)
    }

    // Partição inválida: quantidade <= 0 (0 é o limite)
    @Test
    fun `reservar quantidade zero e rejeitado`() {
        assertThrows<IllegalArgumentException> { Fixtures.estoque().reservar(0) }
    }

    @Test
    fun `liberar devolve a reserva para o disponivel`() {
        val liberado = Fixtures.estoque(disponivel = 7, reservada = 3).liberar(3)

        assertEquals(10, liberado.quantidadeDisponivel)
        assertEquals(0, liberado.quantidadeReservada)
    }

    @Test
    fun `RN54 - liberar mais do que o reservado e rejeitado`() {
        assertThrows<IllegalArgumentException> {
            Fixtures.estoque(disponivel = 7, reservada = 2).liberar(3)
        }
    }

    @Test
    fun `liberar quantidade zero e rejeitado`() {
        assertThrows<IllegalArgumentException> { Fixtures.estoque(reservada = 2).liberar(0) }
    }
}
