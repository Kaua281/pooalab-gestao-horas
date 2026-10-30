# Gestão de Horas Complementares

Aplicação desktop em Java para ajudar estudantes a registrar atividades complementares, associar comprovantes e acompanhar horas cumpridas e pendentes por categoria.

## Equipe

**Turma:** ADS-NOT-PIT-4S-T1 - 54942

- Kauã Oliveira Seixas
- Thiago Viana Santos
- Italo Soares de Lima e Silva
- Isabela Freitas Mota Lima
- Augusto Brasil Guarnieri
- Alisson Brito da Silva

## Escopo inicial

O fluxo proposto é configurar categorias e suas metas, registrar atividades, extrair sugestões de título, data e carga horária de PDFs pesquisáveis e acompanhar o progresso por categoria. As sugestões precisam ser conferidas pelo estudante; o sistema não substitui a avaliação oficial da coordenação. Imagens e PDFs digitalizados poderão ser anexados, mas não terão OCR nesta versão.

O problema e os dados iniciais de validação estão em [`docs/Anexo B — Registro de validação.md`](<docs/Anexo B — Registro de validação.md>). O escopo e os critérios do primeiro MVP estão em [`docs/Anexo C — Definição do MVP.md`](<docs/Anexo C — Definição do MVP.md>).

## Requisitos para desenvolvimento

- JDK 17 ou superior
- Maven 3.9 ou superior

## Compilar e testar

```bash
mvn test
mvn package
```

## Executar e demonstrar

A aplicação ainda não foi implementada. O roteiro de demonstração e os dados seguros serão adicionados em `demo/` quando o fluxo principal estiver definido.

## Estrutura

```text
src/
  main/java/       código da aplicação
  test/java/       testes automatizados
docs/              problema, validação, MVP e declarações
demo/              dados e instruções para demonstração
```

## Transparência

A declaração obrigatória de uso de IA e de componentes de terceiros deverá ser preparada e incluída na entrega final, conforme o Anexo E do manual. A equipe deve registrar as ferramentas e os materiais utilizados durante o desenvolvimento.
