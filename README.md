# Teoria da Computação — IFTM

Material para a disciplina de Teoria da Computação (Licenciatura em Computação — IFTM — Uberlândia Centro).

Este repositório reúne páginas interativas e um projeto Java (`MTgerador`) voltados ao estudo do **Problema do Castor Ocupado** (Busy Beaver) para máquinas de Turing de 3 estados.

## Conteúdo

- **`busy_beaver.html`** — decodificador e simulador de máquinas de Turing de 3 estados (A, B, C + H). Recebe o número inteiro que identifica uma máquina, decodifica sua função de transição e permite simular sua execução passo a passo.
- **`stack_machine_simulator.html`** — simulador interativo das *Stack Machines* de Webber.
- **`MTgerador/`** — projeto Java que executa exaustivamente todas as máquinas de 3 estados e grava o perfil de cada uma em um banco de dados para análise posterior.

---

# Projeto MTgerador

O `MTgerador` percorre todas as 2^24 = 16.777.216 máquinas de Turing de 3 estados úteis (A, B, C) mais o estado de parada (H), simula cada uma a partir da fita vazia por no máximo 21 passos (o valor de S(3), o máximo de passos de uma máquina de 3 estados que para) e registra, para cada máquina, um conjunto de dados em uma tabela de banco de dados.

O banco resultante pode então ser usado para investigações diversas — estatísticas ou diretas — sobre o perfil desse conjunto de máquinas.

O identificador (campo `ID`) de cada máquina é a conversão para decimal da representação binária de sua função de transição. A página `busy_beaver.html` explica como essa representação é construída.

## Tecnologias

O projeto foi desenvolvido no **Apache NetBeans** e usa o **Apache Derby** como banco de dados, acessado em modo cliente/servidor (Derby Network Server) via JDBC. Aqui estão disponíveis apenas os fontes.

---

## Como preparar o ambiente

As instruções a seguir permitem recriar o banco de dados localmente, do zero.

### 1. Instalar o Apache Derby

Baixe a distribuição binária do Apache Derby em <https://db.apache.org/derby/derby_downloads.html> (versão 10.14 ou superior é recomendada; qualquer versão >= 10.7 suporta o tipo `BOOLEAN` usado aqui). Descompacte em uma pasta de sua escolha — essa pasta será referida como `DERBY_HOME`.

A distribuição já inclui:
- o servidor de rede (`derbynet.jar`),
- o driver cliente JDBC (`derbyclient.jar`),
- a ferramenta interativa de linha de comando **`ij`**, usada abaixo para criar a tabela.

### 2. Iniciar o servidor Derby

Em um terminal, a partir de `DERBY_HOME`:

```bash
# Linux / macOS / WSL
java -jar lib/derbyrun.jar server start

# Windows (prompt de comando)
java -jar lib\\derbyrun.jar server start
```

O servidor sobe na porta padrão **1527**, que é a que o código espera (`jdbc:derby://localhost:1527/`). Deixe esse terminal aberto — o servidor precisa continuar rodando enquanto o programa Java é executado.

### 3. Criar o banco e a tabela

Em **outro** terminal, inicie a ferramenta `ij`:

```bash
java -jar lib/derbyrun.jar ij
```

Dentro do `ij`, execute os comandos abaixo. O primeiro cria (e conecta a) o banco de dados chamado `mturing`; o atributo `create=true` faz o Derby criá-lo caso ainda não exista:

```sql
CONNECT 'jdbc:derby://localhost:1527/mturing;create=true';

CREATE TABLE TB_EXECUCOES (
    ID       INTEGER  NOT NULL PRIMARY KEY,
    PAROU    BOOLEAN,
    PASSOS   SMALLINT,
    UNS      SMALLINT,
    TAM_FITA SMALLINT
);
```

> **Nota sobre os tipos.** O código Java grava os cinco campos com `setInt`/`setBoolean`. O `ID` varia de 0 a 16.777.215, o que cabe folgadamente em `INTEGER`. Os demais campos (`PASSOS`, `UNS`, `TAM_FITA`) assumem valores pequenos no universo de 3 estados (limitados por S(3) = 21 e vizinhança), cabendo em `SMALLINT`. Se preferir uniformizar, pode declarar todos como `INTEGER` sem qualquer prejuízo.
>
> **Versões antigas do Derby.** O tipo `BOOLEAN` em `CREATE TABLE` só é suportado a partir do Derby 10.7. Em versões anteriores, substitua `PAROU BOOLEAN` por `PAROU SMALLINT` (0 = falso, 1 = verdadeiro) e ajuste o `setBoolean` correspondente no código.

Para conferir se a tabela foi criada, ainda no `ij`:

```sql
DESCRIBE TB_EXECUCOES;
```

Para sair do `ij`:

```sql
EXIT;
```

### 4. Configurar as credenciais de conexão

A classe `MTgerador/Conexao.java` define a URL de conexão, o usuário e a senha do banco. Ajuste-os para as credenciais que você deseja usar no seu ambiente local:

```java
return DriverManager.getConnection(
    "jdbc:derby://localhost:1527/mturing", "USUARIO", "SENHA");
```

> **Importante.** Não versione credenciais reais em um repositório público. Substitua `USUARIO` e `SENHA` pelos valores do seu ambiente apenas localmente, ou externalize-os (por exemplo, via variáveis de ambiente ou um arquivo de configuração não incluído no controle de versão). Para uma instalação local simples de desenvolvimento, o Derby aceita conexões sem autenticação forte configurada — nesse caso os valores de usuário e senha servem apenas para identificar o esquema.

### 5. Configurar o projeto no NetBeans

1. Crie um projeto `MTgerador` no Apache NetBeans, e importe os arquivos-fonte.
2. Adicione o driver cliente do Derby ao *classpath* do projeto: o arquivo `derbyclient.jar`, encontrado em `DERBY_HOME/lib`. No NetBeans: clique com o botão direito no projeto → *Properties* → *Libraries* → *Add JAR/Folder* → selecione `derbyclient.jar`.
3. Certifique-se de que o servidor Derby (passo 2) está rodando e de que a tabela foi criada (passo 3).

### 6. Executar

Execute a classe `MTMain`. Ela percorre todas as máquinas, simula cada uma e grava os resultados em lotes de 50.000 registros na tabela `TB_EXECUCOES`. Ao final, a tabela conterá um registro por máquina (16.777.216 no total).

O método `Conexao.limpa()` é chamado no início da execução para esvaziar a tabela, de modo que execuções repetidas não acumulem registros duplicados.

---

## Estrutura da tabela TB_EXECUCOES

| Coluna     | Tipo     | Descrição                                                        |
|------------|----------|------------------------------------------------------------------|
| `ID`       | INTEGER  | Identificador da máquina (0 a 16.777.215)                        |
| `PAROU`    | BOOLEAN  | Se a máquina parou dentro do limite de 21 passos                |
| `PASSOS`   | SMALLINT | Número de passos executados                                     |
| `UNS`      | SMALLINT | Quantidade de 1s deixados na fita                               |
| `TAM_FITA` | SMALLINT | Tamanho final da fita                                           |

