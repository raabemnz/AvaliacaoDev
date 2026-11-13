package br.com.soc.sistema.report;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.util.Collections;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import br.com.soc.sistema.BaseBancoTest;
import br.com.soc.sistema.business.CompromissoBusiness;

public class RelatorioCompromissoExcelTest extends BaseBancoTest {

	private static String texto(Row linha, int coluna) {
		return new DataFormatter().formatCellValue(linha.getCell(coluna));
	}

	@Test
	public void geraPlanilhaComTituloPeriodoCabecalhoELinhas() throws Exception {
		byte[] arquivo = RelatorioCompromissoExcel.gerar(
				new CompromissoBusiness().gerarRelatorio("2026-11-10", "2026-11-11"), "10/11/2026 a 11/11/2026");

		try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(arquivo))) {
			Sheet planilha = workbook.getSheetAt(0);

			assertEquals("Compromissos", planilha.getSheetName());
			assertEquals("Relatório de Compromissos", texto(planilha.getRow(0), 0));
			assertEquals("Período: 10/11/2026 a 11/11/2026", texto(planilha.getRow(1), 0));

			Row cabecalho = planilha.getRow(3);
			assertEquals("Código do Funcionário", texto(cabecalho, 0));
			assertEquals("Nome do Funcionário", texto(cabecalho, 1));
			assertEquals("Código da Agenda", texto(cabecalho, 2));
			assertEquals("Nome da Agenda", texto(cabecalho, 3));
			assertEquals("Data", texto(cabecalho, 4));
			assertEquals("Hora", texto(cabecalho, 5));

			Row primeira = planilha.getRow(4);
			assertEquals("1", texto(primeira, 0));
			assertEquals("João", texto(primeira, 1));
			assertEquals("1", texto(primeira, 2));
			assertEquals("Reuniao de equipe", texto(primeira, 3));
			assertEquals("10/11/2026", texto(primeira, 4));
			assertEquals("09:00", texto(primeira, 5));

			assertEquals("18:00", texto(planilha.getRow(6), 5));
			assertEquals("Total de compromissos: 3", texto(planilha.getRow(8), 0));
		}
	}

	@Test
	public void relatorioSemCompromissosGeraArquivoValidoComTotalZero() throws Exception {
		byte[] arquivo = RelatorioCompromissoExcel.gerar(Collections.emptyList(), "01/12/2026 a 31/12/2026");

		assertNotNull(arquivo);
		try (XSSFWorkbook workbook = new XSSFWorkbook(new ByteArrayInputStream(arquivo))) {
			Sheet planilha = workbook.getSheetAt(0);

			assertEquals("Total de compromissos: 0", texto(planilha.getRow(5), 0));
		}
	}
}
