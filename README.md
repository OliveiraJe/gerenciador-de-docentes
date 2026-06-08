# Gerenciador de Docentes – FATEC ZL
- Desenvolvedores: Henrique de Oliveira - Igor Melo - Jefferson Oliveira
- Disciplina: Estrutura de Dados
- Prof.: Leandro Colevati

---

🎓# Sistema de Chamada Pública para Contratação de Docentes

Este projeto consiste em um sistema interno para gerenciamento de chamadas públicas de contratação temporária de professores em uma faculdade. O sistema foi desenvolvido com foco na aplicação prática de Estruturas de Dados clássicas e Persistência de Dados em arquivos CSV.

🚀# Funcionalidades Principais

Acesso Restrito: Sistema de uso exclusivo para funcionários da instituição;<br>
Persistência Automatizada: Todas as operações são feitas via sistema (sem manipulação direta nos arquivos CSV);
Gerenciamento Completo (CRUD): Telas dedicadas para Cadastro, Consulta, Atualização e Remoção de:
- Cursos (cursos.csv)
- Disciplinas (disciplinas.csv)
- Professores (professor.csv)
- Inscrições (inscricoes.csv)
Integridade de Dados: A exclusão de uma disciplina remove automaticamente todas as suas inscrições vinculadas.

📁 Estrutura do Projeto (3 camadas)

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
