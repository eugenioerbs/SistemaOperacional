# Relatório técnico — Simulador de Núcleo de Sistema Operacional

**Disciplina:** Sistemas Operacionais  
**Linguagem:** Java  
**Ambiente de desenvolvimento:** Visual Studio Code  
**Escopo:** implementação didática dos cinco módulos definidos no enunciado do projeto.

## 1. Introdução

Este projeto tem como objetivo demonstrar, por meio de simulações implementadas em Java, conceitos fundamentais de Sistemas Operacionais. O trabalho foi organizado em cinco módulos independentes: concorrência e sincronização; escalonamento de processos; memória virtual e paginação; detecção/prevenção de deadlocks; e sistema de arquivos simulado.

O sistema desenvolvido é executado em espaço de usuário. Portanto, as estruturas de memória, os recursos, as filas, os blocos e as operações de disco são representações didáticas; o programa não implementa um kernel real nem manipula diretamente a memória física ou os dispositivos de armazenamento do computador.

## 2. Objetivos

### 2.1 Objetivo geral

Implementar e demonstrar algoritmos e estruturas de dados associados ao funcionamento de um sistema operacional, tornando observáveis as decisões tomadas por cada mecanismo.

### 2.2 Objetivos específicos

- Demonstrar concorrência, exclusão mútua, semáforos e sincronização entre threads.
- Simular políticas de escalonamento e calcular tempo médio de espera e turnaround.
- Traduzir endereços virtuais, tratar faltas de página e comparar FIFO e LRU.
- Identificar ciclos em grafos de alocação e verificar estados seguros pelo algoritmo do banqueiro.
- Gerenciar arquivos em blocos simulados, mantendo consistentes FAT e bitmap de espaço livre.

## 3. Tecnologias e ferramentas

A linguagem escolhida foi **Java**, com o Visual Studio Code como ambiente de desenvolvimento e a extensão Java para compilar, executar e depurar as classes. As demonstrações utilizam APIs da biblioteca padrão, sem dependências externas específicas.

Entre as APIs utilizadas no Módulo 1 estão `Thread`, `ReentrantLock`, `Semaphore`, `ReentrantReadWriteLock` e `CyclicBarrier`. Nos demais módulos, são utilizados arrays, matrizes, listas, mapas e estruturas de controle implementadas diretamente no projeto.

A escolha de Java permitiu organizar as responsabilidades em classes, usar tipos para representar estados e resultados e concentrar o trabalho na lógica dos algoritmos sem gerenciamento manual de memória. O código foi executado a partir do VS Code; nos logs enviados, o lançamento do programa foi feito pela extensão Java da Red Hat, com runtime 25.0.4.1 indicado no caminho do executável.

## 4. Arquitetura e organização

O código está separado em pacotes `modulo1`, `modulo2`, `modulo3`, `modulo4` e `modulo5`. Cada módulo tem suas próprias classes e uma classe principal de demonstração. Essa organização reduz o acoplamento entre exercícios, facilita executar cada cenário isoladamente e permite discutir suas decisões de implementação separadamente.

A integração atual é principalmente organizacional: os módulos coexistem no mesmo projeto, mas não são coordenados por um único menu ou processo principal. As classes principais são:

| Módulo | Classe principal | Escopo |
|---|---|---|
| 1 | Classes de demonstração individuais | Threads e sincronização. |
| 2 | `MainModulo2` | Round-Robin e prioridades com aging. |
| 3 | `MainModulo3` | FIFO, LRU e persistência de página modificada no swap simulado. |
| 4 | `MainModulo4` | Algoritmo do banqueiro e grafo de alocação. |
| 5 | `MainModulo5` | Operações de arquivos com FAT e bitmap. |

## 5. Decisões de implementação por módulo

### 5.1 Módulo 1 — Concorrência e sincronização (30%)

O módulo contém seis demonstrações:

- `ThreadsDemo`: cria três threads, executa tarefas concorrentes e utiliza `join()` para aguardar o encerramento.
- `MutexDemo`: protege um contador compartilhado com `ReentrantLock`, demonstrando exclusão mútua.
- `ProdutorConsumidor`: utiliza um buffer circular, um lock para proteger os índices e semáforos para representar posições vazias e itens disponíveis.
- `JantarDosFilosofos`: representa os hashis como locks e aplica ordenação global de recursos para eliminar espera circular.
- `LeitoresEscritores`: utiliza `ReentrantReadWriteLock` para permitir leituras concorrentes e exigir acesso exclusivo para escrita.
- `BarreiraDemo`: usa `CyclicBarrier` para sincronizar três threads antes da segunda fase; essa classe é uma demonstração complementar.

A decisão de separar os problemas em classes pequenas facilita observar cada mecanismo. No jantar dos filósofos, a ordenação de recursos previne deadlock, mas não fornece garantia universal contra starvation. No problema de leitores/escritores, foi configurada a política justa do `ReentrantReadWriteLock`; a justiça depende das condições de execução e não deve ser descrita como uma garantia absoluta para todas as situações.

**Validação relatada:** foi informado que as seis classes foram criadas e testadas no VS Code e que funcionaram. A saída integral de cada classe não foi transcrita no registro atual. Os cenários foram configurados para produzir os seguintes resultados principais: contador final de 200.000; 20 itens produzidos e consumidos; cinco filósofos completando três rodadas cada; valor final 6 no problema de leitores/escritores; e três threads ultrapassando a barreira somente depois de todas chegarem.

### 5.2 Módulo 2 — Escalonamento de processos (20%)

As classes `Processo`, `FilaCircular`, `EscalonadorRoundRobin`, `EscalonadorPrioridadeAging` e `RelatorioEscalonamento` separam o modelo de processo, as filas, as políticas e os cálculos das métricas. `MainModulo2` cria quatro processos com chegadas, demandas de CPU e prioridades diferentes e executa os dois escalonadores sobre cópias independentes da configuração inicial.

No Round-Robin, o quantum utilizado no cenário é de 3 unidades de tempo. Um processo não finalizado que não se bloqueia ao final do quantum retorna ao final da fila. No escalonador de prioridades, números maiores representam prioridade mais alta; o aging aumenta gradualmente a prioridade efetiva dos processos que aguardam, com intervalo de 2 unidades. No momento em que um processo recebe CPU, sua prioridade efetiva retorna à base, como decisão específica desse modelo didático.

O processo simulado mantém chegada, CPU total e restante, estado, tempo de espera, instante de conclusão e um evento opcional de bloqueio por E/S. O turnaround é calculado como instante de conclusão menos instante de chegada. O tempo de espera contabiliza o tempo na fila de prontos, não o período em que o processo permanece bloqueado.

**Resultados observados:** `MainModulo2` executou e imprimiu os relatórios seguintes.

**Round-Robin — quantum 3**

| Processo | Espera | Instante de conclusão | Turnaround |
|---|---:|---:|---:|
| P1 | 13 | 22 | 22 |
| P2 | 10 | 15 | 14 |
| P3 | 8 | 18 | 16 |
| P4 | 12 | 21 | 18 |
| **Média** | **10,75 (TME)** | — | **17,50 (TMT)** |

**Prioridades com aging — intervalo 2**

| Processo | Espera | Instante de conclusão | Turnaround |
|---|---:|---:|---:|
| P1 | 11 | 20 | 20 |
| P2 | 0 | 5 | 4 |
| P3 | 12 | 22 | 20 |
| P4 | 7 | 16 | 13 |
| **Média** | **7,50 (TME)** | — | **14,25 (TMT)** |

Neste cenário específico, o algoritmo de prioridades com aging apresentou menores tempos médios. Essa observação não implica superioridade universal; o resultado depende da carga de trabalho e dos parâmetros usados.

### 5.3 Módulo 3 — Memória virtual e paginação (25%)

As classes `EntradaPagina`, `PoliticaSubstituicao`, `ResultadoAcesso` e `GerenciadorMemoria` representam entradas da tabela de páginas, políticas de substituição, resultados de tradução e o mecanismo central de memória. `MainModulo3` executa a mesma sequência de referências com FIFO e LRU.

A configuração demonstrativa contém oito páginas virtuais, quatro posições por página e três molduras físicas. Para um endereço virtual, a página é obtida por divisão inteira pelo tamanho da página, enquanto o deslocamento é obtido pelo resto da divisão. O endereço físico combina o índice da moldura com o deslocamento. Se a página não estiver presente, ocorre page fault e ela é carregada do swap simulado; se não houver moldura livre, FIFO ou LRU escolhe uma vítima.

A política FIFO escolhe a página carregada há mais tempo; LRU escolhe a que teve o acesso mais antigo. Quando uma página modificada é removida, seu conteúdo é copiado para a área de swap simulada, que é uma matriz Java, não um dispositivo ou arquivo real.

**Resultados observados:** a sequência de páginas referenciadas foi `7, 0, 1, 2, 0, 3, 0, 4, 2, 3, 0, 3, 2`, totalizando 13 acessos.

| Métrica | FIFO | LRU |
|---|---:|---:|
| Acessos | 13 | 13 |
| Page faults | 10 | 9 |
| Substituições após ocupação das molduras | 7 | 6 |
| Acertos | 3 | 4 |
| Taxa de acertos | 23,08% | 30,77% |

Na prova de escrita, foi armazenado o valor 999 no endereço virtual 9, correspondente à página 2 e deslocamento 1. Depois que a página foi substituída, o programa voltou a solicitá-la e recuperou o valor 999, indicando que o conteúdo modificado foi preservado no swap simulado e restaurado no novo carregamento.

### 5.4 Módulo 4 — Prevenção e tratamento de deadlocks (15%)

O módulo utiliza duas abordagens. `GrafoAlocacaoRecursos` representa arestas de solicitação (processo para recurso) e alocação (recurso para processo) e executa busca em profundidade para detectar ciclos. O cenário usa uma instância de cada tipo de recurso; com essa premissa, o ciclo encontrado indica deadlock. Para sistemas com múltiplas instâncias de um tipo de recurso, a interpretação de ciclos é mais complexa.

`GerenciadorBanqueiro` mantém os vetores de recursos disponíveis e as matrizes de alocação, máximo e necessidade, com `Necessidade = Máximo - Alocação`. Para avaliar um pedido, o método simula a concessão, verifica se há sequência segura e confirma o pedido somente se a segurança for mantida. Caso contrário, realiza rollback das quantidades alteradas.

**Resultados observados — algoritmo do banqueiro:**

- Recursos disponíveis no início, na ordem A, B e C: `[3, 3, 2]`.
- O algoritmo encontrou a sequência segura `[P1, P3, P0, P2, P4]`.
- Pedido de P1 por `[1, 0, 2]`: concedido; o estado permaneceu seguro e os disponíveis passaram a `[2, 3, 0]`.
- Pedido de P4 por `[3, 3, 0]`: rejeitado porque o estado resultante seria inseguro. O rollback restaurou os disponíveis para `[3, 3, 2]`, e a configuração original foi preservada.

**Resultados observados — grafo:** foram impressas as arestas `P0 -> R1`, `R1 -> P1`, `P1 -> R2`, `R2 -> P2`, `P2 -> R0` e `R0 -> P0`. O ciclo identificado foi `P0 -> R1 -> P1 -> R2 -> P2 -> R0 -> P0`; como há uma instância de cada tipo de recurso, o cenário caracteriza deadlock.

O relatório distingue estado inseguro de deadlock efetivamente identificado. Um estado inseguro não implica necessariamente que o sistema já esteja em deadlock.

### 5.5 Módulo 5 — Sistema de arquivos simulado (10%)

Foi escolhida a combinação FAT + bitmap. `ArquivoInfo` guarda nome, tamanho em bytes e bloco inicial; `SistemaArquivosSimulado` mantém o diretório, o conteúdo dos blocos, a tabela FAT e o bitmap. A FAT encadeia blocos de um mesmo arquivo até `EOF`; o bitmap marca blocos livres ou ocupados. A classe principal demonstra escrita/criação, leitura, atualização, exclusão e impressão das estruturas internas.

A simulação utiliza 12 blocos de 8 bytes, totalizando 96 bytes de capacidade nominal. Essa capacidade é dividida em blocos fixos, e cada arquivo pode ocupar vários blocos. A implementação verifica a disponibilidade de espaço antes de substituir o conteúdo de um arquivo existente.

**Resultados observados:**

| Operação | Resultado |
|---|---|
| Criação de `arquivoA.txt` | 21 bytes, cadeia `[0, 1, 2]`. |
| Criação de `arquivoB.txt` | 18 bytes, cadeia `[3, 4, 5]`. |
| Criação de `arquivoC.txt` | 9 bytes, cadeia `[6, 7]`. |
| Estado após criação | Blocos 0–7 ocupados; blocos 8–11 livres; 4 blocos livres. |
| Leitura de A | Conteúdo recuperado: `Conteudo do arquivo A`. |
| Atualização de B | Novo tamanho de 41 bytes; cadeia `[3, 4, 5, 8, 9, 10]`; bloco 11 permaneceu livre. |
| Exclusão de C | Blocos 6 e 7 liberados; 3 blocos livres ao final: 6, 7 e 11. |

As saídas registradas da FAT, do bitmap e do diretório são compatíveis com as operações demonstradas. O sistema não grava no disco real e seu estado é perdido quando a aplicação termina.

## 6. Análise geral dos resultados

As execuções fornecidas mostram que as classes principais dos Módulos 2, 3, 4 e 5 terminaram e produziram as saídas esperadas para seus cenários didáticos. No Módulo 2, ambos os algoritmos finalizaram os quatro processos e calcularam as métricas. No Módulo 3, LRU apresentou um page fault a menos do que FIFO na sequência escolhida e a demonstração recuperou o valor modificado após a substituição da página. No Módulo 4, o algoritmo do banqueiro aceitou o pedido seguro e reverteu o pedido que tornaria o estado inseguro; o grafo detectou o ciclo especificado. No Módulo 5, criação, leitura, atualização e exclusão mantiveram coerentes o encadeamento FAT e o mapa de espaço livre nos estados impressos.

Para o Módulo 1, foi relatado que todas as seis classes foram executadas com sucesso, mas não há um transcript completo da saída dessas execuções neste registro. Por isso, os resultados numéricos do Módulo 1 são apresentados como resultados previstos pelos cenários de demonstração, não como logs independentes anexados ao relatório.

Esses resultados confirmam os fluxos demonstrativos enviados, mas não substituem uma campanha de testes automatizados. Ainda são recomendáveis testes de fronteira, entradas inválidas, interrupções, falhas de alocação e compilação conjunta de todos os fontes em uma execução limpa.

## 7. Limitações e decisões que devem ser consideradas

1. **Módulos independentes:** ainda não existe um menu ou controlador comum aos cinco módulos.
2. **Concorrência:** os exercícios demonstram mecanismos específicos; não implementam escalonamento real de threads do sistema operacional. Justiça de locks e prevenção de deadlock são propriedades diferentes.
3. **Escalonamento:** o tempo é discreto e os resultados dependem dos parâmetros; aging reinicia para prioridade-base quando o processo recebe CPU, conforme a política escolhida.
4. **Memória virtual:** tabelas, molduras e swap são arrays em Java. Não há interação com uma MMU física ou armazenamento real.
5. **Deadlocks:** o grafo demonstrativo restringe cada tipo de recurso a uma instância. O algoritmo do banqueiro pressupõe que os máximos declarados sejam válidos e conhecidos.
6. **Sistema de arquivos:** a FAT, o bitmap, o diretório e os blocos são voláteis. Não há hierarquia de diretórios, permissões, persistência nem recuperação após desligamento.
7. **Testes:** os resultados relatados correspondem aos cenários executados e compartilhados. Ainda não foi apresentada evidência de testes automatizados para todos os casos-limite.

## 8. Instruções de execução

No VS Code, abra a pasta principal do projeto, localize a classe principal do módulo desejado e pressione **Run**. Os programas principais são:

- Módulo 1: execute individualmente as classes `ThreadsDemo`, `MutexDemo`, `ProdutorConsumidor`, `JantarDosFilosofos`, `LeitoresEscritores` e `BarreiraDemo`.
- Módulo 2: `modulo2.MainModulo2`.
- Módulo 3: `modulo3.MainModulo3`.
- Módulo 4: `modulo4.MainModulo4`.
- Módulo 5: `modulo5.MainModulo5`.

Como alternativa, se os fontes estiverem em `src/moduloN`, é possível compilar todos os arquivos a partir da raiz com PowerShell:

```powershell
New-Item -ItemType Directory -Force out | Out-Null
$arquivos = Get-ChildItem -Path .\src -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -encoding UTF-8 -d .\out $arquivos
```

Se a compilação terminar sem erros, execute a classe de interesse com `java -cp .\out moduloN.NomeDaClasse`. Os caminhos devem corresponder à estrutura real dos diretórios e às declarações `package`.

## 9. Conclusão

O projeto reúne demonstrações dos cinco temas propostos, com estruturas e algoritmos implementados explicitamente para permitir observar sua lógica. Os resultados registrados confirmam a execução bem-sucedida dos cenários apresentados para os Módulos 2, 3, 4 e 5, além do relato de funcionamento das classes do Módulo 1. A arquitetura separada por pacotes facilita o entendimento e a apresentação do trabalho.

Como evolução, recomenda-se consolidar testes automatizados, revisar situações de erro e, se desejado, criar um menu principal para executar os módulos em uma única aplicação. As limitações das simulações devem ser explicitadas durante a apresentação para diferenciar o modelo acadêmico de um núcleo real de sistema operacional.
