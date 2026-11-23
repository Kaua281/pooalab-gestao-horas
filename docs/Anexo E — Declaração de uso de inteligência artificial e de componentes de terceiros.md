# Anexo E - Declaração de uso de inteligência artificial e de componentes de terceiros

**DOCUMENTO OBRIGATÓRIO**

**Solução:** Sistema de Gestão de Horas Complementares

**Turma:** ADS-NOT-PIT-4S-T1 - 54942

**Equipe:** POOA LAB

**Integrantes:**

- Kauã Oliveira Seixas
- Thiago Viana Santos
- Italo Soares de Lima e Silva
- Isabela Freitas Mota Lima
- Augusto Brasil Guarnieri
- Alisson Brito da Silva

## 1. Uso de inteligência artificial

**Houve uso de IA?** (X) Sim ( ) Não

| Ferramenta ou modelo | Finalidade ou tarefa | Parte afetada | Revisão e validação realizadas |
| --- | --- | --- | --- |
| GitHub Copilot usando o Copilot SDK no VS Code | Apoio ao levantamento do MVP, criação inicial das telas com Java Swing,, atualização da documentação e correção de problemas encontrados no projeto | README.md, Anexo C - Definição do MVP, painéis em Java Swing, | Leitura, adaptação e aprovação das propostas pela equipe; revisão do código; compilação e execução automatizada dos testes JUnit |
| GitHub Copilot | Criação e refinamento de testes automatizados com JUnit 5 | src/test | Revisão manual dos casos de teste pela equipe e execução de toda a suíte |
| Google Gemini 3.1 Pro | Proposta de arquitetura e apoio à organização do diagrama de classes | docs/arquitetura.md e docs/ClassDiagram.svg | Revisão do diagrama pela equipe e ajustes com base nas classes e nos relacionamentos efetivamente implementados |

**Observações sobre o uso de IA:** A IA foi utilizada como ferramenta de apoio. A equipe revisou as propostas, alterou o que era necessário, validou o código por meio da compilação e dos testes automatizados e manteve a responsabilidade pelas decisões e pelo conteúdo entregue.

## 2. Componentes e recursos de terceiros

**Houve uso de componentes de terceiros?** (X) Sim ( ) Não

| Componente ou recurso | Origem ou link | Versão e licença | Função e alterações realizadas |
| --- | --- | --- | --- |
| JDK 17 | https://openjdk.org/ | JDK 17; GPL v2 com Classpath Exception, conforme a distribuição utilizada | Compilar e executar a aplicação Java Swing |
| Java Swing | Plataforma Java/JDK | Distribuído com o JDK 17; GPL v2 com Classpath Exception, conforme a distribuição utilizada | Construir a interface desktop |
| SQLite JDBC | https://github.com/xerial/sqlite-jdbc | 3.50.2.0; Apache License 2.0 | Persistência local das categorias e atividades |
| Apache PDFBox | https://pdfbox.apache.org/ | 3.0.5; Apache License 2.0 | Extração de texto de PDFs pesquisáveis |
| JUnit 5 | https://junit.org/junit5/ | 5.12.2; Eclipse Public License 2.0 | Testes unitários e de integração da aplicação |
| Apache Maven | https://maven.apache.org/ | 3.9.9; Apache License 2.0 | Compilação, gerenciamento de dependências e execução dos testes |
| Maven Wrapper | https://maven.apache.org/wrapper/ | Wrapper 3.3.4; Apache License 2.0 | Executar o Maven pelo script mvnw, sem necessidade de instalação prévia |
| Mermaid CLI | https://github.com/mermaid-js/mermaid-cli | Versão utilizada pelo ambiente; MIT License | Geração do diagrama de classes em formato SVG; nenhuma alteração no código da ferramenta |

Não foram incorporados ao projeto fragmentos de código, textos ou imagens protegidos obtidos de terceiros.

## 3. Materiais ou código preexistentes

**Houve material produzido antes do PooaLAB?** ( ) Sim (X) Não

**Identificação e origem:** Não se aplica.

**Contribuição nova realizada pela equipe:** O projeto foi desenvolvido pela equipe durante a atividade do PooaLAB. Foram criadas a aplicação Java, os painéis Swing, os testes, a persistência SQLite, a leitura de PDFs, a documentação e o diagrama de classes.

## 4. Declaração da equipe

Declaramos que as informações acima são completas e verdadeiras; que revisamos e testamos as contribuições apoiadas por IA; que identificamos as fontes, licenças e partes preexistentes relevantes; que não apresentamos como autoria própria material de terceiros; e que não compartilhamos dados pessoais, credenciais ou informações restritas de forma indevida.

**Responsável pela submissão:** Kauã Oliveira Seixas

## Ciência dos integrantes

| Nome completo | Assinatura ou registro de ciência |
| --- | --- |
| Kauã Oliveira Seixas | Ciente |
| Thiago Viana Santos | Ciente |
| Italo Soares de Lima e Silva | Ciente |
| Isabela Freitas Mota Lima | Ciente |
| Augusto Brasil Guarnieri | Ciente |
| Alisson Brito da Silva | Ciente |

**Data:** 22/11/2026
