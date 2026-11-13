class PedidoService {
    fun criar(clienteId: Long, request: PedidoRequest): PedidoResponse {
        // RN17: somente clientes autenticados (e ativos) podem realizar pedidos
        val cliente = clienteRepository.findByIdAndAtivoTrue(clienteId)
        if (cliente == null) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Somente clientes autenticados podem realizar pedidos.")
        }

        // RN18: o endereço de entrega deve ser um endereço ativo do próprio cliente
        val endereco = enderecoRepository.findByIdAndClienteIdAndAtivoTrue(request.enderecoId, clienteId)
        if (endereco == null) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Cadastre ou selecione um endereço de entrega válido.")
        }

        if (request.itens.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "O pedido deve conter pelo menos um item.")
        }

        val quantidades = somarQuantidadesPorProduto(request.itens)
        val itens = mutableListOf<ItemPedido>()
        val estoquesReservados = mutableListOf<Estoque>()
        val indisponiveis = mutableListOf<String>()

        for ((produtoId, quantidade) in quantidades) {
            val estoque = estoqueRepository.findFirstByProdutoIdOrderByIdAsc(produtoId)
            if (estoque == null) {
                throw ResponseStatusException(HttpStatus.NOT_FOUND, "Produto $produtoId não encontrado.")
            }

            // Fluxo de exceção b: produto desativado durante a compra
            if (!estoque.produto.disponivel) {
                throw ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "O produto ${estoque.produto.nome} foi desativado. Remova-o do carrinho.")
            }

            // RN26: verifica a disponibilidade antes de confirmar
            if (estoque.possuiDisponibilidade(quantidade)) {
                itens.add(ItemPedido.de(estoque.produto, quantidade))
                estoquesReservados.add(estoque.reservar(quantidade))
            } else {
                indisponiveis.add(estoque.produto.nome)
            }
        }

        // Fluxo de exceção a: informa quais produtos estão sem estoque suficiente
        if (indisponiveis.isNotEmpty()) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Estoque insuficiente para: ${indisponiveis.joinToString(", ")}.")
        }

        // RN27: registra produtos, quantidades, valores e endereço de entrega
        val pedido = mapper.toEntity(request, gerarNumeroPedido(), cliente, endereco, itens)

        // Fluxo de exceção c: falha no cálculo do valor total
        if (pedido.valorTotal <= 0.0) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_CONTENT, "Não foi possível calcular o valor total do pedido.")
        }

        // Fluxo de exceção d: o pedido é gravado antes da reserva; se falhar, nenhuma baixa é feita
        val salvo = repository.save(pedido)
        // RN54: Estoque.reservar() já impediu saldo negativo
        estoqueRepository.saveAll(estoquesReservados)

        return mapper.toResponse(salvo)
    }
}
