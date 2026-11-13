"""
Engenharia reversa: gera o diagrama de classes (PlantUML) do UC05 - Realizar Pedido
a partir do BYTECODE compilado (build/classes), usando o javap do JDK.

Uso (na raiz do enali-backend, depois de ./gradlew test):
    python docs/plano-de-teste/grupo3/diagrama/gerar_diagrama.py
    java -jar plantuml.jar docs/plano-de-teste/grupo3/diagrama/diagrama-classes.puml
"""
import re
import subprocess
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[4]
SAIDA = Path(__file__).with_name("diagrama-classes.puml")
CLASSPATH = ";".join(str(RAIZ / p) for p in ("build/classes/kotlin/main", "build/classes/kotlin/test"))

PRODUCAO = {
    "service": ["PedidoService"],
    "mapper": ["PedidoMapper"],
    "dto": ["PedidoRequest", "ItemPedidoRequest", "PedidoResponse", "ItemPedidoResponse"],
    "model": ["Pedido", "ItemPedido", "Estoque", "Produto", "Cliente", "Endereco"],
    "repository": ["PedidoRepository", "ClienteRepository", "EnderecoRepository", "EstoqueRepository"],
}
TESTE = {
    "service": ["PedidoServiceTest", "PedidoServiceTest$CaminhosDoGfc", "PedidoServiceTest$ValorLimiteEParticoes",
                "PedidoServiceTest$RegistroDoPedido", "PedidoServiceTest$Cancelamento"],
    "mapper": ["PedidoMapperTest"],
    "model": ["PedidoTest", "ItemPedidoTest", "EstoqueTest"],
    "repository": ["PedidoPersistenciaTest"],
    "": ["Fixtures"],
}
# Classe sob teste de cada classe de teste (convenção XTest -> X)
SOB_TESTE = {"PedidoServiceTest": "PedidoService", "PedidoMapperTest": "PedidoMapper", "PedidoTest": "Pedido",
             "ItemPedidoTest": "ItemPedido", "EstoqueTest": "Estoque", "PedidoPersistenciaTest": "PedidoService"}

KOTLIN = {"long": "Long", "int": "Int", "double": "Double", "boolean": "Boolean", "void": "Unit",
          "java.lang.String": "String", "java.lang.Object": "Any", "java.math.BigDecimal": "BigDecimal"}
IGNORAR = re.compile(r"^(component\d+|copy|toString|hashCode|equals|get[A-Z]\w*|set[A-Z]\w*|is[A-Z]\w*|.*\$.*)$")


def fqn(pacote, nome):
    return "br.com.enali." + (pacote + "." if pacote else "") + nome


def simples(tipo):
    tipo = tipo.strip()
    if tipo in KOTLIN:
        return KOTLIN[tipo]
    tipo = re.sub(r"[\w.]+\.(\w+)", r"\1", tipo)  # remove pacotes, inclusive dentro de generics
    return tipo.replace("? extends ", "")


def javap(nomes, verbose=False):
    args = ["javap", "-p"] + (["-v"] if verbose else []) + ["-cp", CLASSPATH] + nomes
    return subprocess.run(args, capture_output=True, text=True, encoding="utf-8", errors="replace", check=True).stdout


def campos_mock(nome):
    """Campos anotados com @org.mockito.Mock (lidos do bytecode com javap -v)."""
    mocks, atual = set(), None
    for linha in javap([nome], verbose=True).splitlines():
        m = re.match(r"^  (?:public|private|protected)?[\w\s]*? ([\w.$<>, ]+) (\w+);$", linha)
        if m:
            atual = m.group(2)
        elif atual and "org.mockito.Mock" in linha:
            mocks.add(atual)
    return mocks


def ler_tipo(texto):
    """Separa "tipo nome" respeitando generics com espaço, ex.: java.util.Map<A, B> nome."""
    nivel = 0
    for i, c in enumerate(texto):
        nivel += (c == "<") - (c == ">")
        if c == " " and nivel == 0:
            return texto[:i], texto[i + 1:]
    return texto, ""


def dividir(params):
    partes, nivel, atual = [], 0, ""
    for c in params:
        nivel += (c == "<") - (c == ">")
        if c == "," and nivel == 0:
            partes.append(atual)
            atual = ""
        else:
            atual += c
    return [p for p in partes + [atual] if p.strip()]


def ler_classe(nome_qualificado):
    texto = javap([nome_qualificado])
    cab = re.search(r"^(?:public |final |abstract )*(class|interface) ([\w.$]+)(?: extends ([^{]+?))?(?: implements [^{]+)? \{",
                    texto, re.M)
    info = {"tipo": cab.group(1), "extends": cab.group(3) or "", "campos": [], "metodos": []}
    for linha in texto.splitlines()[2:]:
        linha = linha.strip().rstrip(";")
        if not linha or linha == "}" or "static" in linha.split("(")[0] and "Companion" in linha:
            continue
        linha = re.sub(r" throws [\w.$, ]+$", "", linha)
        if linha.endswith(")"):
            # Método: a lista de parâmetros é o último "(...)"; nomes de teste Kotlin podem ter espaços e parênteses
            inicio = linha.rfind("(")
            cabeca, params = linha[:inicio], linha[inicio + 1:-1]
            m = re.match(r"^(?:(public|private|protected) )?((?:(?:static|final|abstract|synchronized) )*)(.*)$", cabeca)
            vis, modificadores, resto = m.groups()
            retorno, nome = ler_tipo(resto)
            if not nome or "static" in modificadores or nome.startswith("br.com.enali") or IGNORAR.match(nome):
                continue  # construtores, métodos gerados pelo Kotlin, lambdas
            params = ", ".join(simples(p) for p in dividir(params))
            info["metodos"].append((vis or "public", nome, params, simples(retorno), retorno))
        else:
            m = re.match(r"^(public|private|protected)?\s*(?:static\s+)?(?:final\s+)?(.+) (\w+)$", linha)
            if m and not m.group(3).startswith(("this$", "$", "Companion", "INSTANCE")):
                info["campos"].append((m.group(1) or "public", m.group(3), simples(m.group(2)), m.group(2)))
    return info


def main():
    classes = {}
    for grupo, mapa in (("main", PRODUCAO), ("test", TESTE)):
        for pacote, nomes in mapa.items():
            for nome in nomes:
                classes[nome] = (grupo, pacote, ler_classe(fqn(pacote, nome)))

    nomes_conhecidos = {n.split("$")[-1]: n for n in classes}
    vis_uml = {"public": "+", "private": "-", "protected": "#"}

    def apelido(nome):
        return nome.replace("$", "_")

    def referencias(tipo_bruto):
        return [nomes_conhecidos[t] for t in re.findall(r"\.(\w+)(?=[<>,\s]|$)", tipo_bruto)
                if t in nomes_conhecidos and not nomes_conhecidos[t].endswith("Test")]

    linhas = ["@startuml", "!pragma layout smetana", "set separator none", "hide empty members",
              "skinparam classAttributeIconSize 0", "skinparam packageStyle rectangle",
              "skinparam defaultFontName Arial", "skinparam linetype ortho", "left to right direction",
              "title UC05 - Realizar Pedido: classes de produção e de teste (engenharia reversa do bytecode)", ""]

    for grupo, titulo in (("main", "Produção (src/main/kotlin)"), ("test", "Teste (src/test/kotlin)")):
        linhas.append(f'package "{titulo}" {{')
        mapa = PRODUCAO if grupo == "main" else TESTE
        for pacote, nomes in mapa.items():
            # apelido distinto: o mesmo pacote existe em src/main e src/test
            linhas.append(f'  package "br.com.enali{"." + pacote if pacote else ""}" as {grupo}_{pacote or "raiz"} {{')
            for nome in nomes:
                _, _, info = classes[nome]
                rotulo = nome.replace("$", ".")
                estereotipo = " <<teste>>" if grupo == "test" and nome != "Fixtures" else ""
                linhas.append(f'    {info["tipo"]} "{rotulo}" as {apelido(nome)}{estereotipo} {{')
                for vis, campo, tipo, _ in info["campos"]:
                    linhas.append(f"      {vis_uml[vis]}{campo}: {tipo}")
                for vis, metodo, params, retorno, _ in info["metodos"]:
                    if grupo == "test" and len(metodo) > 70:
                        metodo = metodo[:67] + "..."
                    sufixo = "" if retorno == "Unit" else f": {retorno}"
                    linhas.append(f"      {vis_uml[vis]}{metodo}({params}){sufixo}")
                linhas.append("    }")
            linhas.append("  }")
        linhas.append("}")
        linhas.append("")

    relacoes = set()
    for nome, (grupo, pacote, info) in classes.items():
        origem = apelido(nome)
        mocks = campos_mock(fqn(pacote, nome)) if grupo == "test" else set()
        campos_alvo = set()
        for _, campo, _, bruto in info["campos"]:
            for alvo in referencias(bruto):
                if alvo == nome or (grupo == "test" and classes[alvo][1] == "model"):
                    continue  # nos testes, entidades são só dados de teste
                campos_alvo.add(alvo)
                if campo in mocks:
                    relacoes.add(f'{origem} ..> {apelido(alvo)} : <<mock>>')
                elif bruto.startswith("java.util.List"):
                    relacoes.add(f'{origem} "1" *-- "*" {apelido(alvo)} : {campo}')
                else:
                    relacoes.add(f'{origem} --> {apelido(alvo)} : {campo}')
        if grupo == "main":
            for alvo in referencias(info["extends"]):
                relacoes.add(f'{origem} ..> {apelido(alvo)} : <<JpaRepository>>')
                campos_alvo.add(alvo)
            for vis, metodo, params, retorno, bruto in info["metodos"]:
                for alvo in set(referencias(bruto)) | {nomes_conhecidos[p] for p in re.findall(r"\w+", params)
                                                      if p in nomes_conhecidos}:
                    if alvo != nome and alvo not in campos_alvo and not alvo.endswith("Test"):
                        relacoes.add(f"{origem} ..> {apelido(alvo)}")
        if "$" in nome:
            relacoes.add(f"{apelido(nome.split('$')[0])} +-- {origem}")
        if nome in SOB_TESTE and SOB_TESTE[nome] not in campos_alvo:
            relacoes.add(f"{origem} ..> {SOB_TESTE[nome]} : <<testa>>")
        if grupo == "test" and nome != "Fixtures" and "$" not in nome and "Fixtures" in javap(["-c", fqn(pacote, nome)]):
            relacoes.add(f"{origem} ..> Fixtures : <<usa>>")

    linhas += sorted(relacoes)
    linhas.append("@enduml")
    SAIDA.write_text("\n".join(linhas) + "\n", encoding="utf-8")
    print(f"{SAIDA} ({len(classes)} classes, {len(relacoes)} relacionamentos)")


if __name__ == "__main__":
    main()
