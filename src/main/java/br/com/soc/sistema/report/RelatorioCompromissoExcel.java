package br.com.soc.sistema.report;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.vo.CompromissoVo;

public final class RelatorioCompromissoExcel {
	
	private static final String[] CABECALHOS = {
		"C\u00F3digo do Funcion\u00E1rio", "Nome do Funcion\u00E1rio", 
		"C\u00F3digo da Agenda", "Nome da Agenda", 
		"Data", "Hora"
	};

	private static final int[] LARGURAS = {22, 40, 20, 40, 14, 10};
	
	private static final int LINHA_CABECALHO = 3;
	
	private RelatorioCompromissoExcel() {}
	
	public static byte[] gerar(List<CompromissoVo> compromissos, String periodo) {
		try(Workbook workbook = new XSSFWorkbook();
			ByteArrayOutputStream saida = new ByteArrayOutputStream()){
			
			Sheet planilha = workbook.createSheet("Compromissos");
			
			CellStyle estiloTitulo = criarEstiloTitulo(workbook);
			CellStyle estiloCabecalho = criarEstiloCabecalho(workbook);
			CellStyle estiloData = criarEstiloData(workbook);
			
			criarTitulo(planilha, estiloTitulo, periodo);
			criarCabecalho(planilha, estiloCabecalho);
			
			int numeroLinha = LINHA_CABECALHO + 1;
			for (CompromissoVo compromisso : compromissos) {
				criarLinha(planilha.createRow(numeroLinha++), compromisso, estiloData);
			}
			
			Row total = planilha.createRow(numeroLinha + 1);
			total.createCell(0).setCellValue("Total de compromissos: " + compromissos.size());
			
			for (int coluna = 0; coluna < LARGURAS.length; coluna++) {
				planilha.setColumnWidth(coluna, LARGURAS[coluna] * 256);
			}
			
			workbook.write(saida);
			return saida.toByteArray();
		}catch (IOException e) {
			throw new TechnicalException("Erro ao gerar o arquivo Excel do relatorio", e);
		}
	}
	
	private static void criarTitulo(Sheet planilha, CellStyle estiloTitulo, String periodo) {
		Cell titulo = planilha.createRow(0).createCell(0);
		titulo.setCellValue("Relat\u00F3rio de Compromissos");
		titulo.setCellStyle(estiloTitulo);
		
		planilha.createRow(1).createCell(0).setCellValue("Per\u00EDodo: " + periodo);
	}
	
	private static void criarCabecalho(Sheet planilha, CellStyle estiloCabecalho) {
		Row cabecalho = planilha.createRow(LINHA_CABECALHO);
		
		for (int coluna = 0; coluna < CABECALHOS.length; coluna++) {
			Cell celula = cabecalho.createCell(coluna);
			celula.setCellValue(CABECALHOS[coluna]);
			celula.setCellStyle(estiloCabecalho);
		}
	}
	
	private static void criarLinha(Row linha, CompromissoVo compromisso, CellStyle estiloData) {
		int coluna = 0;
		
		linha.createCell(coluna++).setCellValue(Long.parseLong(compromisso.getCdFuncionario()));
		linha.createCell(coluna++).setCellValue(compromisso.getNomeFuncionario());
		linha.createCell(coluna++).setCellValue(Long.parseLong(compromisso.getCdAgenda()));
		linha.createCell(coluna++).setCellValue(compromisso.getNomeAgenda());
		
		Cell data = linha.createCell(coluna++);
		data.setCellValue(java.sql.Date.valueOf(LocalDate.parse(compromisso.getData())));
		data.setCellStyle(estiloData);
		
		linha.createCell(coluna).setCellValue(compromisso.getHorarioFormatado());
	}
	
	private static CellStyle criarEstiloTitulo(Workbook workbook) {
		Font fonte = workbook.createFont();
		fonte.setBold(true);
		fonte.setFontHeightInPoints((short) 14);
		
		CellStyle estilo = workbook.createCellStyle();
		estilo.setFont(fonte);
		return estilo;
	}
	
	private static CellStyle criarEstiloCabecalho(Workbook workbook) {
		Font fonte = workbook.createFont();
		fonte.setBold(true);
		
		CellStyle estilo = workbook.createCellStyle();
		estilo.setFont(fonte);
		estilo.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		estilo.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		estilo.setBorderBottom(BorderStyle.THIN);
		return estilo;
	}
	
	private static CellStyle criarEstiloData(Workbook workbook) {
		CellStyle estilo = workbook.createCellStyle();
		estilo.setDataFormat(workbook.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy"));
		return estilo;
	}
}
