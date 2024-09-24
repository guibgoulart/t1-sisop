package t1;


import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Criação de processos de exemplo
        Processo p1 = new Processo("A", 3, 5, 10, 2, 1);
        Processo p2 = new Processo("B", 4, 2, 12, 3, 2);
        Processo p3 = new Processo("C", 2, 6, 8, 1, 3);
        Processo p4 = new Processo("D", 5, 4, 15, 4, 4);

        // Adicionando processos à lista
        List<Processo> listaDeProcessos = new ArrayList<>();
        listaDeProcessos.add(p1);
        listaDeProcessos.add(p2);
        listaDeProcessos.add(p3);
        listaDeProcessos.add(p4);

        // Criando o escalonador com a lista de processos
        Escalonador escalonador = new Escalonador(listaDeProcessos);

        // Iniciando o escalonamento
        escalonador.iniciarEscalonamento();

        // Exibindo o estado final de cada processo
        System.out.println("\n--- Estado final dos processos ---");
        for (Processo p : listaDeProcessos) {
            System.out.println(p);
        }
    }
}