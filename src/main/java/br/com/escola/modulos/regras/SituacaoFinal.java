package br.com.escola.modulos.regras;

public enum SituacaoFinal {
    APROVADO("Aprovado Direto", "Atingiu a média e frequência regulamentares."),
    APROVADO_RECUPERACAO("Aprovado após Recuperação", "Atingiu nota suficiente após exame de recuperação."),
    RECUPERACAO("Em Recuperação", "Média abaixo do corte direto, mas elegível para prova de recuperação."),
    REPROVADO_POR_NOTA("Reprovado por Nota", "Média insuficiente para aprovação ou recuperação."),
    REPROVADO_POR_FALTA("Reprovado por Falta (LDB)", "Frequência escolar inferior a 75% (Art. 24 da LDB 9.394/96)."),
    REPROVADO_NOTA_E_FALTA("Reprovado por Nota e Falta", "Não atingiu média nem frequência mínima exigida.");

    private final String descricao;
    private final String detalhe;

    SituacaoFinal(String descricao, String detalhe) {
        this.descricao = descricao;
        this.detalhe = detalhe;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDetalhe() {
        return detalhe;
    }
}
