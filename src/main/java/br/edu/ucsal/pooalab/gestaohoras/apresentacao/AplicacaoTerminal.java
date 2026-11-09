package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.aplicacao.AtividadeService;
import br.edu.ucsal.pooalab.gestaohoras.dominio.AtividadeComplementar;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public final class AplicacaoTerminal {
    private final CategoriaService categorias;
    private final AtividadeService atividades;
    private final Scanner entrada;

    public AplicacaoTerminal(
            CategoriaService categorias, AtividadeService atividades, Scanner entrada) {
        this.categorias = categorias;
        this.atividades = atividades;
        this.entrada = entrada;
    }

    public void executar() {
        System.out.println("Gestão de Horas Complementares");
        boolean executando = true;
        while (executando) {
            mostrarMenu();
            if (!entrada.hasNextLine()) {
                break;
            }
            String opcao = entrada.nextLine().trim();
            try {
                switch (opcao) {
                    case "1" -> cadastrarCategoria();
                    case "2" -> listarCategorias();
                    case "3" -> cadastrarAtividade();
                    case "4" -> listarAtividades();
                    case "5" -> editarAtividade();
                    case "6" -> removerAtividade();
                    case "7" -> editarCategoria();
                    case "8" -> removerCategoria();
                    case "0" -> executando = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Não foi possível concluir: " + exception.getMessage());
            } catch (IllegalStateException exception) {
                System.out.println("Erro ao acessar os dados: " + exception.getMessage());
            }
            System.out.println();
        }
        System.out.println("Até logo!");
    }

    private void mostrarMenu() {
        System.out.println("""
                1. Cadastrar categoria
                2. Listar categorias
                3. Cadastrar atividade
                4. Listar atividades
                5. Editar atividade
                6. Remover atividade
                7. Editar categoria
                8. Remover categoria
                0. Sair""");
        System.out.print("Escolha uma opção: ");
    }

    private void cadastrarCategoria() {
        String nome = lerObrigatorio("Nome da categoria: ");
        BigDecimal metaHoras = lerMetaHoras();
        Categoria categoria = categorias.cadastrar(nome, metaHoras);
        System.out.println("Categoria cadastrada com ID " + categoria.getId() + ".");
    }

    private void listarCategorias() {
        List<Categoria> lista = categorias.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
            return;
        }
        for (Categoria categoria : lista) {
            System.out.printf(
                    "%d - %s (meta: %s h)%n",
                    categoria.getId(), categoria.getNome(), formatar(categoria.getMetaHoras())
            );
        }
    }

    private void editarCategoria() {
        long id = lerId("ID da categoria que deseja editar: ");
        Categoria existente = buscarCategoria(id);
        Categoria atualizada = categorias.atualizar(
                id,
                lerObrigatorio("Nome da categoria: "),
                lerMetaHoras());
        System.out.println("Categoria " + existente.getId() + " atualizada para "
                + atualizada.getNome() + ".");
    }

    private void removerCategoria() {
        long id = lerId("ID da categoria que deseja remover: ");
        Categoria categoria = buscarCategoria(id);
        System.out.println("As atividades serão mantidas sem categoria e poderão ser reclassificadas.");
        String confirmacao = lerObrigatorio("Remover a categoria " + categoria.getNome() + "? (s/n): ");
        if (!confirmacao.equalsIgnoreCase("s") && !confirmacao.equalsIgnoreCase("sim")) {
            System.out.println("Remoção cancelada.");
            return;
        }
        categorias.remover(id);
        System.out.println("Categoria removida. As atividades foram mantidas sem categoria.");
    }

    private Categoria buscarCategoria(long id) {
        return categorias.listar().stream()
                .filter(categoria -> categoria.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Categoria não encontrada."));
    }

    private void cadastrarAtividade() {
        if (categorias.listar().isEmpty()) {
            System.out.println("Cadastre uma categoria antes de registrar atividades.");
            return;
        }
        mostrarCategorias();
        AtividadeComplementar atividade = atividades.cadastrar(
                lerObrigatorio("Título: "),
                lerData(),
                lerCargaHoraria(),
                lerIdOpcional("ID da categoria (Enter para deixar sem categoria): "));
        System.out.println("Atividade cadastrada com ID " + atividade.getId() + ".");
    }

    private void listarAtividades() {
        List<AtividadeComplementar> lista = atividades.listar();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma atividade cadastrada.");
            return;
        }
        for (AtividadeComplementar atividade : lista) {
            mostrarAtividade(atividade);
        }
    }

    private void editarAtividade() {
        long id = lerId("ID da atividade que deseja editar: ");
        AtividadeComplementar existente = atividades.listar().stream()
                .filter(atividade -> atividade.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Atividade não encontrada."));
        mostrarCategorias();
        atividades.atualizar(
                id,
                lerObrigatorio("Título: "),
                lerData(),
                lerCargaHoraria(),
                lerIdOpcional("ID da categoria (Enter para deixar sem categoria): "));
        System.out.println("Atividade " + existente.getId() + " atualizada.");
    }

    private void removerAtividade() {
        long id = lerId("ID da atividade que deseja remover: ");
        atividades.remover(id);
        System.out.println("Atividade removida.");
    }

    private void mostrarCategorias() {
        System.out.println("Categorias disponíveis:");
        for (Categoria categoria : categorias.listar()) {
            System.out.printf("%d - %s%n", categoria.getId(), categoria.getNome());
        }
    }

    private void mostrarAtividade(AtividadeComplementar atividade) {
        System.out.printf(
                "%d - %s | %s | %s h | Categoria: %s%n",
                atividade.getId(),
                atividade.getTitulo(),
                atividade.getData(),
                formatar(atividade.getCargaHoraria()),
                atividade.getCategoria() == null ? "Sem categoria" : atividade.getCategoria().getNome()
        );
    }

    private LocalDate lerData() {
        while (true) {
            String valor = lerObrigatorio("Data (AAAA-MM-DD): ");
            try {
                return LocalDate.parse(valor);
            } catch (DateTimeParseException exception) {
                System.out.println("Informe uma data válida no formato AAAA-MM-DD.");
            }
        }
    }

    private BigDecimal lerCargaHoraria() {
        while (true) {
            String valor = lerObrigatorio("Carga horária: ");
            try {
                BigDecimal horas = new BigDecimal(valor.replace(',', '.'));
                if (horas.signum() <= 0) {
                    System.out.println("A carga horária deve ser maior que zero.");
                    continue;
                }
                return horas;
            } catch (NumberFormatException exception) {
                System.out.println("Informe um número válido, por exemplo 4 ou 2,5.");
            }
        }
    }

    private long lerId(String pergunta) {
        while (true) {
            String valor = lerObrigatorio(pergunta);
            long id;
            try {
                id = Long.parseLong(valor);
            } catch (NumberFormatException exception) {
                System.out.println("Informe um ID numérico válido.");
                continue;
            }
            if (id > 0) {
                return id;
            }
            System.out.println("O ID deve ser maior que zero.");
        }
    }

    private Long lerIdOpcional(String pergunta) {
        while (true) {
            System.out.print(pergunta);
            String valor = entrada.nextLine().trim();
            if (valor.isEmpty()) {
                return null;
            }
            try {
                long id = Long.parseLong(valor);
                if (id > 0) {
                    return id;
                }
            } catch (NumberFormatException ignored) {
                // Continue prompting for a valid category ID.
            }
            System.out.println("Informe um ID numérico válido maior que zero ou deixe em branco.");
        }
    }

    private BigDecimal lerMetaHoras() {
        while (true) {
            String valor = lerObrigatorio("Meta de horas: ");
            try {
                BigDecimal horas = new BigDecimal(valor.replace(',', '.'));
                if (horas.signum() <= 0) {
                    System.out.println("A meta de horas deve ser maior que zero.");
                    continue;
                }
                return horas;
            } catch (NumberFormatException exception) {
                System.out.println("Informe um número válido, por exemplo 20 ou 12,5.");
            }
        }
    }

    private String lerObrigatorio(String pergunta) {
        System.out.print(pergunta);
        String valor = entrada.nextLine();
        if (valor.isBlank()) {
            throw new IllegalArgumentException("Este campo é obrigatório.");
        }
        return valor.strip();
    }

    private static String formatar(BigDecimal valor) {
        return valor.stripTrailingZeros().toPlainString();
    }
}
