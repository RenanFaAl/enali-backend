package br.com.enali.service

import br.com.enali.Fixtures
import br.com.enali.dto.PedidoRequest
import br.com.enali.mapper.PedidoMapper
import br.com.enali.model.Estoque
import br.com.enali.model.ItemPedido
import br.com.enali.model.Pedido
import br.com.enali.repository.ClienteRepository
import br.com.enali.repository.EnderecoRepository
import br.com.enali.repository.EstoqueRepository
import br.com.enali.repository.PedidoRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyIterable
import org.mockito.BDDMockito.given
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.inOrder
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Optional

/**
 * Testes de unidade do PedidoService (camada de aplicação) - UC05 Realizar Pedido.
 *
 * Recursos de teste usados:
 * - MOCK: os quatro repositórios são mocks do Mockito (@Mock). Nenhum banco de dados é acessado.
 * - STUB: cada given(...).willReturn(...) programa a resposta de um mock para o cenário do teste.
 * - DRIVER: cada método de teste faz o papel do PedidoController, chamando service.criar(...) diretamente.
 * - O PedidoMapper é usado real (objeto puro, sem dependências).
 *
 * Os testes CT01 a CT09 percorrem os caminhos independentes C1 a C9 do GFC gerado pelo ComplexGraph
 * para o método criar() (V(G) = 10). O caminho C10 é inviável e está explicado no fim da classe.
 */
@ExtendWith(MockitoExtension::class)
class PedidoServiceTest {

    @Mock lateinit var pedidoRepository: PedidoRepository
    @Mock lateinit var clienteRepository: ClienteRepository
    @Mock lateinit var enderecoRepository: EnderecoRepository
    @Mock lateinit var estoqueRepository: EstoqueRepository

    @Captor lateinit var estoquesCaptor: ArgumentCaptor<Iterable<Estoque>>

    private lateinit var service: PedidoService

    private val cliente = Fixtures.cliente(id = 1)
    private val endereco = Fixtures.endereco(id = 10, cliente = cliente)
    private val prefixoHoje = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE)

    private val vaso = Fixtures.produto(id = 100, nome = "Vaso de Cerâmica", preco = "50.00")
    private val cesta = Fixtures.produto(id = 200, nome = "Cesta de Palha", preco = "30.00")
    private val prato = Fixtures.produto(id = 300, nome = "Prato Pintado", preco = "25.00")

    @BeforeEach
    fun criarService() {
        service = PedidoService(pedidoRepository, PedidoMapper(), clienteRepository, enderecoRepository, estoqueRepository)
    }

    // ---------- STUBS reutilizados ----------

    private fun clienteEEnderecoValidos() {
        given(clienteRepository.findByIdAndAtivoTrue(1L)).willReturn(cliente)
        given(enderecoRepository.findByIdAndClienteIdAndAtivoTrue(10L, 1L)).willReturn(endereco)
    }

    private fun noEstoque(estoque: Estoque) {
        given(estoqueRepository.findFirstByProdutoIdOrderByIdAsc(estoque.produto.id)).willReturn(estoque)
    }

    // Simula o banco devolvendo o pedido com o id gerado
    private fun bancoSalvaPedido() {
        given(pedidoRepository.save(any(Pedido::class.java))).willAnswer { it.getArgument<Pedido>(0).copy(id = 1) }
    }

    private fun assertErro(status: HttpStatus, mensagem: String, acao: () -> Unit) {
        val erro = assertThrows<ResponseStatusException> { acao() }
        assertEquals(status, erro.statusCode)
        assertEquals(mensagem, erro.reason)
    }

    private fun assertNadaGravado() {
        verify(pedidoRepository, never()).save(any(Pedido::class.java))
        verify(estoqueRepository, never()).saveAll(anyIterable<Estoque>())
    }

    private fun estoquesReservados(): List<Estoque> {
        verify(estoqueRepository).saveAll(estoquesCaptor.capture())
        return estoquesCaptor.value.toList()
    }

    @Nested
    inner class CaminhosDoGfc {

        // C1: 1-2-4-6-8-9-11-13-14-16-17-19-21
        @Test
        fun `CT01 (C1) - pedido com um item e registrado como PENDENTE e reserva o estoque`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 10))
            bancoSalvaPedido()

            val resposta = service.criar(1L, Fixtures.pedidoRequest(100L to 2))

            assertEquals(1L, resposta.id)
            assertEquals(prefixoHoje + "00001", resposta.numeroPedido)
            assertEquals("PENDENTE", resposta.status)
            assertEquals(100.00, resposta.valorTotal)
            assertEquals(1L, resposta.clienteId)
            assertEquals("Rua das Flores", resposta.ruaEntrega)
            assertEquals(2, resposta.itens.single().quantidade)

            val reservado = estoquesReservados().single()
            assertEquals(8, reservado.quantidadeDisponivel)
            assertEquals(2, reservado.quantidadeReservada)
        }

        // C2: 1-2-3 (RN17)
        @Test
        fun `CT02 (C2) RN17 - cliente inexistente ou inativo nao pode realizar pedido`() {
            given(clienteRepository.findByIdAndAtivoTrue(1L)).willReturn(null)

            assertErro(HttpStatus.FORBIDDEN, "Somente clientes autenticados podem realizar pedidos.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 1))
            }
            verifyNoInteractions(enderecoRepository, estoqueRepository, pedidoRepository)
        }

        // C3: 1-2-4-5 (RN18)
        @Test
        fun `CT03 (C3) RN18 - endereco que nao e do cliente ou esta inativo e rejeitado`() {
            given(clienteRepository.findByIdAndAtivoTrue(1L)).willReturn(cliente)
            given(enderecoRepository.findByIdAndClienteIdAndAtivoTrue(99L, 1L)).willReturn(null)

            assertErro(HttpStatus.UNPROCESSABLE_CONTENT, "Cadastre ou selecione um endereço de entrega válido.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 1, enderecoId = 99))
            }
            verifyNoInteractions(estoqueRepository, pedidoRepository)
        }

        // C4: 1-2-4-6-7
        @Test
        fun `CT04 (C4) - pedido sem itens e rejeitado`() {
            clienteEEnderecoValidos()

            assertErro(HttpStatus.BAD_REQUEST, "O pedido deve conter pelo menos um item.") {
                service.criar(1L, PedidoRequest(enderecoId = 10, itens = emptyList()))
            }
            verifyNoInteractions(estoqueRepository, pedidoRepository)
        }

        // C5: 1-2-4-6-8-9-10
        @Test
        fun `CT05 (C5) - produto inexistente e rejeitado`() {
            clienteEEnderecoValidos()
            given(estoqueRepository.findFirstByProdutoIdOrderByIdAsc(999L)).willReturn(null)

            assertErro(HttpStatus.NOT_FOUND, "Produto 999 não encontrado.") {
                service.criar(1L, Fixtures.pedidoRequest(999L to 1))
            }
            assertNadaGravado()
        }

        // C6: 1-2-4-6-8-9-11-12 (fluxo de exceção b)
        @Test
        fun `CT06 (C6) - produto desativado durante a compra pede remocao do carrinho`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso.copy(disponivel = false)))

            assertErro(HttpStatus.UNPROCESSABLE_CONTENT, "O produto Vaso de Cerâmica foi desativado. Remova-o do carrinho.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 1))
            }
            assertNadaGravado()
        }

        // C7: 1-2-4-6-8-9-11-13-15-14-16-17-18 (RN26, fluxo de exceção a)
        @Test
        fun `CT07 (C7) RN26 - estoque insuficiente impede o pedido e informa o produto`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 1))

            assertErro(HttpStatus.CONFLICT, "Estoque insuficiente para: Vaso de Cerâmica.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 2))
            }
            assertNadaGravado()
        }

        // C8: 1-2-4-6-8-9-11-13-14-16-17-19-20 (fluxo de exceção c)
        @Test
        fun `CT08 (C8) - falha no calculo (total zero) aborta o pedido`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso.copy(desconto = 100.0)))

            assertErro(HttpStatus.UNPROCESSABLE_CONTENT, "Não foi possível calcular o valor total do pedido.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 1))
            }
            assertNadaGravado()
        }

        // C9: 1-2-4-6-8-9-11-13-14-16-8-9-11-13-14-16-17-19-21 (laço com duas iterações)
        @Test
        fun `CT09 (C9) - pedido com dois produtos soma o total e reserva os dois estoques`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 10))
            noEstoque(Fixtures.estoque(cesta, disponivel = 5))
            bancoSalvaPedido()

            val resposta = service.criar(1L, Fixtures.pedidoRequest(100L to 2, 200L to 1))

            assertEquals(130.00, resposta.valorTotal)
            assertEquals(2, resposta.itens.size)
            val reservados = estoquesReservados().associateBy { it.produto.id }
            assertEquals(8, reservados.getValue(100L).quantidadeDisponivel)
            assertEquals(4, reservados.getValue(200L).quantidadeDisponivel)
        }

        // C10: 1-2-4-6-8-9-11-13-14-16-17-18 é INVIÁVEL: o nó 17 (há indisponíveis?) só é verdadeiro
        // se algum item passou pelo nó 15 (ELSE). Por isso não existe caso de teste para ele.
    }

    @Nested
    inner class ValorLimiteEParticoes {

        @Test
        fun `CT10 RN26 RN54 - quantidade igual ao saldo (limite) e aceita e zera o disponivel`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 3))
            bancoSalvaPedido()

            service.criar(1L, Fixtures.pedidoRequest(100L to 3))

            assertEquals(0, estoquesReservados().single().quantidadeDisponivel)
        }

        @Test
        fun `CT11 RN26 - quantidade uma unidade acima do saldo (limite) e rejeitada`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 3))

            assertErro(HttpStatus.CONFLICT, "Estoque insuficiente para: Vaso de Cerâmica.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 4))
            }
        }

        @Test
        fun `CT12 RN26 - lista todos os produtos sem estoque e nao reserva nenhum`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 10))
            noEstoque(Fixtures.estoque(cesta, disponivel = 0))
            noEstoque(Fixtures.estoque(prato, disponivel = 1))

            assertErro(HttpStatus.CONFLICT, "Estoque insuficiente para: Cesta de Palha, Prato Pintado.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 1, 200L to 1, 300L to 2))
            }
            assertNadaGravado()
        }

        @Test
        fun `CT13 RN26 - estoque inativo conta como indisponivel`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 10, ativo = false))

            assertErro(HttpStatus.CONFLICT, "Estoque insuficiente para: Vaso de Cerâmica.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 1))
            }
        }

        @Test
        fun `CT14 RN26 - mesmo produto repetido no carrinho tem as quantidades somadas`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 4))

            // 3 + 2 = 5 unidades, mas só há 4
            assertErro(HttpStatus.CONFLICT, "Estoque insuficiente para: Vaso de Cerâmica.") {
                service.criar(1L, Fixtures.pedidoRequest(100L to 3, 100L to 2))
            }
        }
    }

    @Nested
    inner class RegistroDoPedido {

        @Test
        fun `CT15 - falha ao gravar o pedido nao faz baixa no estoque (fluxo de excecao d)`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 10))
            given(pedidoRepository.save(any(Pedido::class.java))).willThrow(DataIntegrityViolationException("falha no banco"))

            assertThrows<DataIntegrityViolationException> {
                service.criar(1L, Fixtures.pedidoRequest(100L to 1))
            }
            verify(estoqueRepository, never()).saveAll(anyIterable<Estoque>())
        }

        @Test
        fun `CT16 - pedido e gravado antes da reserva do estoque`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 10))
            bancoSalvaPedido()

            service.criar(1L, Fixtures.pedidoRequest(100L to 1))

            val ordem = inOrder(pedidoRepository, estoqueRepository)
            ordem.verify(pedidoRepository).save(any(Pedido::class.java))
            ordem.verify(estoqueRepository).saveAll(anyIterable<Estoque>())
        }

        @Test
        fun `CT17 - numero do pedido segue a sequencia do dia`() {
            clienteEEnderecoValidos()
            noEstoque(Fixtures.estoque(vaso, disponivel = 10))
            bancoSalvaPedido()
            val anterior = Pedido(numeroPedido = prefixoHoje + "00007", valorTotal = 10.0, status = "PENDENTE")
            given(pedidoRepository.findByNumeroPedidoStartingWith(prefixoHoje)).willReturn(listOf(anterior))

            val resposta = service.criar(1L, Fixtures.pedidoRequest(100L to 1))

            assertEquals(prefixoHoje + "00008", resposta.numeroPedido)
        }
    }

    @Nested
    inner class Cancelamento {

        private fun pedidoComVaso(status: String) = Pedido(
            id = 5,
            numeroPedido = prefixoHoje + "00001",
            valorTotal = 100.0,
            status = status,
            itens = listOf(ItemPedido.de(vaso, 2))
        )

        @Test
        fun `CT18 - cancelar devolve a reserva ao estoque e marca CANCELADO`() {
            given(pedidoRepository.findById(5L)).willReturn(Optional.of(pedidoComVaso("PENDENTE")))
            noEstoque(Fixtures.estoque(vaso, disponivel = 8, reservada = 2))
            bancoSalvaPedido()

            val resposta = service.cancelar(5L)

            assertEquals("CANCELADO", resposta.status)
            val liberado = estoquesReservados().single()
            assertEquals(10, liberado.quantidadeDisponivel)
            assertEquals(0, liberado.quantidadeReservada)
        }

        @Test
        fun `CT19 - pedido ja cancelado nao pode ser cancelado de novo`() {
            given(pedidoRepository.findById(5L)).willReturn(Optional.of(pedidoComVaso("CANCELADO")))

            assertErro(HttpStatus.CONFLICT, "Este pedido já foi cancelado") { service.cancelar(5L) }
            verifyNoInteractions(estoqueRepository)
        }

        @Test
        fun `CT20 - cancelar pedido inexistente retorna 404`() {
            given(pedidoRepository.findById(5L)).willReturn(Optional.empty())

            assertErro(HttpStatus.NOT_FOUND, "Pedido não encontrado") { service.cancelar(5L) }
        }
    }
}
