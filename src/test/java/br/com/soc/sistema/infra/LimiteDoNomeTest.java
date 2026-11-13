package br.com.soc.sistema.infra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.junit.Test;

import br.com.soc.sistema.action.AgendaAction;
import br.com.soc.sistema.action.FuncionarioAction;

public class LimiteDoNomeTest {

	private static String conteudo(String caminho) throws Exception {
		return new String(Files.readAllBytes(Paths.get(caminho)), StandardCharsets.UTF_8);
	}

	@Test
	public void asActionsExpoemOLimiteParaAsTelas() {
		assertEquals(Limites.TAMANHO_MAXIMO_NOME, new FuncionarioAction().getTamanhoMaximoNome());
		assertEquals(Limites.TAMANHO_MAXIMO_NOME, new AgendaAction().getTamanhoMaximoNome());
	}

	@Test
	public void aMensagemDoLimiteUsaOMesmoNumero() {
		assertEquals(Mensagens.NOME_EXCEDE_O_TAMANHO_MAXIMO,
				"Nome deve ter no máximo " + Limites.TAMANHO_MAXIMO_NOME + " caracteres.");
	}

	@Test
	public void osFormulariosNaoRepetemOLimiteNoMaxlength() throws Exception {
		for (String tela : new String[] { "src/main/webapp/funcionario/cadFuncionario_i.jsp",
				"src/main/webapp/agenda/cadAgenda_i.jsp" }) {
			String jsp = conteudo(tela);

			assertTrue(tela, jsp.contains("maxlength=\"%{tamanhoMaximoNome}\""));
			assertFalse(tela, jsp.contains("maxlength=\"" + Limites.TAMANHO_MAXIMO_NOME + "\""));
		}
	}
}
