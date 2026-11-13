package br.com.soc.sistema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Restaura o banco H2 em memoria ao estado inicial (o do script de carga).
 * Os Daos sempre usam o banco "avaliacao", por isso os testes que acessam dados o restauram antes de cada execucao.
 */
public final class BancoEmMemoria {

	// os parametros precisam ser os mesmos do Dao para que seja o mesmo banco
	private static final String URL = "jdbc:h2:mem:avaliacao;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false;";

	private BancoEmMemoria() {}

	public static void restaurar() {
		try (Connection con = DriverManager.getConnection(URL);
			 Statement st = con.createStatement()) {

			st.execute("DROP ALL OBJECTS");
			st.execute("RUNSCRIPT FROM 'classpath:CRIA_TABELAS_E_INSERE_REGISTROS_INICIAIS.sql'");
		} catch (SQLException e) {
			throw new IllegalStateException("Nao foi possivel restaurar o banco de testes", e);
		}
	}
}
