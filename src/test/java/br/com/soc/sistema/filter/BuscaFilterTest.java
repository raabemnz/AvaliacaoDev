package br.com.soc.sistema.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.infra.OpcoesComboBuscar;

public class BuscaFilterTest {

	@Test
	public void escolhaVaziaSignificaNenhumTipoDeBuscaSelecionado() {
		assertTrue(new BuscaFilter().setOpcoesCombo("").isNullOpcoesCombo());
		assertTrue(new BuscaFilter().setOpcoesCombo("   ").isNullOpcoesCombo());
		assertTrue(new BuscaFilter().setOpcoesCombo(null).isNullOpcoesCombo());
		assertTrue(new BuscaFilter().isNullOpcoesCombo());
	}

	@Test
	public void identificaOTipoDeBuscaPeloCodigo() {
		assertEquals(OpcoesComboBuscar.ID, new BuscaFilter().setOpcoesCombo("1").getOpcoesCombo());
		assertEquals(OpcoesComboBuscar.NOME, new BuscaFilter().setOpcoesCombo("2").getOpcoesCombo());
		assertFalse(new BuscaFilter().setOpcoesCombo("1").isNullOpcoesCombo());
	}

	@Test
	public void recusaCodigoDeBuscaInexistente() {
		BuscaFilter filtro = new BuscaFilter();

		BusinessException e = assertThrows(BusinessException.class, () -> filtro.setOpcoesCombo("9"));

		assertEquals("Codigo informado nao existe", e.getMessage());
	}

	@Test
	public void guardaOValorDigitado() {
		BuscaFilter filtro = new BuscaFilter().setOpcoesCombo("2").setValorBusca("joao");

		assertEquals("joao", filtro.getValorBusca());
	}
}
