package br.edu.ucsal.pooalab.gestaohoras.apresentacao;

import br.edu.ucsal.pooalab.gestaohoras.aplicacao.CategoriaService;
import br.edu.ucsal.pooalab.gestaohoras.dominio.Categoria;

import java.math.BigDecimal;
import java.util.List;
import java.util.Scanner;

public final class AplicacaoTerminal {
    private final CategoriaService categorias;
    private final Scanner entrada;

    public AplicacaoTerminal(CategoriaService categorias, Scanner entrada) {
        this.categorias = categorias;
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
                    case "0" -> executando = false;
                    default -> System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Não foi possível concluir: " + exception.getMessage());
            } catch (IllegalStateException exception) {
                System.out.println("Erro ao acessar os dados: " + exception.getMessage());
            }
        }
        System.out.println("Até logo!");
    }

    private void mostrarMenu() {
        System.out.println("1. Cadastrar categoria\n2. Listar categorias\n0. Sair");
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
