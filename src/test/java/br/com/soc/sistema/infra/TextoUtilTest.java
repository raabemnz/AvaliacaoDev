package br.com.soc.sistema.infra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TextoUtilTest {

	@Test
	public void removeAcentos() {
		assertEquals("Joao Conceicao", TextoUtil.removerAcentos("João Conceição"));
		assertEquals("AEIOU aeiou", TextoUtil.removerAcentos("ÁÉÍÓÚ áéíóú"));
	}

	@Test
	public void textoNuloViraVazio() {
		assertEquals("", TextoUtil.removerAcentos(null));
	}

	@Test
	public void padraoDeBuscaNaoTemAcentoNemMaiuscula() {
		assertEquals("%joao%", TextoUtil.padraoLike("JOÃO"));
	}

	@Test
	public void padraoDeBuscaProtegeOsCuringasDoLike() {
		assertEquals("%jo\\_ao\\%%", TextoUtil.padraoLike("Jo_ão%"));
	}

	@Test
	public void padraoDeBuscaProtegeABarraInvertida() {
		assertEquals("%a\\\\b%", TextoUtil.padraoLike("a\\b"));
	}

	@Test
	public void buscaVaziaCasaComTudo() {
		assertEquals("%%", TextoUtil.padraoLike(""));
		assertEquals("%%", TextoUtil.padraoLike(null));
	}

	@Test
	public void condicaoDeBuscaUsaAColunaSemAcentoEOCaractereDeEscape() {
		String condicao = TextoUtil.condicaoLike("nm_funcionario");

		assertTrue(condicao.startsWith("TRANSLATE(lower(nm_funcionario)"));
		assertTrue(condicao.endsWith(" like ? escape '\\'"));
	}
}
