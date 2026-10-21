# Board Tasks

Aplicação de linha de comando para criar e acompanhar boards de tarefas. Cada board possui colunas ordenadas e cards que avançam pelo fluxo; também é possível cancelar, bloquear e desbloquear cards, além de consultar relatórios de andamento e bloqueios.

## Requisitos

- Java 17 ou superior
- MySQL acessível pela aplicação
- Gradle Wrapper incluído no repositório (`./gradlew`)

## Configuração do banco

Crie previamente um banco MySQL, por padrão chamado `board`, e configure as credenciais por variáveis de ambiente. A aplicação usa estas configurações:

| Variável | Obrigatória | Padrão / descrição |
| --- | --- | --- |
| `BOARD_DB_URL` | Não | `jdbc:mysql://localhost:3306/board?serverTimezone=UTC` |
| `BOARD_DB_USER` | Sim | Usuário do MySQL |
| `BOARD_DB_PASSWORD` | Sim | Senha do MySQL |

Exemplo em Linux/macOS:

```sh
export BOARD_DB_URL='jdbc:mysql://localhost:3306/board?serverTimezone=UTC'
export BOARD_DB_USER='board'
export BOARD_DB_PASSWORD='board'
```

O usuário configurado precisa ter permissão para criar e alterar tabelas no banco. Ao iniciar, a aplicação executa as migrations Liquibase localizadas em `src/main/resources/db/changelog/migrations`.

## Executar

Na raiz do projeto:

```sh
./gradlew run
```

O menu principal permite criar um board com colunas inicial, pendentes opcionais, final e de cancelamento; selecionar um board existente; ou excluí-lo. Dentro de um board, as opções permitem criar, mover, cancelar, bloquear e desbloquear cards, consultar boards, colunas e cards, e emitir relatórios de tempo por coluna e de bloqueios.

## Testes

Execute a suíte automatizada com:

```sh
./gradlew test
```

Os testes cobrem regras de domínio, serviços, interação com o menu de console e integração do DAO.

## Organização do código

- `br.com.dio.ui`: menus e interação com o terminal
- `br.com.dio.service`: operações e consultas da aplicação
- `br.com.dio.domain`: validações do board e regras de movimentação
- `br.com.dio.persistence`: conexão, entidades, DAOs e migrations
- `br.com.dio.dto`: estruturas de dados usadas entre consultas e relatórios
- `src/test/java`: testes automatizados
