Enali — Sistema de E-commerce

# I) Nome do projeto e do grupo

Nome do projeto: Enali — Sistema de E-commerce  
Nome do grupo: AG2R 


# II) Repositório Git

Link do repositório Back: https://github.com/RenanFaAl/enali-backend

Link do repositório Front: https://github.com/RenanFaAl/enali-Front

# III) Integrantes e responsabilidades

|Integrante|Entidades
|-------|-------|
| Renan Fabiani de Almeida| Cliente e Pedido |
| Guilherme Oliveira Rocha| Administrador e Endereço|
| Arthur Silva Carvalho | Estoque e Produto |
| Rafael José Brito do Nascimento| Material e Fornecedor|


## IV) Descrição da aplicação

O Enali é um sistema de e-commerce desenvolvido com o objetivo de organizar e facilitar o gerenciamento das operações relacionadas a uma loja virtual. A aplicação busca centralizar informações importantes, permitindo o gerenciamento de clientes, pedidos, produtos, administradores, materiais, fornecedores, endereços e estoque.

A solução procura reduzir a necessidade de controles manuais e informações dispersas, contribuindo para uma administração mais organizada dos dados. Com o cadastro e a consulta de informações, a aplicação facilita o acompanhamento dos registros e a manutenção dos dados utilizados nas operações comerciais.

O backend disponibiliza uma API REST responsável por receber requisições, validar os dados enviados, executar as regras da aplicação e realizar operações de persistência no banco de dados PostgreSQL.

A arquitetura em camadas promove a separação de responsabilidades, facilitando a manutenção, os testes e a evolução do sistema.

## V) Tecnologias utilizadas

### Backend

- **Kotlin:** linguagem utilizada no desenvolvimento do backend.
- **Spring Boot:** framework para configuração e execução da aplicação.
- **Spring WebMVC:** implementação dos controladores e endpoints REST.
- **Spring Data JPA:** integração com o banco de dados e operações de persistência.
- **PostgreSQL:** sistema gerenciador de banco de dados relacional.
- **Bean Validation:** validação dos dados recebidos nas requisições.
- **Spring Security:** recursos de autenticação e autorização.
- **Gradle:** gerenciamento de dependências e execução do processo de build.
- **REST Client para VS Code:** ferramenta utilizada para testar as requisições HTTP.

### Frontend

- **Kotlin**: linguagem de programação utilizada no desenvolvimento da interface.
- **Kotlin Multiplatform**: compartilhamento de código entre desktop, mobile e web.
- **Compose Multiplatform**: framework para construção das interfaces gráficas compartilhadas.
- **Material 3 (Compose Material 3)**: componentes visuais para formulários, botões, campos de texto, caixas de seleção e estrutura da aplicação.
- **Compose Runtime**: gerenciamento de estado e atualização reativa da interface.


## VI) Arquitetura da aplicação

### Backend

O backend utiliza uma arquitetura em camadas, separando as responsabilidades de acesso HTTP, conversão de dados, regras da aplicação e persistência.

| Camada       | Pasta         | Responsabilidade |
|-------|-------|-------|
| Model        | `model/`      | Define as entidades JPA que representam os dados persistidos no banco. |
| DTO          | `dto/`        | Define os objetos de Request e Response utilizados na comunicação HTTP. |
| Mapper       | `mapper/`     | Converte os DTOs em entidades e as entidades em DTOs de resposta. |
| Repository   | `repository/` | Realiza as operações de persistência e consultas ao banco de dados. |
| Service      | `service/`    | Centraliza as regras de negócio e as operações CRUD. |
| Controller   | `controller/` | Recebe as requisições HTTP, aplica as validações e encaminha as operações aos serviços. |
| Configuração | `config/`     | Contém configurações gerais da aplicação e, quando implementadas, as configurações de segurança. |

Os arquivos estão organizados sob o pacote-base `br.com.enali`, dentro de `src/main/kotlin/`.

Exemplo da estrutura do backend:

```text
src/
└── main/
    └── kotlin/
        └── br/
            └── com/
                └── enali/
                    ├── model/
                    ├── dto/
                    ├── mapper/
                    ├── repository/
                    ├── service/
                    ├── controller/
                    └── config/
```

A camada de `model` define as entidades. Os `repositórios` utilizam o Spring Data JPA para consultar e persistir os registros. Os `serviços` executam as operações de negócio, enquanto os `controladores` disponibilizam os endpoints da API.

Os `DTOs` separam os dados recebidos e retornados pela API das entidades utilizadas internamente. Os `mappers` realizam as conversões entre essas estruturas, e as validações de entrada são aplicadas por meio do Bean Validation.

### Frontend

O frontend é responsável pela interface com o usuário e pela comunicação com a API REST do backend.

Arquitetura:


| Camada| Pasta | Responsabilidade|
|-------|-------|-------|
| Model       | `model/`       | Modelos de dados de entidades.
| UI       | `ui/`       | Camada de estilização e páginas.
| Components  | `ui/components/`  |  Componentes reutilizáveis, como a barra lateral de navegação.
| Screens     | `ui/screens/`     | Telas de formulário para cadastro de entidades.
| Theme     | `ui/theme/`     |  Tema padronizado de cores.

```text
frontend/
└── shared/
    └── src/
        └── commonMain/
            └── kotlin/
                └── br/
                    └── com/
                        └── enali/
                            ├── model/
                            ├── ui/
                            │   ├── components/
                            │   ├── screens/
                            │   └── theme/
                            └── App.kt
```


## VII) Instalação, execução e acesso

### Pré-requisitos

Antes de iniciar a aplicação, é necessário ter instalado:

- JDK compatível com a configuração do projeto — atualmente configurado para Java 21.
- PostgreSQL.
- Git.
- Visual Studio Code ou outra IDE compatível com Kotlin e Gradle.

## Backend

### 1. Baixar ou clonar o repositório

Após inserir a URL correta do repositório, execute:

```bash
git clone https://github.com/RenanFaAl/enali-backend
cd enali-backend
```

Ajuste o nome da pasta conforme o repositório clonado.

### 2. Configurar o banco de dados

#### Crie um banco de dados PostgreSQL para a aplicação. Por exemplo:

```sql
CREATE DATABASE enali;
```

#### Configurar as variáveis de ambiente
   
Na raiz do projeto, crie um arquivo .env com as seguintes variáveis:

```properties
DB_URL=jdbc:postgresql://localhost:5432/enali
DB_USER=seu_usuario
DB_PASSWORD=sua_senha
JWT_SECRET=sua_chave_secreta
```
Substitua seu_usuario e sua_senha pelas credenciais do seu PostgreSQL.

#### Configurar o segredo JWT

A variável JWT_SECRET deve conter uma chave secreta hexadecimal forte, utilizada na assinatura dos tokens de autenticação. Gere uma chave segura e mantenha-a privada. Não compartilhe esse valor nem o envie para o repositório.

#### Configurar o acesso às variáveis

Verifique se o back-end está configurado para carregar as variáveis de ambiente e utilizá-las na configuração do Spring Boot. Caso utilize o arquivo .env, confirme que o projeto possui um mecanismo para carregá-lo, pois o Spring Boot não carrega automaticamente qualquer arquivo .env por padrão.

#### Segurança

Não publique o arquivo .env no GitHub.

Não compartilhe as credenciais do banco de dados ou o segredo JWT.

Em ambientes de produção, configure as variáveis de ambiente diretamente no ambiente de implantação.

### 3. Executar o backend

No Windows, Linux ou Mac, abra o terminal na raiz do backend e execute:

```powershell
gradlew bootRun
```

### 4. Acessar a API

Considerando a configuração padrão da aplicação, a API estará disponível em:

`http://localhost:8080`

Exemplos de endpoints implementados para as entidades:

|Método |	Endpoint	|Descrição|
|--------|------------|---------|
|POST	 |/auth/cliente/signup	|Cadastrar cliente|
|POST	|/auth/cliente/signin	|Realizar login de cliente|
|POST	|/auth/admin/signin	|Realizar login de administrador|
|GET |	/clientes/me	|Consultar dados do cliente autenticado|
|PUT|	/clientes/me	|Atualizar dados do cliente autenticado|
|POST|	/pedidos	|Criar pedido|
|GET|	/pedidos	|Listar pedidos (administrador)|
|GET|	/pedidos/{id}	|Consultar pedido por ID|
|PATCH|	/pedidos/{id}/cancelar	|Cancelar pedido|
|GET	|/produtos	|Listar produtos|
|POST	|/produtos	|Cadastrar produto|
|GET	|/materiais	|Listar materiais|



### 5. Testar as requisições

As requisições HTTP podem ser testadas utilizando a extensão REST Client do Visual Studio Code. Para isso utilize a pasta `http` e execute as requisições pela opção **Send Request**.

Obs: As rotas protegidas precisarão das credenciais ou do token de autenticação exigido pela configuração de segurança.

## Frontend

### 1. Baixar ou clonar o frontend

Após inserir a URL correta do repositório, execute:

```bash
git clone https://github.com/RenanFaAl/enali-Front
cd enali-front
```

Ajuste o nome da pasta conforme o repositório clonado.

### 2. Executar o frontend
   
Para executar o Front-end, utilize:
```bash
gradlew :desktopApp:run
```

## Considerações finais

O Enali utiliza uma arquitetura organizada para separar as responsabilidades do sistema, facilitando a manutenção e a implementação de novas funcionalidades. A utilização de Kotlin, Spring Boot, Spring Data JPA e PostgreSQL oferece uma base para o desenvolvimento de uma API REST estruturada, com persistência relacional e validação de dados.
