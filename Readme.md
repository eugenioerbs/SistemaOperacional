# Simulador de Núcleo de Sistema Operacional

Projeto acadêmico da disciplina de **Sistemas Operacionais**. O sistema reúne cinco módulos didáticos desenvolvidos em Java para demonstrar mecanismos de concorrência, escalonamento de processos, gerenciamento de memória virtual, tratamento de deadlocks e organização de um sistema de arquivos.

> **Escopo:** trata-se de um simulador executado em espaço de usuário. Não é um kernel real e não controla diretamente o hardware, a memória física ou o disco do computador.

## Sumário

- [Tecnologias e ferramentas](#tecnologias-e-ferramentas)
- [Organização do projeto](#organização-do-projeto)
- [Módulos implementados](#módulos-implementados)
- [Como executar no VS Code](#como-executar-no-vs-code)
- [Resultados observados](#resultados-observados)
- [Limitações conhecidas](#limitações-conhecidas)
- [Próximas melhorias](#próximas-melhorias)

## Tecnologias e ferramentas

- **Linguagem:** Java.
- **Editor:** Visual Studio Code.
- **Execução:** extensão Java para VS Code e runtime Java configurado no ambiente.
- **Bibliotecas externas:** não são necessárias para as demonstrações descritas; são utilizadas APIs e coleções da biblioteca padrão do Java.
- **Versão recomendada:** JDK 17 ou superior.

## Organização do projeto

A estrutura prevista para os fontes é:

```text
SistemaOperacional/
├── README.md
├── RELATORIO.md
└── src/
    ├── modulo1/
    ├── modulo2/
    ├── modulo3/
    ├── modulo4/
    └── modulo5/
```

Cada pacote contém classes específicas e uma classe principal para executar sua demonstração. Os módulos podem ser executados independentemente. **Ainda não existe um programa central que integre as cinco simulações em um único fluxo de execução.**

## Módulos implementados

### Módulo 1 — Concorrência e sincronização (30%)

Demonstra criação de threads, exclusão mútua, semáforos, coordenação de tarefas e sincronização por barreira.

| Classe | Responsabilidade |
|---|---|
| `ThreadsDemo` | Cria três threads, executa tarefas e aguarda seu encerramento. |
| `MutexDemo` | Protege um contador compartilhado com `ReentrantLock`. |
| `ProdutorConsumidor` | Simula buffer circular com mutex e semáforos contadores para posições vazias e itens disponíveis. |
| `JantarDosFilosofos` | Simula a disputa por hashis e usa ordenação de recursos para prevenir deadlock. |
| `LeitoresEscritores` | Permite leituras concorrentes e escrita exclusiva com `ReentrantReadWriteLock`. |
| `BarreiraDemo` | Demonstra sincronização entre fases com `CyclicBarrier` (complemento didático). |

A estratégia dos filósofos impede espera circular pela ordenação consistente dos recursos. A configuração justa dos bloqueios pode favorecer a ordem de atendimento, mas não equivale a uma garantia universal de ausência de starvation.

### Módulo 2 — Escalonamento de processos (20%)

Simula escalonamento de CPU com Round-Robin e prioridades com envelhecimento (*aging*), além do cálculo do tempo médio de espera (TME) e do tempo médio de turnaround (TMT).

| Classe | Responsabilidade |
|---|---|
| `Processo` | Guarda chegada, demanda de CPU, prioridade, estado e métricas. Permite um evento opcional de bloqueio por E/S. |
| `FilaCircular` | Implementa a fila circular usada pelo Round-Robin. |
| `RelatorioEscalonamento` | Calcula e exibe espera, conclusão, turnaround e médias. |
| `EscalonadorRoundRobin` | Distribui CPU em fatias limitadas pelo quantum, com preempção. |
| `EscalonadorPrioridadeAging` | Escolhe a maior prioridade efetiva e envelhece processos que esperam. |
| `MainModulo2` | Cria os processos de demonstração e executa os dois algoritmos. |

Decisões do modelo: números maiores significam prioridade mais alta; o Round-Robin utiliza quantum 3 no cenário demonstrado; o escalonador com aging utiliza intervalo 2; a prioridade efetiva retorna à prioridade base quando o processo recebe CPU. O modelo de processo simula um evento opcional de bloqueio por E/S.

### Módulo 3 — Memória virtual e paginação (25%)

Simula a tradução de endereços virtuais para físicos por meio de uma tabela de páginas, page faults, substituição de páginas e armazenamento de apoio (*swap*) simulado.

| Classe | Responsabilidade |
|---|---|
| `PoliticaSubstituicao` | Define as políticas FIFO e LRU. |
| `EntradaPagina` | Registra presença, moldura, instante de carga, último acesso e modificação. |
| `ResultadoAcesso` | Registra o resultado de uma leitura ou escrita e sua tradução de endereço. |
| `GerenciadorMemoria` | Mantém tabela de páginas, molduras, memória simulada, swap e métricas. |
| `MainModulo3` | Executa a sequência de referências para FIFO e LRU e demonstra persistência de página modificada. |

Parâmetros do cenário: 8 páginas virtuais, 4 posições por página e 3 molduras físicas. A área de swap é uma matriz na memória do programa; não é um arquivo de swap real.

### Módulo 4 — Deadlocks (15%)

Contém duas abordagens complementares para dependências e segurança de recursos.

| Classe | Responsabilidade |
|---|---|
| `GrafoAlocacaoRecursos` | Representa arestas de solicitação e alocação e procura ciclos por busca em profundidade (DFS). |
| `GerenciadorBanqueiro` | Mantém recursos disponíveis, alocação, máximo e necessidade; avalia estados seguros e decide sobre solicitações. |
| `MainModulo4` | Demonstra um estado seguro, pedidos de recursos e um ciclo no grafo. |

O grafo demonstrativo permite uma única instância de cada tipo de recurso; nessa condição, um ciclo indica deadlock. No algoritmo do banqueiro, um estado inseguro significa que não foi encontrada uma sequência segura com os dados informados, não que exista necessariamente deadlock naquele instante.

### Módulo 5 — Sistema de arquivos simulado (10%)

Simula operações de arquivos com FAT, blocos de dados e bitmap de espaço livre.

| Classe | Responsabilidade |
|---|---|
| `ArquivoInfo` | Mantém nome, tamanho em bytes e bloco inicial. |
| `SistemaArquivosSimulado` | Implementa gravação, leitura, atualização, exclusão, encadeamento FAT, alocação/liberação de blocos e bitmap. |
| `MainModulo5` | Executa uma demonstração de criação, leitura, atualização, exclusão e inspeção das estruturas. |

O cenário usa 12 blocos de 8 bytes. Os blocos, a FAT, o bitmap e o diretório são estruturas em memória; não há persistência em disco real.

## Como executar no VS Code

1. Abra a pasta raiz `SistemaOperacional` no VS Code.
2. Confirme que a extensão Java está instalada e que um JDK compatível está configurado.
3. Abra a classe principal do módulo desejado.
4. Clique em **Run** acima do método `main`.

Classes principais e demonstrações do Módulo 1:

| Módulo | Classe a executar |
|---|---|
| 1 | `modulo1.ThreadsDemo`, `modulo1.MutexDemo`, `modulo1.ProdutorConsumidor`, `modulo1.JantarDosFilosofos`, `modulo1.LeitoresEscritores`, `modulo1.BarreiraDemo` |
| 2 | `modulo2.MainModulo2` |
| 3 | `modulo3.MainModulo3` |
| 4 | `modulo4.MainModulo4` |
| 5 | `modulo5.MainModulo5` |

### Compilação pelo PowerShell (opcional)

Se os arquivos estiverem em `src/modulo1` até `src/modulo5`, execute a partir da raiz do projeto:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$arquivos = Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d .\out $arquivos
```

Depois de uma compilação bem-sucedida, execute os programas, um de cada vez:

```powershell
java -cp .\out modulo1.ThreadsDemo
java -cp .\out modulo1.MutexDemo
java -cp .\out modulo1.ProdutorConsumidor
java -cp .\out modulo1.JantarDosFilosofos
java -cp .\out modulo1.LeitoresEscritores
java -cp .\out modulo1.BarreiraDemo
java -cp .\out modulo2.MainModulo2
java -cp .\out modulo3.MainModulo3
java -cp .\out modulo4.MainModulo4
java -cp .\out modulo5.MainModulo5
```

Esses comandos pressupõem que a estrutura de diretórios corresponda às declarações `package`. Se os fontes não estiverem dentro de `src`, ajuste o caminho passado a `Get-ChildItem`. Como alternativa, continue usando o botão **Run** do VS Code, que foi utilizado nas execuções registradas.

## Resultados observados

Os valores abaixo foram transcritos das saídas fornecidas durante a implementação. Os cenários de teste são didáticos e não representam uma medição de um sistema operacional real.

### Módulo 1

Foi informado que as seis classes foram executadas e funcionaram. Os programas foram configurados para demonstrar os seguintes resultados: três threads concluídas; contador protegido por mutex chegando a 200.000; 20 itens produzidos e consumidos; cinco filósofos completando três rodadas cada; valor final 6 no cenário leitores/escritores; e três threads sincronizadas pela barreira. Não foi fornecido um log completo consolidado dessas seis execuções.

### Módulo 2

`MainModulo2` foi executado com quatro processos (P1–P4), quantum 3 no Round-Robin e intervalo de aging 2 no escalonador por prioridades.

**Round-Robin**

| Processo | Tempo de espera | Instante de conclusão | Turnaround |
|---|---:|---:|---:|
| P1 | 13 | 22 | 22 |
| P2 | 10 | 15 | 14 |
| P3 | 8 | 18 | 16 |
| P4 | 12 | 21 | 18 |
| **Média** | **10,75 (TME)** | — | **17,50 (TMT)** |

**Prioridades com aging**

| Processo | Tempo de espera | Instante de conclusão | Turnaround |
|---|---:|---:|---:|
| P1 | 11 | 20 | 20 |
| P2 | 0 | 5 | 4 |
| P3 | 12 | 22 | 20 |
| P4 | 7 | 16 | 13 |
| **Média** | **7,50 (TME)** | — | **14,25 (TMT)** |

Para esse conjunto específico de entradas, o cenário com prioridades e aging apresentou TME e TMT menores. Isso não demonstra que esse algoritmo sempre supera Round-Robin; os resultados dependem da carga de processos e das regras adotadas.

### Módulo 3

A sequência de referência usou 13 acessos às páginas virtuais `7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2`, com três molduras físicas.

| Métrica | FIFO | LRU |
|---|---:|---:|
| Acessos | 13 | 13 |
| Page faults | 10 | 9 |
| Substituições de páginas já carregadas | 7 | 6 |
| Acertos (hits) | 3 | 4 |
| Taxa de acertos | 23,08% | 30,77% |

Na demonstração adicional de escrita, o valor `999` foi gravado no endereço virtual 9 (página 2, deslocamento 1). A página foi removida da RAM, seu conteúdo foi preservado no swap simulado e o valor `999` foi recuperado quando ela foi carregada novamente.

### Módulo 4

**Algoritmo do banqueiro:** o estado inicial tinha recursos disponíveis `[3, 3, 2]`, na ordem `[A, B, C]`, e foi considerado seguro. Uma sequência segura encontrada foi `[P1, P3, P0, P2, P4]`.

- Solicitação de P1 por `[1, 0, 2]`: concedida; o estado resultante continuou seguro, com recursos disponíveis `[2, 3, 0]`.
- Solicitação de P4 por `[3, 3, 0]`: rejeitada porque o estado resultante seria inseguro. A operação foi revertida e os recursos disponíveis voltaram a `[3, 3, 2]`.

**Grafo de alocação:** o programa exibiu as arestas `P0 -> R1`, `R1 -> P1`, `P1 -> R2`, `R2 -> P2`, `P2 -> R0` e `R0 -> P0`, detectando o ciclo `P0 -> R1 -> P1 -> R2 -> P2 -> R0 -> P0`. Como o cenário usa uma instância por tipo de recurso, o ciclo indica deadlock.

### Módulo 5

A demonstração utilizou 12 blocos de 8 bytes cada.

| Operação | Resultado observado |
|---|---|
| Criação de `arquivoA.txt` | 21 bytes; cadeia `[0, 1, 2]`. |
| Criação de `arquivoB.txt` | 18 bytes; cadeia `[3, 4, 5]`. |
| Criação de `arquivoC.txt` | 9 bytes; cadeia `[6, 7]`. |
| Estado após as três criações | 8 blocos ocupados e 4 livres (8–11). |
| Leitura de A | Retornou `Conteudo do arquivo A`. |
| Atualização de B | Conteúdo atualizado com 41 bytes; cadeia `[3, 4, 5, 8, 9, 10]`; restou 1 bloco livre (11). |
| Exclusão de C | Blocos 6 e 7 liberados; restaram 3 blocos livres (6, 7 e 11). |

O diretório, a FAT e o bitmap apresentados no terminal são coerentes com as operações dessa execução.

## Limitações conhecidas

- As simulações são independentes e não estão integradas em uma interface única.
- O Módulo 1 demonstra sincronização, mas não é um núcleo de sistema operacional.
- O Módulo 2 trabalha com unidades discretas de tempo, prioridades configuradas e um evento opcional de bloqueio por processo.
- No Módulo 3, RAM e swap são matrizes em memória; a simulação não conversa com hardware de MMU nem com um arquivo real de swap.
- No Módulo 4, o grafo assume uma instância por tipo de recurso; o banqueiro depende das necessidades máximas declaradas.
- No Módulo 5, FAT, bitmap e blocos são voláteis e não persistem após o fim do programa.
- Os resultados registrados cobrem os cenários demonstrativos enviados; ainda é recomendável realizar testes sistemáticos de casos-limite e entradas inválidas.

## Próximas melhorias

- Reunir as simulações em um menu principal opcional.
- Acrescentar testes automatizados e casos de borda.
- Revisar o tratamento de exceções, interrupções e recuperação de falhas.
- Documentar a política de prioridade/aging e as garantias de justiça da concorrência com mais rigor.
- Avaliar persistência em arquivo para o sistema de arquivos simulado, caso isso seja solicitado pelo professor.
