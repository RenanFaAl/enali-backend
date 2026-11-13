package br.com.enali

import br.com.enali.dto.ItemPedidoRequest
import br.com.enali.dto.PedidoRequest
import br.com.enali.model.Cliente
import br.com.enali.model.Endereco
import br.com.enali.model.Estoque
import br.com.enali.model.Produto
import java.math.BigDecimal
import java.time.LocalDate

/**
 * Dados de teste reutilizados pelas classes de teste do UC05 - Realizar Pedido.
 * Não acessa banco: só monta objetos em memória.
 */
object Fixtures {

    fun cliente(id: Long = 1, ativo: Boolean = true) = Cliente(
        id = id,
        nome = "Maria Artesã",
        email = "maria@enali.com",
        senha = "hash",
        cpf = "12345678901",
        dataNascimento = LocalDate.of(1990, 1, 1),
        ativo = ativo
    )

    fun endereco(id: Long = 10, cliente: Cliente? = cliente()) = Endereco(
        id = id,
        rua = "Rua das Flores",
        numero = "100",
        bairro = "Centro",
        cidade = "São Paulo",
        estado = "São Paulo",
        siglaEstado = "SP",
        cep = "01001-000",
        cliente = cliente
    )

    fun produto(
        id: Long = 100,
        nome: String = "Vaso de Cerâmica",
        preco: String = "50.00",
        desconto: Double = 0.0,
        disponivel: Boolean = true
    ) = Produto(
        id = id,
        nome = nome,
        descricao = "Peça artesanal",
        preco = BigDecimal(preco),
        disponivel = disponivel,
        desconto = desconto
    )

    fun estoque(
        produto: Produto = produto(),
        disponivel: Int = 10,
        reservada: Int = 0,
        ativo: Boolean = true
    ) = Estoque(
        produto = produto,
        quantidadeDisponivel = disponivel,
        quantidadeReservada = reservada,
        localizacao = "Prateleira A",
        ativo = ativo
    )

    fun pedidoRequest(vararg itens: Pair<Long, Int>, enderecoId: Long = 10) = PedidoRequest(
        enderecoId = enderecoId,
        itens = itens.map { (produtoId, quantidade) -> ItemPedidoRequest(produtoId, quantidade) },
        observacoes = "Embrulhar para presente"
    )
}
