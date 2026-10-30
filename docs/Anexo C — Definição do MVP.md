# Anexo C — Definição do MVP

## Problema que queremos resolver

Durante a graduação, temos dificuldade para manter as atividades complementares e seus comprovantes organizados e para saber quantas horas já registramos e quantas ainda faltam. A pesquisa inicial também mostrou que a dúvida sobre a aceitação das atividades e a carga horária válida é uma preocupação importante.

## Público e usuário principal

Estudantes da UCSal que precisam acompanhar as horas complementares exigidas em seu curso. No MVP, cada instalação será usada localmente por um estudante, sem conta ou sincronização entre dispositivos.

## Proposta de valor

Reunir em um só lugar o registro das atividades complementares e seus comprovantes e mostrar, com base nas metas configuradas pelo estudante, as horas registradas e as que ainda faltam por categoria.

## Fluxo principal

1. Criamos as categorias necessárias e informamos a meta de horas de cada uma.
2. Registramos uma atividade, preenchendo os dados manualmente ou usando sugestões extraídas de um comprovante.
3. Anexamos o comprovante digital, se já o tivermos.
4. Conferimos e corrigimos as informações sugeridas antes de salvar a atividade.
5. Consultamos o total de horas registradas, o progresso e as horas restantes de cada categoria.
6. Se necessário, corrigimos ou removemos um registro e conferimos os totais atualizados.

## O que fará parte do MVP

- Aplicação desktop em Java, executada localmente.
- Cadastro e edição de categorias e suas metas de horas.
- Cadastro, consulta, edição e remoção de atividades.
- Associação opcional de comprovante PDF ou imagem a uma atividade.
- Extração de texto de PDFs pesquisáveis (com texto selecionável) para sugerir título, data e carga horária da atividade, quando essas informações forem encontradas.
- Revisão e confirmação manual das sugestões antes de salvar. O estudante poderá corrigir os dados ou preencher a atividade manualmente se a extração não encontrar informações úteis.
- Persistência local de categorias, atividades e referências aos comprovantes, para que os dados continuem disponíveis após fechar e abrir o programa.
- Painel com horas registradas no total e por categoria, meta, horas restantes e progresso percentual. As horas restantes não serão exibidas como número negativo quando a meta for atingida.
- Dados fictícios para a demonstração, sem informações pessoais ou documentos reais.

As categorias e metas serão informadas pelo estudante. Não vamos presumir que uma configuração de demonstração corresponde às regras oficiais de qualquer curso.

## Critérios para considerar o MVP pronto

- Conseguimos criar e alterar uma categoria e sua meta.
- O sistema não aceita categoria sem nome ou com meta menor ou igual a zero.
- Conseguimos registrar uma atividade válida e associá-la a uma categoria existente.
- O sistema não aceita atividade sem título, data, categoria ou carga horária positiva.
- Conseguimos editar e remover uma atividade, e os totais são recalculados corretamente após cada operação.
- As horas registradas por categoria correspondem à soma das atividades registradas naquela categoria.
- As horas restantes correspondem a `máximo entre zero e (meta da categoria - horas registradas)`.
- O progresso por categoria é calculado como `mínimo entre (horas registradas / meta) × 100 e 100%`.
- Conseguimos anexar e abrir um comprovante local, e a tela indica quando não há comprovante anexado.
- Ao selecionar um PDF com texto selecionável, o sistema sugere título, data e carga horária quando conseguir identificá-los, e permite revisar e corrigir cada campo antes de salvar.
- Se o PDF não tiver texto selecionável ou não contiver informações reconhecíveis, o sistema informa que não conseguiu sugerir os dados e permite continuar o preenchimento manual.
- A categoria é escolhida pelo estudante; uma sugestão extraída nunca é apresentada como confirmação de que a atividade será aceita pela coordenação.
- Categorias, atividades e comprovantes continuam disponíveis depois de encerrar e iniciar novamente a aplicação.
- Uma pessoa da equipe consegue executar o fluxo completo usando apenas as instruções do README e os dados fictícios.
- Os testes cobrem pelo menos os cálculos de horas restantes e progresso, além das validações de entrada do fluxo principal.

## Fora do escopo desta versão

- Confirmar, prever ou garantir a aprovação de uma atividade pela coordenação.
- Substituir as regras oficiais do curso ou enviar atividades a sistemas institucionais.
- Contas de usuário, sincronização ou armazenamento em nuvem.
- Notificações e alertas automáticos.
- Exportação de relatório para submissão.
- OCR para imagens ou PDFs digitalizados sem texto selecionável.
- Extração automática de categoria ou aprovação da atividade pela coordenação.
- Preenchimento automático sem revisão e confirmação do estudante.

Esses itens poderão ser considerados depois de conversarmos com estudantes e confirmarmos as regras e necessidades envolvidas.

## Limitações e cuidados

- O sistema organiza os registros do estudante; não é um sistema oficial da UCSal nem valida a aceitação das atividades.
- As metas precisam ser conferidas pelo próprio estudante com uma fonte oficial do seu curso.
- Os dados e comprovantes ficam no computador em que a aplicação for usada; esta versão não oferece cópia de segurança automática nem sincronização.
- A extração depende da qualidade e da estrutura do texto do PDF. As sugestões podem estar incompletas ou incorretas e sempre precisam ser conferidas pelo estudante.
- Na demonstração, usaremos somente dados e documentos fictícios.

## Decisões técnicas iniciais

- Interface desktop feita com Java Swing, para manter a aplicação executável localmente sem servidor.
- Banco SQLite local para persistir categorias e atividades.
- Biblioteca Java de leitura de PDF para extrair texto de PDFs pesquisáveis; a biblioteca e sua licença serão registradas na declaração de componentes de terceiros.
- Comprovantes armazenados como arquivos locais, com a localização associada ao registro da atividade.
- O arquivo README será atualizado com os passos de instalação, execução, teste e demonstração quando a implementação estiver pronta.

## Como vamos validar a solução

Vamos pedir a um estudante para configurar uma categoria, registrar uma atividade usando um PDF de demonstração com texto selecionável, conferir as sugestões extraídas e localizar as horas registradas e restantes sem ajuda da equipe. Também verificaremos o comportamento com um PDF sem texto selecionável e confirmaremos que o preenchimento manual continua disponível. Vamos anotar dúvidas, erros e sugestões e usar esse retorno para decidir os próximos ajustes, sem guardar dados pessoais desnecessários.
