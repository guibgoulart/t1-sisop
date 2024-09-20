import org.junit.jupiter.api.Test;
import t1.EstadoProcesso;
import t1.Processo;

import static org.junit.jupiter.api.Assertions.*;

public class ProcessoTest {

    @Test
    public void testCreditosIniciais() {
        Processo p = new Processo("A", 2, 5, 6, 3, 1);
        assertEquals(3, p.getCreditos(), "Os créditos iniciais devem ser iguais à prioridade");
    }

    @Test
    public void testDecrementarCreditos() {
        Processo p = new Processo("B", 3, 10, 6, 3, 2);
        p.decrementarCreditos();
        assertEquals(2, p.getCreditos(), "Os créditos devem ser decrementados em 1");
    }

    @Test
    public void testCreditosNaoNegativos() {
        Processo p = new Processo("C", 2, 5, 6, 1, 3);
        p.decrementarCreditos();
        p.decrementarCreditos();
        p.decrementarCreditos(); // Exceder o limite de créditos
        assertEquals(0, p.getCreditos(), "Os créditos não devem ser negativos");
    }

    @Test
    public void testMudancaDeEstado() {
        Processo p = new Processo("D", 2, 5, 6, 3, 4);
        p.mudarEstado(EstadoProcesso.EXECUTANDO);
        assertEquals(EstadoProcesso.EXECUTANDO, p.getEstado(), "O estado deve ser atualizado para 'EXECUTANDO'");
    }

    @Test
    public void testResetarCreditos() {
        Processo p = new Processo("E", 3, 10, 6, 3, 2);
        p.decrementarCreditos(); // 2 créditos restantes
        p.resetarCreditos(); // Fórmula: cred = cred/2 + prio => 2/2 + 3 = 4
        assertEquals(4, p.getCreditos(), "Os créditos devem ser recalculados usando a fórmula de reset");
    }

    @Test
    public void testProcessoComSurtoZero() {
        // Caso extremo onde o processo tem surto de CPU igual a zero (não requer tempo de CPU)
        Processo p = new Processo("G", 0, 5, 0, 3, 1);
        assertEquals(0, p.getSurtoCpu(), "O surto de CPU deve ser 0");
        assertEquals(EstadoProcesso.PRONTO, p.getEstado(), "O processo deve começar no estado PRONTO");
    }

    @Test
    public void testProcessoFinalizadoNaoAlteraEstado() {
        // Testa se um processo no estado FINALIZADO não altera mais seu estado
        Processo p = new Processo("K", 2, 5, 6, 3, 1);
        p.mudarEstado(EstadoProcesso.EXECUTANDO);  // Passa de PRONTO para EXECUTANDO
        p.mudarEstado(EstadoProcesso.FINALIZADO);  // Finaliza o processo

        // Agora tentar mudar o estado após estar FINALIZADO (deve permanecer FINALIZADO)
        p.mudarEstado(EstadoProcesso.PRONTO);  // Tentar retornar para PRONTO
        assertEquals(EstadoProcesso.FINALIZADO, p.getEstado(), "O estado não deve mudar após ser FINALIZADO");
    }


    @Test
    public void testCreditosNaoAlteradosSeZero() {
        // Testa se os créditos permanecem em zero após várias tentativas de decremento
        Processo p = new Processo("I", 2, 5, 6, 1, 1);
        p.decrementarCreditos();
        p.decrementarCreditos(); // Créditos já são 0 aqui
        assertEquals(0, p.getCreditos(), "Os créditos não devem ser menores que zero");
        p.decrementarCreditos(); // Tentativa extra
        assertEquals(0, p.getCreditos(), "Os créditos devem permanecer 0 após tentativas adicionais de decremento");
    }

    @Test
    public void testResetarCreditosComZeroCreditos() {
        // Testa se o processo reseta corretamente os créditos quando eles são zero
        Processo p = new Processo("J", 3, 5, 6, 2, 2);
        while (p.getCreditos() > 0) {
            p.decrementarCreditos(); // Reduz até os créditos serem zero
        }
        p.resetarCreditos(); // Fórmula: cred = cred/2 + prio => 0/2 + 2 = 2
        assertEquals(2, p.getCreditos(), "Os créditos devem ser recalculados corretamente após reset quando estavam em zero");
    }

    @Test
    public void testProcessoBloqueadoESemSurtoCpu() {
        // Testa se um processo com surto de CPU igual a zero que entra em estado BLOQUEADO funciona corretamente
        Processo p = new Processo("L", 0, 5, 0, 2, 1);
        p.mudarEstado(EstadoProcesso.BLOQUEADO);
        assertEquals(EstadoProcesso.BLOQUEADO, p.getEstado(), "O processo deve estar no estado BLOQUEADO");
        assertEquals(0, p.getSurtoCpu(), "O surto de CPU deve ser 0 para este processo");
    }

    @Test
    public void testTransicaoInvalidaDeEstado() {
        // Testa se a transição de PRONTO para FINALIZADO diretamente lança uma exceção
        Processo p = new Processo("M", 2, 5, 6, 3, 1);
        // Agora esperamos uma exceção ao tentar mudar de PRONTO para FINALIZADO
        assertThrows(IllegalStateException.class, () -> {
            p.mudarEstado(EstadoProcesso.FINALIZADO);
        }, "O estado não pode mudar de PRONTO para FINALIZADO diretamente");
    }


    @Test
    public void testBloquearProcessoComCreditosPositivos() {
        // Testando se o processo vai para o estado BLOQUEADO com créditos ainda positivos
        Processo p = new Processo("N", 2, 5, 6, 3, 1);
        p.mudarEstado(EstadoProcesso.BLOQUEADO);
        assertEquals(EstadoProcesso.BLOQUEADO, p.getEstado(), "O estado deve ser BLOQUEADO");
        assertTrue(p.getCreditos() > 0, "O processo ainda deve ter créditos restantes");
    }

    @Test
    public void testResetarCreditosLimiteInferior() {
        // Testa se o processo reseta corretamente os créditos quando a prioridade é muito baixa
        Processo p = new Processo("O", 2, 5, 6, 0, 2); // Prioridade 0
        p.decrementarCreditos(); // Créditos ficam 0
        p.resetarCreditos(); // Fórmula: cred = 0/2 + 0 = 0
        assertEquals(0, p.getCreditos(), "Os créditos devem permanecer 0 após reset se a prioridade for 0");
    }

    @Test
    public void testCreditosGrandesNaResetagem() {
        // Testa se o reset funciona corretamente quando os créditos são muito grandes
        Processo p = new Processo("P", 2, 5, 6, 1000, 1); // Prioridade alta
        p.decrementarCreditos(); // Créditos diminuem para 999
        p.resetarCreditos(); // Fórmula: cred = 999/2 + 1000
        assertEquals(1499, p.getCreditos(), "Os créditos devem ser recalculados corretamente para grandes valores");
    }
}