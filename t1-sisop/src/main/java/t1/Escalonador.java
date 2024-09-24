package t1;

import java.util.List;

public class Escalonador {
    private List<Processo> listaDeProcessos;
    private int tempo;

    // Construtor que recebe a lista de processos
    public Escalonador(List<Processo> processos) {
        this.listaDeProcessos = processos;
        this.tempo = 0; // Inicializa o tempo total do sistema
    }

    public void resetarCreditos() {
        System.out.println("Resetando créditos de todos os processos...");
        for (Processo p : listaDeProcessos) {
            // Redistribui os créditos de acordo com a fórmula, independentemente do estado
            p.resetarCreditos();
            System.out.println("Processo: " + p.getNome() + " | Créditos resetados para: " + p.getCreditos());
        }
    }

    // Método que seleciona o processo com o maior número de créditos (e desempate por ordem)
    public Processo selecionarProcesso() {
        Processo processoSelecionado = null;

        for (Processo p : listaDeProcessos) {
            if (p.getEstado() == EstadoProcesso.PRONTO && p.getCreditos() > 0) {
                if (processoSelecionado == null ||
                        p.getCreditos() > processoSelecionado.getCreditos() ||
                        (p.getCreditos() == processoSelecionado.getCreditos() && p.getOrdem() < processoSelecionado.getOrdem())) {
                    processoSelecionado = p;
                }
            }
        }

        return processoSelecionado; // Retorna o processo com maior crédito ou desempate por ordem
    }

    // Método para finalizar o processo
    public void finalizarProcesso(Processo p) {
        p.mudarEstado(EstadoProcesso.FINALIZADO);
        System.out.println("Processo " + p.getNome() + " finalizado.");
    }


    // Método principal para gerenciar a execução do processo
    public void executarProcesso(Processo p) {
        iniciarExecucao(p);
        processarSurtoCPU(p);
        finalizarOuBloquearProcesso(p); // Chama o método para decidir se bloqueia ou finaliza
    }

    // Método para iniciar a execução do processo
    public void iniciarExecucao(Processo p) {
        p.mudarEstado(EstadoProcesso.EXECUTANDO);
        System.out.println("Tempo: " + tempo + "ms - Executando processo: " + p.getNome() + " | Créditos: " + p.getCreditos());
    }

    // Método para processar o surto de CPU
    public void processarSurtoCPU(Processo p) {
        int tempoExecutado = p.getSurtoCpu();

        for (int i = 0; i < tempoExecutado; i++) {
            if (p.getCreditos() > 0) {
                p.decrementarCreditos(); // Reduz 1 crédito a cada milissegundo
                p.decrementarTempoTotalCpu(1); // Desconta 1ms do tempo total de CPU
                tempo++; // Incrementa o tempo global do sistema

                System.out.println("Tempo: " + tempo + "ms - Processo: " + p.getNome() + " | Créditos restantes: " + p.getCreditos() + " | Tempo restante de CPU: " + p.getTempoTotalCpu());
            }

            // Se o tempo total de CPU do processo acabar, interrompemos o loop
            if (p.getTempoTotalCpu() <= 0) {
                break;
            }
        }

    }

    // Método para verificar se o processo deve ser finalizado ou bloqueado para E/S
    private void finalizarOuBloquearProcesso(Processo p) {
        if (p.getTempoTotalCpu() <= 0) {
            finalizarProcesso(p); // Finaliza se o tempo de CPU acabou
        } else if (p.getTempoES() > 0) {
            bloquearParaES(p); // Bloqueia para E/S se o processo tiver operações de E/S
        } else {
            p.mudarEstado(EstadoProcesso.PRONTO); // Caso contrário, volta para PRONTO
        }
    }

    // Método para bloquear o processo para operações de E/S
    public void bloquearParaES(Processo p) {
        p.mudarEstado(EstadoProcesso.BLOQUEADO);
        System.out.println("Tempo: " + tempo + "ms - Processo " + p.getNome() + " bloqueado para E/S por " + p.getTempoES() + "ms.");

        // Incrementa o tempo global do sistema com o tempo de E/S
        tempo += p.getTempoES();
    }


    // Verifica se todos os processos na fila de PRONTOS estão sem créditos
    private boolean todosProcessosProntosSemCreditos() {
        for (Processo p : listaDeProcessos) {
            if (p.getEstado() == EstadoProcesso.PRONTO && p.getCreditos() > 0) {
                return false; // Existe pelo menos um processo PRONTO com créditos
            }
        }
        return true; // Todos os processos PRONTOS estão sem créditos
    }


    public boolean todosProcessosFinalizados() {
        for (Processo p : listaDeProcessos) {
            if (p.getEstado() != EstadoProcesso.FINALIZADO) {
                return false;  // Se algum processo não está finalizado, retorna false
            }
        }
        return true;  // Se todos os processos estão finalizados, retorna true
    }

    public void iniciarEscalonamento() {
        while (!todosProcessosFinalizados()) {
            Processo processoExecutando = selecionarProcesso();

            if (processoExecutando != null) {
                System.out.println("Tempo: " + tempo + "ms - Processo selecionado para execução: " + processoExecutando.getNome());
                executarProcesso(processoExecutando);
            } else {
                // Se nenhum processo tem créditos, redistribuímos os créditos
                if (todosProcessosProntosSemCreditos()) {
                    resetarCreditos();  // Redistribui os créditos de todos os processos
                }
            }
            // Incrementa o tempo global do sistema após cada ciclo de execução
            tempo++;
        }
        System.out.println("Todos os processos foram finalizados.");
    }
}