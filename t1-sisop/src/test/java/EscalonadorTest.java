import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import t1.Escalonador;
import t1.EstadoProcesso;
import t1.Processo;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class EscalonadorTest {

    private Processo p1, p2, p3, p4;
    private Escalonador escalonador;

    @BeforeEach
    public void setup() {
        // Inicializa os processos com diferentes créditos e ordens
        p1 = new Processo("A", 2, 5, 6, 3, 1); // 3 créditos, ordem 1
        p2 = new Processo("B", 3, 10, 6, 4, 2); // 4 créditos, ordem 2
        p3 = new Processo("C", 0, 0, 14, 3, 3); // 3 créditos, ordem 3
        p4 = new Processo("D", 0, 0, 10, 2, 4); // 2 créditos, ordem 4

        // Todos os processos começam no estado PRONTO
        p1.mudarEstado(EstadoProcesso.PRONTO);
        p2.mudarEstado(EstadoProcesso.PRONTO);
        p3.mudarEstado(EstadoProcesso.PRONTO);
        p4.mudarEstado(EstadoProcesso.PRONTO);

        // Cria o escalonador com os processos
        escalonador = new Escalonador(Arrays.asList(p1, p2, p3, p4));
    }

    @Test
    public void testSelecionaProcessoComMaisCreditos() {
        p1 = new Processo("A", 3, 5, 6, 2, 1);
        p2 = new Processo("B", 2, 5, 6, 3, 2);

        escalonador = new Escalonador(Arrays.asList(p1, p2));

        // O processo B tem mais créditos (3), então ele deve ser selecionado
        Processo processoSelecionado = escalonador.selecionarProcesso();
        assertEquals(p2, processoSelecionado, "O processo com mais créditos deveria ser selecionado.");
    }

    @Test
    public void testPerdaDeCreditoPorExecucao() {
        p1 = new Processo("A", 3, 0, 6, 3, 1); // 3 créditos, 6ms de tempo total de CPU

        escalonador.executarProcesso(p1);

        // Após o surto de CPU de 3ms, o processo A deve perder 3 créditos
        assertEquals(0, p1.getCreditos(), "O processo A deveria ter perdido 3 créditos após a execução.");
        assertEquals(EstadoProcesso.PRONTO, p1.getEstado(), "O processo A deveria estar no estado PRONTO após a execução.");
    }

    @Test
    public void testDesempatePorOrdem() {
        // Reduz os créditos de p2 para forçar um empate com p1 (ambos terão 3 créditos)
        p2.decrementarCreditos(); // p2 terá 3 créditos agora

        // Seleciona o processo após o empate
        Processo processoSelecionado = escalonador.selecionarProcesso();

        // Verifica se o processo com a menor ordem (p1) foi selecionado
        assertEquals(p1, processoSelecionado, "Processo A, com menor ordem, deveria ser selecionado em caso de empate.");
    }
    @Test
    public void testNenhumProcessoProntoBloqueado() {
        // Coloca todos os processos no estado BLOQUEADO ou FINALIZADO de maneira válida
        p1.mudarEstado(EstadoProcesso.BLOQUEADO);
        p2.mudarEstado(EstadoProcesso.BLOQUEADO);
        p3.mudarEstado(EstadoProcesso.BLOQUEADO);
        p4.mudarEstado(EstadoProcesso.BLOQUEADO);

        // Verifica que nenhum processo pronto está disponível
        Processo processoSelecionado = escalonador.selecionarProcesso();
        assertNull(processoSelecionado, "Nenhum processo pronto deve resultar em null.");
    }

    @Test
    public void testNenhumProcessoProntoFinalizado() {
        // Coloca todos os processos no estado BLOQUEADO ou FINALIZADO de maneira válida
        p1.mudarEstado(EstadoProcesso.EXECUTANDO);
        p2.mudarEstado(EstadoProcesso.EXECUTANDO);
        p3.mudarEstado(EstadoProcesso.EXECUTANDO);
        p4.mudarEstado(EstadoProcesso.EXECUTANDO);

        p1.mudarEstado(EstadoProcesso.FINALIZADO);
        p2.mudarEstado(EstadoProcesso.FINALIZADO);
        p3.mudarEstado(EstadoProcesso.FINALIZADO);
        p4.mudarEstado(EstadoProcesso.FINALIZADO);

        // Verifica que nenhum processo pronto está disponível
        Processo processoSelecionado = escalonador.selecionarProcesso();
        assertNull(processoSelecionado, "Nenhum processo pronto deve resultar em null.");
    }

    @Test
    public void testTodosProcessosSemCreditos() {
        // Todos os processos prontos, mas com 0 créditos
        while (p1.getCreditos() > 0) {
            p1.decrementarCreditos(); // Decrementa até chegar a 0
        }
        while (p2.getCreditos() > 0) {
            p2.decrementarCreditos(); // Decrementa até chegar a 0
        }
        while (p3.getCreditos() > 0) {
            p3.decrementarCreditos(); // Decrementa até chegar a 0
        }
        while (p4.getCreditos() > 0) {
            p4.decrementarCreditos(); // Decrementa até chegar a 0
        }

        // Selecionar processo deve retornar null
        Processo processoSelecionado = escalonador.selecionarProcesso();
        assertNull(processoSelecionado, "Se todos os processos estão sem créditos, o escalonador deve retornar null.");
    }


    @Test
    public void testSomenteUmProcessoPronto() {
        // Coloca todos os processos no estado BLOQUEADO, exceto p3
        p1.mudarEstado(EstadoProcesso.BLOQUEADO);
        p2.mudarEstado(EstadoProcesso.BLOQUEADO);
        p4.mudarEstado(EstadoProcesso.BLOQUEADO);

        // Apenas p3 está PRONTO
        Processo processoSelecionado = escalonador.selecionarProcesso();
        assertEquals(p3, processoSelecionado, "Quando há apenas um processo pronto, ele deve ser selecionado.");
    }

    @Test
    public void testEmpateCompletoDeCreditosEOrdem() {
        // Configura dois processos com o mesmo número de créditos e ordem
        p1 = new Processo("E", 2, 5, 6, 3, 1); // p1: 3 créditos, ordem 1
        p2 = new Processo("F", 3, 10, 6, 3, 1); // p2: 3 créditos, ordem 1

        // Todos no estado PRONTO
        p1.mudarEstado(EstadoProcesso.PRONTO);
        p2.mudarEstado(EstadoProcesso.PRONTO);

        // Cria um novo escalonador com p1 e p2
        escalonador = new Escalonador(Arrays.asList(p1, p2));

        // Seleciona o processo; como ambos têm os mesmos créditos e ordem, o primeiro encontrado deve ser selecionado
        Processo processoSelecionado = escalonador.selecionarProcesso();
        assertEquals(p1, processoSelecionado, "No caso de empate total, o primeiro processo encontrado deve ser selecionado.");
    }

    @Test
    public void testIniciarExecucao() {
        escalonador.iniciarExecucao(p1);
        assertEquals(EstadoProcesso.EXECUTANDO, p1.getEstado(), "O processo A deveria estar no estado EXECUTANDO.");
    }

    @Test
    public void testProcessarSurtoCPU() {
        // Executa o surto de CPU
        escalonador.processarSurtoCPU(p1);

        // Verifica se o tempo de CPU e os créditos foram reduzidos corretamente
        assertEquals(1, p1.getCreditos(), "O processo A deveria ter 1 crédito restante.");
        assertEquals(4, p1.getTempoTotalCpu(), "O tempo total de CPU do processo A deveria ser 4ms.");
    }

    @Test
    public void testFinalizarProcesso() {
        // Simula a finalização do processo
        escalonador.iniciarExecucao(p1);
        escalonador.finalizarProcesso(p1);
        assertEquals(EstadoProcesso.FINALIZADO, p1.getEstado(), "O processo A deveria estar no estado FINALIZADO.");
    }

    @Test
    public void testBloqueioAposSurtoES() {
        p1 = new Processo("A", 3, 5, 6, 2, 1); // 3ms de surto de CPU, 5ms de E/S

        escalonador.executarProcesso(p1);

        // Após o surto, o processo deve estar no estado BLOQUEADO
        assertEquals(EstadoProcesso.BLOQUEADO, p1.getEstado(), "O processo A deveria estar bloqueado após o surto de CPU.");
    }

    @Test
    public void testBloquearParaES() {
        // Processo com 5ms de E/S e 6ms de CPU total
        p1 = new Processo("A", 2, 5, 6, 3, 1);

        // Executa o bloqueio para E/S
        escalonador.bloquearParaES(p1);

        // Verifica se o processo foi corretamente bloqueado para E/S
        assertEquals(EstadoProcesso.BLOQUEADO, p1.getEstado(), "O processo A deveria estar no estado BLOQUEADO após a operação de E/S.");

        // Verifica que o tempo total de CPU não foi alterado
        assertEquals(6, p1.getTempoTotalCpu(), "O tempo total de CPU não deveria ser alterado após a operação de E/S.");
    }


    @Test
    public void testSurtoDeCPUExato() {
        // Processo com 2ms de surto e 6ms de CPU total
        p1 = new Processo("A", 2, 5, 6, 3, 1);
        escalonador.executarProcesso(p1);

        // Verifica se após o surto, o processo está no estado correto e o tempo foi reduzido corretamente
        assertEquals(4, p1.getTempoTotalCpu(), "O tempo total de CPU deveria ser 4ms após o surto.");
        assertEquals(EstadoProcesso.BLOQUEADO, p1.getEstado(), "O processo A deveria estar bloqueado para E/S após o surto.");
    }


    @Test
    public void testProcessoComCreditosZeradosDuranteExecucao() {
        // Processo com apenas 1 crédito restante e 5ms de E/S
        p1 = new Processo("A", 3, 5, 6, 1, 1); // apenas 1 crédito
        escalonador.executarProcesso(p1);

        // Verifica se o crédito foi zerado
        assertEquals(0, p1.getCreditos(), "O processo A deveria ter 0 créditos após a execução.");

        // Como o processo A tem operações de E/S, ele deve estar no estado BLOQUEADO, não PRONTO
        assertEquals(EstadoProcesso.BLOQUEADO, p1.getEstado(), "O processo A deveria estar no estado BLOQUEADO após o crédito acabar e entrar em E/S.");
    }


    @Test
    public void testBloqueioComCreditosRestantes() {
        // Processo com 2 créditos restantes e E/S
        p1 = new Processo("A", 2, 5, 6, 2, 1); // 2 créditos, 5ms de E/S
        escalonador.executarProcesso(p1);

        // Verifica se o processo foi bloqueado e o tempo de CPU foi corretamente descontado
        assertEquals(EstadoProcesso.BLOQUEADO, p1.getEstado(), "O processo A deveria estar bloqueado para E/S.");
        assertEquals(4, p1.getTempoTotalCpu(), "O tempo total de CPU deveria ser reduzido após o bloqueio para E/S.");
    }

    @Test
    public void testRedistribuicaoDeCreditosAposES() {
        // Processo com 2ms de surto de CPU e 5ms de E/S
        p1 = new Processo("A", 2, 5, 6, 2, 1); // Prioridade 2, 2 créditos

        // Executa o processo até bloquear para E/S
        escalonador.executarProcesso(p1); // Isso deve bloquear o processo para E/S

        // Simula o retorno do processo ao estado PRONTO após E/S
        p1.mudarEstado(EstadoProcesso.PRONTO);
        p1.resetarCreditos(); // Reseta os créditos conforme a fórmula: cred = (cred / 2) + prio

        // Verifica se os créditos foram recalculados corretamente com base na prioridade
        int creditosEsperados = (2 / 2) + 2; // Fórmula: cred = (cred / 2) + prio = (2 / 2) + 2 = 3
        assertEquals(creditosEsperados, p1.getCreditos(), "Os créditos deveriam ser recalculados corretamente após a operação de E/S.");
    }

    @Test
    public void testRedistribuicaoDeCreditosQuandoTodosSemCreditos() {
        // Inicializa os processos com prioridade 2 e sem operações de E/S
        p1 = new Processo("A", 3, 0, 6, 2, 1); // Prioridade 2
        p2 = new Processo("B", 2, 0, 6, 2, 2); // Prioridade 2
        p3 = new Processo("C", 3, 0, 6, 2, 1); // Prioridade 2
        p4 = new Processo("D", 2, 0, 6, 2, 2); // Prioridade 2

        // Executa até os créditos de ambos acabarem
        while (p1.getCreditos() > 0) {
            escalonador.executarProcesso(p1);
        }
        while (p2.getCreditos() > 0) {
            escalonador.executarProcesso(p2);
        }
        while (p3.getCreditos() > 0) {
            escalonador.executarProcesso(p3);
        }
        while (p4.getCreditos() > 0) {
            escalonador.executarProcesso(p4);
        }


        // Ambos os processos PRONTOS estão sem créditos
        assertEquals(0, p1.getCreditos(), "O processo A deveria estar com 0 créditos.");
        assertEquals(0, p2.getCreditos(), "O processo B deveria estar com 0 créditos.");
        assertEquals(0, p3.getCreditos(), "O processo C deveria estar com 0 créditos.");
        assertEquals(0, p4.getCreditos(), "O processo D deveria estar com 0 créditos.");


        // Verifica se a fila de prontos está vazia de créditos e, se sim, redistribui
        escalonador.resetarCreditos();

        // Verifica se os créditos foram redistribuídos corretamente com a fórmula correta
        int expectedCreditosP1 = (p1.getCreditos() / 2) + p1.getPrioridade();
        int expectedCreditosP2 = (p2.getCreditos() / 2) + p2.getPrioridade();
        int expectedCreditosP3 = (p3.getCreditos() / 2) + p3.getPrioridade();
        int expectedCreditosP4 = (p4.getCreditos() / 2) + p4.getPrioridade();



        // Faz a verificação usando a fórmula correta
        assertEquals(expectedCreditosP1, p1.getCreditos(), "Os créditos do processo A deveriam ser redistribuídos corretamente.");
        assertEquals(expectedCreditosP2, p2.getCreditos(), "Os créditos do processo B deveriam ser redistribuídos corretamente.");
        assertEquals(expectedCreditosP3, p3.getCreditos(), "Os créditos do processo C deveriam ser redistribuídos corretamente.");
        assertEquals(expectedCreditosP4, p4.getCreditos(), "Os créditos do processo D deveriam ser redistribuídos corretamente.");


    }

    @Test
    public void testTempoTotalCpuAposES() {
        // Processo com 5ms de E/S e 2ms de surto de CPU
        p1 = new Processo("A", 2, 5, 6, 3, 1);

        // Executa o surto de CPU
        escalonador.processarSurtoCPU(p1);

        // Bloqueia para E/S
        escalonador.bloquearParaES(p1);

        // O tempo total de CPU deve ser reduzido apenas pelo surto de CPU, não pela E/S
        assertEquals(4, p1.getTempoTotalCpu(), "O tempo total de CPU deveria ser reduzido corretamente após o surto de CPU.");
    }

    @Test
    public void testFinalizacaoProcessoAposTempoDeCPU() {
        p1 = new Processo("A", 3, 0, 3, 3, 1); // Tempo total de CPU = 3ms

        escalonador.executarProcesso(p1);

        // Após consumir todo o tempo de CPU, o processo A deve estar no estado FINALIZADO
        assertEquals(EstadoProcesso.FINALIZADO, p1.getEstado(), "O processo A deveria estar finalizado após consumir todo o tempo de CPU.");
    }

    @Test
    public void testEscalonador(){
        escalonador.iniciarEscalonamento();
    }
}
