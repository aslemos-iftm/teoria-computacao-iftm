
package MTgerador;

import java.util.ArrayList;
import java.util.List;

public class MTMain {
    
    private static List<MTdados> dados = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Conexao.limpa();
        for (int i = 0; i <= 16777215; i++) {
            char[] s;
            s = traduz(i);
            registra(s, i);
            if ((i+1) % 50000 == 0) {
                System.out.println("salvando lote de dados "+i);
                Conexao.salvarExecucao(dados);
                dados = new ArrayList<>();
            }
            //System.out.println("\n");
        }//fim do for externo       
        Conexao.salvarExecucao(dados);
    } //fim do Main

    private static char[] traduz(int máquina) {
        int tam = 24;

        //System.out.println(máquina + " em binário (nibbles): ");
        String bin24 = String.format("%24s", Integer.toBinaryString(máquina)).replace(" ", "0");
        for (int j = 0; j < tam; j += 4) {// 1 nibble (4 bits)
            //especificadores de formato %d inteiro, %s de string, nibble[%d] de 4 em 4 bits                
            //System.out.printf("[%d] = %s ", j, bin24.subSequence(j, j + 4));
        }
        //System.out.println();

        char[] s = bin24.toCharArray();

        if (s.length == 0) {
            System.out.println("Não há caracteres!");
        } else {//primeiro nibble [0]
            for (int j = 0; j < 6; j++) {
                if (s[j * 4 + 1] == '0') {
                    s[j * 4 + 1] = 'L';
                } else if (s[j * 4 + 1] == '1') {
                    s[j * 4 + 1] = 'R';
                }
                //dois ultimos bits do nibble [0]                    
                if (s[j * 4 + 2] == '0' && s[j * 4 + 3] == '0') {
                    s[j * 4 + 2] = 'H';
                    s[j * 4 + 3] = ';';  //00 -->halt
                } else if (s[j * 4 + 2] == '0' && s[j * 4 + 3] == '1') {
                    s[j * 4 + 2] = 'A';
                    s[j * 4 + 3] = ';';  //01 --> A
                } else if (s[j * 4 + 2] == '1' && s[j * 4 + 3] == '0') {
                    s[j * 4 + 2] = 'B';
                    s[j * 4 + 3] = ';';  //10 --> B
                } else if (s[j * 4 + 2] == '1' && s[j * 4 + 3] == '1') {
                    s[j * 4 + 2] = 'C';
                    s[j * 4 + 3] = ';';  //11 --> c
                }
            }//fim do for interno
        }//fim do else que testa o nibble[0]  
        //System.out.print(Arrays.toString(s) + "\n");
        return (s);
    }

    private static void registra(char[] s, int mt_id) throws Exception {

        char[] aux = new char[18];
        int index = 0;
        for (int i = 0; i < s.length; i++) {
            if (s[i] != ';') {
                aux[index] = s[i];
                index++;
            }
        }

        char[][] auxalt = new char[6][3];

        System.arraycopy(aux, 0, auxalt[0], 0, 3);
        System.arraycopy(aux, 3, auxalt[1], 0, 3);
        System.arraycopy(aux, 6, auxalt[2], 0, 3);
        System.arraycopy(aux, 9, auxalt[3], 0, 3);
        System.arraycopy(aux, 12, auxalt[4], 0, 3);
        System.arraycopy(aux, 15, auxalt[5], 0, 3);

        char[][] delta = {{'A', '0', '#', '#', '#'}, {'A', '1', '#', '#', '#'}, {'B', '0', '#', '#', '#'},
        {'B', '1', '#', '#', '#'}, {'C', '0', '#', '#', '#'}, {'C', '1', '#', '#', '#'}};
        for (int i = 0; i <= 5; i++) {
            System.arraycopy(auxalt[i], 0, delta[i], 2, 3);
            //System.out.print(Arrays.toString(delta[i]));
        }

        //System.out.println();
        MTexecutor MT = new MTexecutor();
        MT.inicializaDelta(delta);

        MTdados mTdados;
        mTdados = MT.aceita("", 21);       // 21 é o valor máximo de S(3) para o castor ocupado
        mTdados.id = mt_id;
        dados.add(mTdados);
        
        //System.out.println(Conexao.consultaExecucao());    
    }
    
}
