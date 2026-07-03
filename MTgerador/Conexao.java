/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package MTgerador;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author André
 */
public class Conexao {
    
    private static Connection getConexao() throws Exception {
        Class.forName("org.apache.derby.jdbc.ClientDriver");
        return DriverManager.getConnection("jdbc:derby://localhost:1527/mturing", "aslemos","senha");
    }   
    
    public static void salvarExecucao(List<MTdados> mtDadosList) throws Exception {
        Connection c = getConexao();
        PreparedStatement statement = c.prepareStatement("INSERT INTO TB_EXECUCOES(ID,PAROU,PASSOS,UNS,TAM_FITA) VALUES (?,?,?,?,?)");
        for (MTdados dados: mtDadosList) {
            statement.setInt(1, dados.id);
            statement.setBoolean(2, dados.parou);
            statement.setInt(3, dados.passos);
            statement.setInt(4, dados.uns);
            statement.setInt(5, dados.tam_fita);
            statement.addBatch();
        }
        statement.executeBatch();
        c.commit();
        c.close();
    }
    
     public static List<MTdados> consultaExecucao() throws Exception {
        Connection c = getConexao();
        List<MTdados> dados = new ArrayList<>();
        PreparedStatement statement = c.prepareStatement("SELECT ID,PAROU,PASSOS,UNS FROM TB_EXECUCOES");
        ResultSet rs = statement.executeQuery();
        while (rs.next()) {
            dados.add(new MTdados(rs.getInt(1),rs.getBoolean(2),rs.getInt(3),rs.getInt(4), rs.getInt(5)));
        }
        c.close();
        return dados;
        
    }
    
    public static void limpa() throws Exception {
        Connection c = getConexao();
        PreparedStatement statement = c.prepareStatement("DELETE FROM TB_EXECUCOES");
        statement.execute();
        c.commit();
        c.close();
    }
    
}
