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

O projeto foi desenvolvido no **Apache NetBeans** e usa o **Apache Derby** como banco de dados, acessado em modo cliente/servidor (Derby Network Server) via JDBC. Dentro do NetBeans, o Derby aparece com o nome **"Java DB"**. Todo o preparo do banco descrito abaixo é feito pela própria IDE.

---

## Preparação do banco de dados (tudo pelo NetBeans)

### 1. Instalar o Apache Derby

Antes de mais nada, é preciso ter o Apache Derby instalado, pois o NetBeans precisa saber onde encontrá-lo.

1. Baixe a distribuição binária do Apache Derby em <https://db.apache.org/derby/derby_downloads.html>. Recomenda-se a versão 10.14 ou superior (qualquer versão >= 10.7 suporta o tipo `BOOLEAN` usado no projeto).
2. Descompacte o arquivo em uma pasta de sua escolha — por exemplo `C:\\Apache\\db-derby` no Windows, ou `~/apache/db-derby` no Linux. Essa pasta contém as subpastas `lib`, `bin`, etc.

> **Observação.** Versões antigas do NetBeans (quando ainda distribuído pela Oracle) já vinham com o Java DB embutido, junto ao GlassFish. Nas versões atuais do **Apache** NetBeans, pode ser necessário instalar o Derby separadamente, como descrito acima, e apontar o caminho para a IDE (passo 2).

### 2. Registrar a instalação do Derby no NetBeans

1. Abra o NetBeans e vá até a aba **Services** (menu *Window → Services*, ou `Ctrl+5`).
2. Expanda o nó **Databases**. Dentro dele deve aparecer o nó **Java DB**.
3. Clique com o botão direito em **Java DB** e escolha **Properties**.
4. No campo **Java DB Installation**, informe o caminho da pasta onde você descompactou o Derby (a pasta que contém a subpasta `lib`). Confirme com **OK**.

### 3. Iniciar o servidor Java DB

Ainda na aba **Services**, clique com o botão direito no nó **Java DB** e escolha **Start Server**.

Uma aba chamada *Java DB Database Process* abrirá na janela de saída (Output), exibindo uma mensagem semelhante a:

```
Apache Derby Network Server - 10.x.x.x started and ready to accept connections on port 1527
```

A porta **1527** é a porta padrão e é exatamente a que o código do projeto espera (`jdbc:derby://localhost:1527/`). O servidor precisa permanecer iniciado enquanto o programa Java for executado.

### 4. Criar o banco de dados `mturing`

1. No menu principal, escolha **Tools → Java DB Database → Create Database…**
2. Na caixa de diálogo *Create Java DB Database*, preencha:
   - **Database Name:** `mturing`
   - **User Name:** um usuário à sua escolha (por exemplo, `mturing`)
   - **Password:** uma senha à sua escolha 
3. Confirme com **OK**.

O NetBeans cria o banco e, automaticamente, uma conexão para ele. Anote o usuário e a senha escolhidos: eles precisarão coincidir com os que constam na classe `Conexao.java` (veja o passo 7).

### 5. Conectar-se ao banco

Na aba **Services**, dentro de **Databases**, localize o nó da conexão:

```
jdbc:derby://localhost:1527/mturing
```

Se o ícone estiver "quebrado" (desconectado), clique com o botão direito nele e escolha **Connect**. Quando conectado, o ícone fica inteiro e o nó pode ser expandido, revelando as subpastas **Tables**, **Views** e **Procedures**.

### 6. Criar a tabela TB_EXECUCOES

1. Expanda o nó da conexão `mturing`, clique com o botão direito na subpasta **Tables** e escolha **Execute Command…** (abre um editor SQL já conectado ao banco).
2. Cole o script abaixo no editor e execute-o (botão *Run SQL*, ou `Ctrl+Shift+E`):

```sql
CREATE TABLE TB_EXECUCOES (
    ID       INTEGER  NOT NULL PRIMARY KEY,
    PAROU    BOOLEAN,
    PASSOS   SMALLINT,
    UNS      SMALLINT,
    TAM_FITA SMALLINT
);
```

3. Para conferir, clique com o botão direito no nó **Tables** e escolha **Refresh**. A tabela `TB_EXECUCOES` deve aparecer na árvore.

> **Nota sobre os tipos.** O código Java grava os cinco campos com `setInt`/`setBoolean`. O `ID` varia de 0 a 16.777.215, cabendo em `INTEGER`. Os demais campos (`PASSOS`, `UNS`, `TAM_FITA`) assumem valores pequenos no universo de 3 estados (limitados por S(3) = 21 e vizinhança), cabendo em `SMALLINT`. Se preferir uniformizar, pode declarar todos como `INTEGER` sem qualquer prejuízo.
>
> **Versões antigas do Derby.** O tipo `BOOLEAN` em `CREATE TABLE` só é suportado a partir do Derby 10.7. Em versões anteriores, substitua `PAROU BOOLEAN` por `PAROU SMALLINT` (0 = falso, 1 = verdadeiro) e ajuste o `setBoolean` correspondente no código.

### 7. Ajustar as credenciais no código

Abra a classe `MTgerador/Conexao.java` e verifique se a URL, o usuário e a senha coincidem com o banco que você criou:

```java
return DriverManager.getConnection(
    "jdbc:derby://localhost:1527/mturing", "USUARIO", "SENHA");
```

Substitua `USUARIO` e `SENHA` pelos valores que você definiu no passo 4.

> **Importante.** Não versione credenciais reais em um repositório público. Ajuste esses valores apenas no seu ambiente local, ou externalize-os (por exemplo, via variáveis de ambiente).

### 8. Adicionar o driver do Derby ao projeto

O programa usa o driver **cliente** do Derby (`org.apache.derby.jdbc.ClientDriver`). É preciso incluí-lo no *classpath* do projeto:

1. Clique com o botão direito no projeto `MTgerador` na aba **Projects** e escolha **Properties**.
2. Selecione a categoria **Libraries**.
3. Clique em **Add JAR/Folder** e selecione o arquivo `derbyclient.jar`, localizado na subpasta `lib` da instalação do Derby.
4. Confirme com **OK**.

### 9. Executar

Com o servidor iniciado (passo 3) e a tabela criada (passo 6), execute a classe `MTMain` (botão direito sobre ela → **Run File**, ou `Shift+F6`).

O programa percorre todas as máquinas, simula cada uma e grava os resultados em lotes de 50.000 registros na tabela `TB_EXECUCOES`. Ao final, a tabela conterá um registro por máquina (16.777.216 no total). O método `Conexao.limpa()`, chamado no início da execução, esvazia a tabela antes de começar, evitando duplicatas em execuções repetidas.

Para inspecionar os resultados, na aba **Services**, clique com o botão direito na tabela `TB_EXECUCOES` e escolha **View Data**.

---

## Estrutura da tabela TB_EXECUCOES

| Coluna     | Tipo     | Descrição                                             |
|------------|----------|-------------------------------------------------------|
| `ID`       | INTEGER  | Identificador da máquina (0 a 16.777.215)            |
| `PAROU`    | BOOLEAN  | Se a máquina parou dentro do limite de 21 passos    |
| `PASSOS`   | SMALLINT | Número de passos executados                         |
| `UNS`      | SMALLINT | Quantidade de 1s deixados na fita                   |
| `TAM_FITA` | SMALLINT | Tamanho final da fita                               |
