package t1;

public class Processo {

    private String nome;
    private int surtoCpu;
    private int tempoES;
    private int tempoTotalCpu;
    private int prioridade;
    private int creditos;
    private int ordem;
    private EstadoProcesso estado;

    public Processo(String nome, int surtoCpu, int tempoES, int tempoTotalCpu, int prioridade, int ordem) {
        this.nome = nome;
        this.surtoCpu = surtoCpu;
        this.tempoES = tempoES;
        this.tempoTotalCpu = tempoTotalCpu;
        this.prioridade = prioridade;
        this.creditos = prioridade; // créditos iniciam iguais à prioridade
        this.ordem = ordem;
        this.estado = EstadoProcesso.PRONTO; // estado inicial é 'PRONTO'
    }

    // Getters
    public String getNome() {
        return nome;
    }

    public int getSurtoCpu() {
        return surtoCpu;
    }

    public int getTempoES() {
        return tempoES;
    }

    public int getTempoTotalCpu() {
        return tempoTotalCpu;
    }

    public int getPrioridade() {
        return prioridade;
    }

    public int getCreditos() {
        return creditos;
    }

    public int getOrdem() {
        return ordem;
    }

    public EstadoProcesso getEstado() {
        return estado;
    }

    // manipulação do estado e dos créditos do processo
    public void decrementarCreditos() {
        if (creditos > 0) {
            creditos--;
        }
    }

    public void mudarEstado(EstadoProcesso novoEstado) {
        if (this.estado == EstadoProcesso.FINALIZADO) {
            // Um processo finalizado não pode mudar de estado
            return;
        }

        // Validação das transições de estado
        switch (this.estado) {
            case PRONTO:
                // De PRONTO, pode ir para EXECUTANDO ou BLOQUEADO, mas não para FINALIZADO diretamente
                if (novoEstado == EstadoProcesso.FINALIZADO) {
                    throw new IllegalStateException("O estado não pode mudar de PRONTO para FINALIZADO diretamente.");
                }
                break;

            case EXECUTANDO:
                // De EXECUTANDO, pode ir para BLOQUEADO ou FINALIZADO
                if (novoEstado != EstadoProcesso.BLOQUEADO && novoEstado != EstadoProcesso.FINALIZADO) {
                    throw new IllegalStateException("O estado não pode mudar de EXECUTANDO para " + novoEstado);
                }
                break;

            case BLOQUEADO:
                // De BLOQUEADO, só pode voltar para PRONTO
                if (novoEstado != EstadoProcesso.PRONTO) {
                    throw new IllegalStateException("O estado não pode mudar de BLOQUEADO para " + novoEstado);
                }
                break;

            default:
                throw new IllegalStateException("Transição de estado inválida.");
        }

        // Se a transição for válida, mudamos o estado
        this.estado = novoEstado;
    }

    // resetar os créditos de acordo com a fórmula: cred = cred/2 + prio
    public void resetarCreditos() {
        creditos = creditos / 2 + prioridade;
    }

    @Override
    public String toString() {
        return String.format("Processo %s: Estado = %s, Créditos = %d, Tempo Total de CPU = %d",
                nome, estado, creditos, tempoTotalCpu);
    }
}

