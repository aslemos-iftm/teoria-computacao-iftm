package MTgerador;

public class MTexecutor {
    /*
     * Uma constante representa a célula em branco.
     */
    private static final char B = 0;

    /*
     * Uma cadeia contendo, para cada
     * estado aceitador, o caractere respectivo,  
     * em qualquer ordem 
     */
    private static final char aceitador = 'H';

    /*
     * O estado inicial.
     */
    private static final char inicial = 'A';

    private static char[][] delta;

    /*   * A fita corrente e posição do cabeçote da MT. Sempre
     * manter 0 <= cabeçote < fita.length(), acrescentando
     * brancos no início ou final da fita, tantos quantos
     * necessário.
     */
    private String fita;
    private int cabeçote;

    /*
     * O estado atual.
     */
    private char estado;

    /**
     * Obtém o movimento para um dado estado e símbolo.
     * @param estado estado atual
     * @param símbolo símbolo na posição atual do cabeçote
     * @return char[] de cinco elementos da tabela delta
     */
    
    void inicializaDelta (char [][] d){
        delta = d;
    }
    char[] consultaMovimento(char estado, char símbolo) {
        for(int i = 0; i<delta.length; i++) {
            char[] movimento = delta[i];
            if (movimento[0]==estado && movimento[1]==símbolo)
                return movimento;
        }
        return null;
    }

    void executaMovimento(char novoestado, char símbolo, char dir) {

        // escreve na fita
        fita = fita.substring(0,cabeçote) + símbolo + fita.substring(cabeçote+1);

        // move o cabeçote, mantendo a invariante
        // 0 <= cabeçote < fita.length()
        if (dir=='L') {
            if (cabeçote==0) fita = "0" + fita; else cabeçote -= 1;
        }
        else {
            cabeçote += 1;
            if (cabeçote==fita.length()) fita += "0";
        }

        // vai para o próximo estado
        estado = novoestado;
    }

    public MTdados aceita(String s, int j) {

        estado = inicial;
        cabeçote = 0;
        fita = s;
        int passo = 0;
        boolean parou = false;
        //String história = "";
        
        //variáveis que vão registrar o ocorrido

        // define 0 <= cabeçote < fita.length()
        if (cabeçote==fita.length()) fita += "0";

        while (passo <= j) {
            
            //história += estado;
            if (estado==aceitador) {
                parou = true;
                break;
            }
            
            char[] movimento = consultaMovimento(estado,fita.charAt(cabeçote));
            if (movimento==null) {
                parou = false;
                //mensagem de erro
                break;
            }
            executaMovimento(movimento[4],movimento[2],movimento[3]);
            passo++;
        }
        //System.out.println(história);
        
        int uns = 0;
        for (int i = 0; i < fita.length(); i++){
            if (fita.charAt(i) == '1')
                uns++;
        }
        
        MTdados MTdados = new MTdados();
        
        MTdados.parou = parou;
        MTdados.passos = passo;
        MTdados.uns = uns;
        MTdados.tam_fita = fita.length();
        
        //System.out.println(fita);
        //System.out.println(uns);
        
        return(MTdados);
        
    }
}

