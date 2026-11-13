package br.com.soc.sistema.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import br.com.soc.sistema.BaseBancoTest;

public class RelatorioActionTest extends BaseBancoTest {

	private RelatorioAction acao(String dataInicial, String dataFinal) {
		RelatorioAction action = new RelatorioAction();
		action.setDataInicial(dataInicial);
		action.setDataFinal(dataFinal);
		return action;
	}

	@Test
	public void abrirATelaDeFiltrosNaoGeraRelatorio() {
		RelatorioAction action = new RelatorioAction();

		assertEquals("success", action.compromissos());
		assertTrue(action.getCompromissos().isEmpty());
	}

	@Test
	public void geraORelatorioEmHtmlComOsCompromissosDoPeriodo() {
		RelatorioAction action = acao("2026-11-10", "2026-11-10");

		assertEquals("html", action.visualizarCompromissos());
		assertEquals(2, action.getCompromissos().size());
		assertEquals("10/11/2026 a 10/11/2026", action.getPeriodoFormatado());
	}

	@Test
	public void periodoInvalidoVoltaParaOsFiltrosComMensagem() {
		RelatorioAction vazio = acao("", "2026-11-10");
		RelatorioAction invertido = acao("2026-11-11", "2026-11-10");

		assertEquals("success", vazio.visualizarCompromissos());
		assertEquals("Data inicial deve ser informada.", vazio.getActionErrors().iterator().next());
		assertEquals("success", invertido.visualizarCompromissos());
		assertEquals("Data final não pode ser anterior à data inicial.", invertido.getActionErrors().iterator().next());
	}

	@Test
	public void exportaOExcelComNomeDeArquivoFixo() {
		RelatorioAction action = acao("2026-11-10", "2026-11-11");

		assertEquals("excel", action.exportarCompromissos());
		assertEquals("relatorio-compromissos.xlsx", action.getNomeArquivo());
		assertNotNull(action.getArquivoExcel());
	}

	@Test
	public void exportacaoComPeriodoInvalidoNaoGeraArquivo() {
		RelatorioAction action = acao("2026-11-11", "2026-11-10");

		assertEquals("success", action.exportarCompromissos());
		assertNull(action.getArquivoExcel());
		assertEquals(1, action.getActionErrors().size());
	}
}
