package br.edu.ucsal.pooalab.gestaohoras.dominio;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record ResumoCategoria(
        Long categoriaId,
        String nomeCategoria,
        BigDecimal horasRegistradas,
        BigDecimal metaHoras,
        BigDecimal horasRestantes,
        BigDecimal progressoPercentual) {

    private static final BigDecimal CEM = BigDecimal.valueOf(100);

    public static ResumoCategoria calcular(Categoria categoria, BigDecimal horasRegistradas) {
        if (categoria == null) {
            throw new IllegalArgumentException("A categoria é obrigatória.");
        }
        if (horasRegistradas == null || horasRegistradas.signum() < 0) {
            throw new IllegalArgumentException("As horas registradas não podem ser negativas.");
        }

        BigDecimal meta = categoria.getMetaHoras();

        // máximo entre zero e (meta - registradas): nunca mostra horas restantes negativas
        BigDecimal restantes = meta.subtract(horasRegistradas).max(BigDecimal.ZERO);

        // mínimo entre (registradas / meta) x 100 e 100. O arredondamento é para baixo,
        // para que 100% só apareça quando a meta realmente foi atingida.
        BigDecimal percentual = horasRegistradas.multiply(CEM)
                .divide(meta, 2, RoundingMode.DOWN)
                .min(CEM);

        return new ResumoCategoria(
                categoria.getId(), categoria.getNome(),
                horasRegistradas, meta, restantes, percentual);
    }
}
