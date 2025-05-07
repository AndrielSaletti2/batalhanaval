package org.example;
import java.util.Scanner;
import java.util.Random;

public class Main {
    static final int TAMANHO = 10;
    static final int[][] BARCOS = {{4}, {3, 3}, {2, 2, 2}, {1, 1, 1, 1}};

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();

        System.out.print("Digite seu nome: ");
        String jogador1 = sc.nextLine();

        System.out.print("Deseja jogar contra outro jogador (M) ou contra o computador (C)? ");
        String modo = sc.nextLine().trim().toUpperCase();
        while (!modo.equals("M") && !modo.equals("C")) {
            System.out.print("Opção inválida. Digite M (multi) ou C (computador): ");
            modo = sc.nextLine().trim().toUpperCase();
        }

        String jogador2 = modo.equals("M") ? pedirNome(sc, "jogador 2") : "Computador";

        char[][] mapa1 = inicializarMapa();
        char[][] mapa2 = inicializarMapa();
        char[][] ataques1 = inicializarMapa();
        char[][] ataques2 = inicializarMapa();

        System.out.println("\n" + jogador1 + ", deseja posicionar os barcos automaticamente? (S/N)");
        String resposta = sc.nextLine().trim();
        if (resposta.equalsIgnoreCase("S")) {
            posicionarAutomaticamente(mapa1, rand);
        } else {
            posicionarManual(mapa1, sc, jogador1);
        }

        if (modo.equals("M")) {
            System.out.println("\n" + jogador2 + ", deseja posicionar os barcos automaticamente? (S/N)");
            resposta = sc.nextLine().trim();
            if (resposta.equalsIgnoreCase("S")) {
                posicionarAutomaticamente(mapa2, rand);
            } else {
                posicionarManual(mapa2, sc, jogador2);
            }
        } else {
            posicionarAutomaticamente(mapa2, rand);
        }

        boolean turnoJogador1 = true;
        boolean acertou;

        while (true) {
            if (turnoJogador1) {
                System.out.println("\n" + jogador1 + ", sua vez de atacar!");
                exibirMapa(ataques1);
                int[] coord = obterCoordenadasValidas(sc, ataques1);
                int linha = coord[0], coluna = coord[1];

                if (mapa2[linha][coluna] == 'O') {
                    System.out.println("Acertou!");
                    ataques1[linha][coluna] = 'X';
                    mapa2[linha][coluna] = 'X';
                    acertou = true;
                } else {
                    System.out.println("Errou!");
                    ataques1[linha][coluna] = '*';
                    mapa2[linha][coluna] = '*';
                    acertou = false;
                }

                if (verificarVitoria(mapa2)) {
                    System.out.println("\n" + jogador1 + " venceu!");
                    break;
                }

                if (!acertou) {
                    turnoJogador1 = !turnoJogador1;
                }
            } else {
                System.out.println("\n" + jogador2 + ", sua vez de atacar!");
                exibirMapa(ataques2);
                int linha, coluna;

                if (jogador2.equals("Computador")) {
                    do {
                        linha = rand.nextInt(TAMANHO);
                        coluna = rand.nextInt(TAMANHO);
                    } while (ataques2[linha][coluna] != '~');
                    System.out.println("Computador atacou (" + linha + ", " + coluna + ")");
                } else {
                    Scanner scJogador2 = new Scanner(System.in);
                    int[] coord = obterCoordenadasValidas(scJogador2, ataques2);
                    linha = coord[0];
                    coluna = coord[1];
                }

                if (mapa1[linha][coluna] == 'O') {
                    System.out.println("Acertou!");
                    ataques2[linha][coluna] = 'X';
                    mapa1[linha][coluna] = 'X';
                    acertou = true;
                } else {
                    System.out.println("Errou!");
                    ataques2[linha][coluna] = '*';
                    mapa1[linha][coluna] = '*';
                    acertou = false;
                }

                if (verificarVitoria(mapa1)) {
                    System.out.println("\n" + jogador2 + " venceu!");
                    break;
                }

                if (!acertou) {
                    turnoJogador1 = !turnoJogador1;
                }
            }
        }

        sc.close();
    }

    static String pedirNome(Scanner sc, String quem) {
        System.out.print("Digite o nome do " + quem + ": ");
        return sc.nextLine();
    }

    static char[][] inicializarMapa() {
        char[][] mapa = new char[TAMANHO][TAMANHO];
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                mapa[i][j] = '~';
            }
        }
        return mapa;
    }

    static void exibirMapa(char[][] mapa) {
        System.out.print("   ");
        for (int i = 0; i < TAMANHO; i++) System.out.print(i + " ");
        System.out.println();
        for (int i = 0; i < TAMANHO; i++) {
            System.out.printf("%2d ", i);
            for (int j = 0; j < TAMANHO; j++) {
                System.out.print(mapa[i][j] + " ");
            }
            System.out.println();
        }
    }

    static void posicionarManual(char[][] mapa, Scanner sc, String jogador) {
        sc.nextLine();
        System.out.println("Barcos disponíveis:");
        System.out.println("- 1 navio de 4 posições");
        System.out.println("- 2 navios de 3 posições");
        System.out.println("- 3 navios de 2 posições");
        System.out.println("- 4 navios de 1 posição");

        // Navio tm 4
        posicionarBarcoManual(mapa, sc, jogador, 4, 1);

        // Navios tm 3
        posicionarBarcoManual(mapa, sc, jogador, 3, 2);

        // Navios tm 2
        posicionarBarcoManual(mapa, sc, jogador, 2, 3);

        // Navios tm 1
        posicionarBarcoManual(mapa, sc, jogador, 1, 4);
    }

    static void posicionarBarcoManual(char[][] mapa, Scanner sc, String jogador, int tamanho, int quantidade) {
        for (int q = 0; q < quantidade; q++) {
            System.out.println(jogador + ", posicione o " + (q+1) + "º barco de " + tamanho + " casas.");
            while (true) {
                System.out.print("Informe linha, coluna e direção (H/V - para navios de 1 posição use qualquer direção): ");
                String input = sc.nextLine().trim();
                String[] partes = input.split(" ");

                if (partes.length != 3) {
                    System.out.println("Formato inválido! Use: linha coluna H/V");
                    continue;
                }

                try {
                    int linha = Integer.parseInt(partes[0]);
                    int coluna = Integer.parseInt(partes[1]);
                    String direcao = tamanho > 1 ? partes[2].toUpperCase() : "H";
                    if (posicionarBarco(mapa, linha, coluna, tamanho, direcao)) {
                        exibirMapa(mapa);
                        break;
                    } else {
                        System.out.println("Posição inválida! Tente novamente.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Valores inválidos! Use números para linha e coluna.");
                }
            }
        }
    }

    static void posicionarAutomaticamente(char[][] mapa, Random rand) {
        // Navio de 4
        posicionarBarcoAutomatico(mapa, rand, 4, 1);

        // Navios de 3
        posicionarBarcoAutomatico(mapa, rand, 3, 2);

        // Navios de 2
        posicionarBarcoAutomatico(mapa, rand, 2, 3);

        // Navios de 1
        posicionarBarcoAutomatico(mapa, rand, 1, 4);
    }

    static void posicionarBarcoAutomatico(char[][] mapa, Random rand, int tamanho, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            boolean colocado = false;
            while (!colocado) {
                int linha = rand.nextInt(TAMANHO);
                int coluna = rand.nextInt(TAMANHO);
                String direcao = tamanho > 1 ? (rand.nextBoolean() ? "H" : "V") : "H";
                colocado = posicionarBarco(mapa, linha, coluna, tamanho, direcao);
            }
        }
    }

    static boolean posicionarBarco(char[][] mapa, int linha, int coluna, int tamanho, String direcao) {
        if (linha < 0 || coluna < 0 || linha >= TAMANHO || coluna >= TAMANHO) {
            return false;
        }

        if (direcao.equals("H")) {
            if (coluna + tamanho > TAMANHO) return false;
            for (int i = 0; i < tamanho; i++) {
                if (mapa[linha][coluna + i] != '~') return false;
            }
            for (int i = 0; i < tamanho; i++) mapa[linha][coluna + i] = 'O';
            return true;
        } else if (direcao.equals("V")) {
            if (linha + tamanho > TAMANHO) return false;
            for (int i = 0; i < tamanho; i++) {
                if (mapa[linha + i][coluna] != '~') return false;
            }
            for (int i = 0; i < tamanho; i++) mapa[linha + i][coluna] = 'O';
            return true;
        }
        return false;
    }

    static int[] obterCoordenadasValidas(Scanner sc, char[][] ataques) {
        int linha, coluna;
        while (true) {
            System.out.print("Informe linha e coluna para atacar (ex: 1 2): ");
            linha = sc.nextInt();
            coluna = sc.nextInt();
            sc.nextLine(); // Limpa o buffer
            if (linha < 0 || linha >= TAMANHO || coluna < 0 || coluna >= TAMANHO) {
                System.out.println("Coordenadas inválidas! Fora do mapa.");
            } else if (ataques[linha][coluna] != '~') {
                System.out.println("Você já atacou essa posição! Escolha outra.");
            } else {
                break;
            }
        }
        return new int[]{linha, coluna};
    }

    static boolean verificarVitoria(char[][] mapa) {
        for (int i = 0; i < TAMANHO; i++) {
            for (int j = 0; j < TAMANHO; j++) {
                if (mapa[i][j] == 'O') return false;
            }
        }
        return true;
    }
}