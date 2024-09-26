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
    public int tempoRestanteES;
    private int turnaroundTime;
    private int tempoDeInicio;

    public Processo(String nome, int surtoCpu, int tempoES, int tempoTotalCpu, int prioridade, int ordem) {
        this.nome = nome;
        this.surtoCpu = surtoCpu;
        this.tempoES = tempoES;
        this.tempoTotalCpu = tempoTotalCpu;
        this.prioridade = prioridade;
        this.creditos = prioridade;
        this.ordem = ordem;
        this.estado = EstadoProcesso.PRONTO;
        this.tempoRestanteES = 0; // Inicializamos o tempo restante de E/S como 0
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

    public int getTempoDeInicio() {
        return tempoDeInicio;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
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

    public void setTempoDeInicio(int tempo){
        tempoDeInicio = tempo;
    }

    public void calculaTurnaroundTime(int tempoFinal) {
        turnaroundTime = turnaroundTime + (tempoFinal - tempoDeInicio);
    }

    public void decrementarCreditos() {
        if (creditos > 0) {
            creditos--;
        }
    }

    public void decrementarTempoTotalCpu(int tempo) {
        this.tempoTotalCpu = Math.max(0, this.tempoTotalCpu - tempo); // Garante que nunca seja negativo
    }

    public void decrementarTempoTotalCpu() {
        if (tempoTotalCpu > 0) {
            tempoTotalCpu--;
        }
    }

    public void resetarCreditos() {
        this.creditos = ((this.creditos / 2) + this.prioridade);  // Garante que ao menos 1 crédito seja atribuído
    }

    public void mudarEstado(EstadoProcesso novoEstado) {
        if (this.estado == EstadoProcesso.FINALIZADO) {
            return; // Processo finalizado não pode mudar de estado
        }

        switch (this.estado) {
            case PRONTO:
                if (novoEstado == EstadoProcesso.FINALIZADO) {
                    throw new IllegalStateException("O estado não pode mudar de PRONTO para FINALIZADO diretamente.");
                }
                break;

            case EXECUTANDO:
                if (novoEstado != EstadoProcesso.BLOQUEADO && novoEstado != EstadoProcesso.FINALIZADO && novoEstado != EstadoProcesso.PRONTO) {
                    throw new IllegalStateException("O estado não pode mudar de EXECUTANDO para " + novoEstado);
                }
                break;

            case BLOQUEADO:
                if (novoEstado != EstadoProcesso.PRONTO) {
                    throw new IllegalStateException("O estado não pode mudar de BLOQUEADO para " + novoEstado);
                }
                break;

            default:
                throw new IllegalStateException("Transição de estado inválida.");
        }

        this.estado = novoEstado;
    }

    @Override
    public String toString() {
        return String.format("Processo %s: Estado = %s, Créditos = %d, Tempo Total de CPU = %d, Turnarround time = %d",
                nome, estado, creditos, tempoTotalCpu, turnaroundTime);
    }
}

