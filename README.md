# Gerenciador de Docentes – FATEC ZL
**Disciplina:** Estrutura de Dados | **Prof.:** Leandro Colevá

---

## Estrutura do Projeto (3 camadas)

```
gerenciadordedocentes/
├── src/
│   ├── Main.java                          ← Ponto de entrada
│   ├── model/                             ← Camada de Modelo / Estruturas de Dados
│   │   ├── Cursos.java
│   │   ├── Disciplina.java
│   │   ├── Professor.java
│   │   ├── Inscricoes.java
│   │   ├── Lista.java                     ← Lista Encadeada Simples genérica
│   │   ├── Fila.java                      ← Fila genérica (FIFO)
│   │   └── TabelaHash.java                ← Hash Table com função hash customizada
│   ├── controller/                        ← Camada de Controle (regras de negócio)
│   │   ├── CursosController.java
│   │   ├── DisciplinaController.java
│   │   ├── ProfessorController.java
│   │   └── InscricoesController.java
│   ├── view/                              ← Camada de Visão (Java Swing)
│   │   ├── MainWindow.java                ← Janela principal com menu
│   │   ├── CoursesView.java               ← CRUD Cursos
│   │   ├── ClassesView.java               ← CRUD Disciplinas
│   │   ├── TeachersView.java              ← CRUD Professores
│   │   ├── SubscriptionsView.java         ← CRUD Inscrições
│   │   ├── InscritosView.java             ← Consulta inscritos por disciplina
│   │   └── ProcessosAbertosView.java      ← Disciplinas com processos abertos (Hash)
│   └── main/resource/data/               ← Arquivos CSV
│       ├── cursos.csv
│       ├── disciplinas.csv
│       ├── professor.csv
│       └── inscricoes.csv
├── .classpath
└── .project
```

---

## Como importar no Eclipse

1. Abra o Eclipse
2. **File → Import → Existing Projects into Workspace**
3. Selecione a pasta `gerenciadordedocentes`
4. Clique em **Finish**
5. Clique com botão direito em `src/Main.java` → **Run As → Java Application**

> ⚠️ **Atenção:** O projeto deve ser executado a partir da raiz do projeto Eclipse  
> (o diretório de trabalho padrão), pois os arquivos CSV usam caminho relativo  
> `src/main/resource/data/`.  
> Para garantir isso: **Run Configurations → Arguments → Working Directory → Other →** selecione a pasta do projeto.

---

## Estruturas de Dados implementadas

| Estrutura | Onde é usada |
|-----------|-------------|
| **Lista Encadeada Simples** (`Lista<T>`) | Operações de UPDATE e DELETE em todos os CRUDs |
| **Fila (FIFO)** (`Fila<T>`) | Consulta de Cursos, Disciplinas e Professores |
| **Tabela Hash** (`TabelaHash`) | Consulta de disciplinas com processos abertos |
| **Insertion Sort** | Ordenação de inscritos por pontuação (decrescente) |

### Função Hash customizada (TabelaHash.java)
```java
private int hash(String chave) {
    int h = 0;
    for (int i = 0; i < chave.length(); i++) {
        h = (31 * h + chave.charAt(i)) % TAMANHO;
    }
    return Math.abs(h) % TAMANHO; // TAMANHO = 101 (número primo)
}
```
- Soma ponderada dos caracteres com multiplicador 31
- Tamanho da tabela: 101 (número primo para melhor distribuição)
- Colisões resolvidas por encadeamento (chaining)

---

## Regras de negócio implementadas

- Inserções **somente via sistema** (nunca diretamente no CSV)
- Ao remover uma disciplina, todas as inscrições associadas são excluídas em cascata
- Não há linhas vazias nos arquivos CSV após operações de remoção
- Consulta de inscritos ordenada por pontuação via **Insertion Sort** (sem classes internas do Java)
- Tabela Hash populada dinamicamente a partir das inscrições ativas
