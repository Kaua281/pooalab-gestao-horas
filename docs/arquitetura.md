# Arquitetura do sistema

![Diagrama de classes da arquitetura](ClassDiagram.svg)

```mermaid
classDiagram
    direction TB

    class Main
    class PainelInicial
    class PainelVisaoGeral
    class PainelAtividades
    class PainelCategorias
    class PainelComprovantes
    class AplicacaoTerminal

    class CategoriaService {
        +cadastrar(nome, metaHoras) Categoria
        +atualizar(id, nome, metaHoras) Categoria
        +remover(id)
        +listar() List~Categoria~
        +atualizarOrdem(ids)
    }
    class AtividadeService {
        +cadastrar(titulo, data, carga, categoriaId) AtividadeComplementar
        +atualizar(id, titulo, data, carga, categoriaId) AtividadeComplementar
        +listar() List~AtividadeComplementar~
        +remover(id)
    }
    class PainelInicialService {
        +carregar() DadosPainelInicial
        +salvarOrdemCategorias(ids)
    }
    class ProgressoService {
        +calcularResumo() List~ResumoCategoria~
        +totalHorasRegistradas() BigDecimal
        +horasSemCategoria() BigDecimal
    }
    class ServicoExtracaoPdf {
        +lerTexto(arquivo) String
        +sugerirDados(arquivo) SugestaoAtividade
    }
    class ExtratorTextoPdf {
        <<interface>>
        +extrairTexto(arquivo) String
    }
    class ExtratorPdfBox {
        <<Apache PDFBox>>
        +extrairTexto(arquivo) String
    }

    class Categoria {
        +Long id
        +String nome
        +BigDecimal metaHoras
    }
    class AtividadeComplementar {
        +Long id
        +String titulo
        +LocalDate data
        +BigDecimal cargaHoraria
    }
    class SugestaoAtividade {
        +String titulo
        +LocalDate data
        +BigDecimal cargaHoraria
    }
    class DadosPainelInicial
    class ResumoCategoria

    class CategoriaRepository {
        <<interface>>
        +salvar(categoria) Categoria
        +atualizar(categoria) Categoria
        +listarTodas() List~Categoria~
        +atualizarOrdem(ids)
        +remover(id)
    }
    class AtividadeRepository {
        <<interface>>
        +salvar(atividade) AtividadeComplementar
        +listarTodas() List~AtividadeComplementar~
        +buscarPorId(id) Optional~AtividadeComplementar~
        +remover(id)
    }
    class SQLiteCategoriaRepository
    class SQLiteAtividadeRepository
    class SQLiteDatabase

    Main --> PainelInicial
    Main --> AplicacaoTerminal
    Main --> CategoriaService
    Main --> AtividadeService

    PainelInicial *-- PainelVisaoGeral
    PainelInicial *-- PainelAtividades
    PainelInicial *-- PainelCategorias
    PainelInicial *-- PainelComprovantes

    PainelAtividades --> AtividadeService
    PainelAtividades --> CategoriaService
    PainelCategorias --> CategoriaService
    PainelComprovantes --> AtividadeService
    PainelComprovantes --> CategoriaService
    PainelComprovantes --> ServicoExtracaoPdf
    PainelVisaoGeral --> DadosPainelInicial

    CategoriaService --> CategoriaRepository
    AtividadeService --> AtividadeRepository
    AtividadeService --> CategoriaRepository
    PainelInicialService --> CategoriaService
    PainelInicialService --> AtividadeService
    PainelInicialService --> ProgressoService
    ProgressoService --> CategoriaRepository
    ProgressoService --> AtividadeRepository

    ServicoExtracaoPdf --> ExtratorTextoPdf
    ExtratorPdfBox ..|> ExtratorTextoPdf

    SQLiteCategoriaRepository ..|> CategoriaRepository
    SQLiteAtividadeRepository ..|> AtividadeRepository
    SQLiteCategoriaRepository --> SQLiteDatabase
    SQLiteAtividadeRepository --> SQLiteDatabase

    Categoria "1" <-- "0..*" AtividadeComplementar : pertence a
    ServicoExtracaoPdf ..> SugestaoAtividade : cria
    ProgressoService ..> ResumoCategoria : calcula
    PainelInicialService ..> DadosPainelInicial : cria
```

## Padrões aplicados

- **Responsabilidade única (SRP):** `CategoriaService` concentra as operações de categoria, `AtividadeService` concentra as operações de atividade e `ProgressoService` calcula os resumos. `ServicoExtracaoPdf` interpreta o texto dos PDFs.
- **Repository:** `CategoriaService` usa `CategoriaRepository`, enquanto `AtividadeService` usa `AtividadeRepository` e `CategoriaRepository`. `SQLiteCategoriaRepository` e `SQLiteAtividadeRepository` implementam as interfaces e compartilham `SQLiteDatabase`.
- **Strategy:** `ServicoExtracaoPdf` depende da interface `ExtratorTextoPdf`, implementada por `ExtratorPdfBox`. Assim, o serviço não fica acoplado diretamente à biblioteca de leitura de PDF.

## Fluxo resumido

1. `PainelInicial` reúne os painéis de visão geral, atividades, categorias e comprovantes.
2. Cada painel encaminha as ações do usuário aos serviços de aplicação correspondentes.
3. `CategoriaService`, `AtividadeService` e `PainelInicialService` validam e coordenam as operações.
4. Os serviços usam os repositórios para salvar ou consultar dados; `ProgressoService` calcula os resumos por categoria.
5. Ao selecionar um PDF, `ServicoExtracaoPdf` usa `ExtratorTextoPdf`, implementada por `ExtratorPdfBox`, e cria uma `SugestaoAtividade`.
6. Os campos sugeridos podem ser conferidos e alterados antes de `AtividadeService` cadastrar a atividade. O PDF pode ser copiado ou aberto localmente, mas não fica associado permanentemente à atividade no banco.

As categorias e metas são configuradas pelo estudante. A extração do PDF não define a categoria nem determina se uma atividade será aceita pela coordenação.
