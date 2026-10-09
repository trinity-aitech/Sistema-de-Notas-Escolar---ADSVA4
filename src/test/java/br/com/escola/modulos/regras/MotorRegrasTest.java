package br.com.escola.modulos.regras;

import java.util.List;
import java.util.Objects;

/**
 * Testes das Regras de Negócio: LDB 9.394/96, CEE e Regulamento Escolar.
 * Pode ser executado via JUnit ou diretamente como aplicação Java independente (main).
 */
public class MotorRegrasTest {

    public static void main(String[] args) {
        System.out.println("--- INICIANDO TESTES DO MOTOR DE REGRAS ESCOLARES ---");
        MotorRegrasTest tests = new MotorRegrasTest();
        int pass = 0, fail = 0;

        try { tests.testCalculoMedia(); pass++; System.out.println("✅ testCalculoMedia: PASS"); } catch (Throwable e) { fail++; System.err.println("❌ testCalculoMedia: " + e.getMessage()); }
        try { tests.testCalculoFrequencia(); pass++; System.out.println("✅ testCalculoFrequencia: PASS"); } catch (Throwable e) { fail++; System.err.println("❌ testCalculoFrequencia: " + e.getMessage()); }
        try { tests.testAprovadoDireto(); pass++; System.out.println("✅ testAprovadoDireto: PASS"); } catch (Throwable e) { fail++; System.err.println("❌ testAprovadoDireto: " + e.getMessage()); }
        try { tests.testReprovadoPorFaltaLdbArt24(); pass++; System.out.println("✅ testReprovadoPorFaltaLdbArt24 (Art. 24 da LDB): PASS"); } catch (Throwable e) { fail++; System.err.println("❌ testReprovadoPorFaltaLdbArt24: " + e.getMessage()); }
        try { tests.testElegivelRecuperacao(); pass++; System.out.println("✅ testElegivelRecuperacao: PASS"); } catch (Throwable e) { fail++; System.err.println("❌ testElegivelRecuperacao: " + e.getMessage()); }
        try { tests.testAprovadoAposRecuperacao(); pass++; System.out.println("✅ testAprovadoAposRecuperacao: PASS"); } catch (Throwable e) { fail++; System.err.println("❌ testAprovadoAposRecuperacao: " + e.getMessage()); }
        try { tests.testReprovadoPorNotaSemExame(); pass++; System.out.println("✅ testReprovadoPorNotaSemExame: PASS"); } catch (Throwable e) { fail++; System.err.println("❌ testReprovadoPorNotaSemExame: " + e.getMessage()); }
        try { tests.testReprovadoPorNotaEFalta(); pass++; System.out.println("✅ testReprovadoPorNotaEFalta: PASS"); } catch (Throwable e) { fail++; System.err.println("❌ testReprovadoPorNotaEFalta: " + e.getMessage()); }

        System.out.println("\nResumo dos testes: " + pass + " passaram, " + fail + " falharam.");
        if (fail > 0) {
            System.exit(1);
        }
    }

    private final MotorRegrasService motor = new MotorRegrasService();

    public void testCalculoMedia() {
        double m = motor.calcularMedia(List.of(8.0, 9.0, 7.0, 10.0));
        assertEquals(8.5, m, 0.01, "Média deveria ser 8.5");
    }

    public void testCalculoFrequencia() {
        // 80 horas de aula, 16 faltas -> 64 presenças -> 80%
        double f = motor.calcularPercentualFrequencia(80, 16);
        assertEquals(80.0, f, 0.01, "Frequência deveria ser 80%");
    }

    public void testAprovadoDireto() {
        // Média 7.5, 80 aulas, 8 faltas (90% freq)
        MotorRegrasService.ResultadoAvaliacao res = motor.avaliarSituacaoFinal(7.5, 80, 8, null);
        assertEquals(SituacaoFinal.APROVADO, res.situacao(), "Deveria estar APROVADO");
    }

    public void testReprovadoPorFaltaLdbArt24() {
        // Média altíssima 9.5, mas 80 aulas e 25 faltas -> 68.75% frequência (< 75% exigido pela LDB)
        MotorRegrasService.ResultadoAvaliacao res = motor.avaliarSituacaoFinal(9.5, 80, 25, null);
        assertEquals(SituacaoFinal.REPROVADO_POR_FALTA, res.situacao(), "Deveria estar REPROVADO_POR_FALTA pela LDB 75%");
    }

    public void testElegivelRecuperacao() {
        // Média 5.5 (entre 4.0 e 7.0), frequência 85% (> 75%), sem exame ainda
        MotorRegrasService.ResultadoAvaliacao res = motor.avaliarSituacaoFinal(5.5, 80, 10, null);
        assertEquals(SituacaoFinal.RECUPERACAO, res.situacao(), "Deveria estar em RECUPERACAO");
    }

    public void testAprovadoAposRecuperacao() {
        // Média anual 5.5 + nota exame 6.5 = média final 6.0 (>= 5.0 corte de recuperação)
        MotorRegrasService.ResultadoAvaliacao res = motor.avaliarSituacaoFinal(5.5, 80, 10, 6.5);
        assertEquals(SituacaoFinal.APROVADO_RECUPERACAO, res.situacao(), "Deveria estar APROVADO_RECUPERACAO");
        assertEquals(6.0, res.mediaFinal(), 0.01, "Média final deve ser 6.0");
    }

    public void testReprovadoPorNotaSemExame() {
        // Média 3.2 (< 4.0 mínimo para exame)
        MotorRegrasService.ResultadoAvaliacao res = motor.avaliarSituacaoFinal(3.2, 80, 10, null);
        assertEquals(SituacaoFinal.REPROVADO_POR_NOTA, res.situacao(), "Deveria estar REPROVADO_POR_NOTA direto");
    }

    public void testReprovadoPorNotaEFalta() {
        // Média 3.0 e frequência 60%
        MotorRegrasService.ResultadoAvaliacao res = motor.avaliarSituacaoFinal(3.0, 80, 32, null);
        assertEquals(SituacaoFinal.REPROVADO_NOTA_E_FALTA, res.situacao(), "Deveria estar REPROVADO_NOTA_E_FALTA");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " - Esperado: " + expected + ", mas obtido: " + actual);
        }
    }

    private static void assertEquals(double expected, double actual, double delta, String message) {
        if (Math.abs(expected - actual) > delta) {
            throw new AssertionError(message + " - Esperado: " + expected + ", mas obtido: " + actual);
        }
    }
}
