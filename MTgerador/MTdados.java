/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MTgerador;

/**
 *
 * @author André
 */
public class MTdados {
    int id;
    int passos;
    int uns;
    boolean parou;
    int tam_fita;

    public MTdados() {
        
    }
    public MTdados(int id, boolean parou,int passos, int uns, int tam_fita) {
        this.id = id;
        this.passos = passos;
        this.uns = uns;
        this.parou = parou;
        this.tam_fita = tam_fita;
    }

    @Override
    public String toString() {
        return "MTdados{" + "id=" + id + ", passos=" + passos + ", uns=" + uns + ", parou=" + parou + ", tam_fita=" + tam_fita + '}';
    }
    
    
    
    
}
