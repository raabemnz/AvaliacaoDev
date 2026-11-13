package br.com.soc.sistema.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.business.CompromissoBusiness;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.infra.Action;
import br.com.soc.sistema.report.RelatorioCompromissoExcel;
import br.com.soc.sistema.vo.CompromissoVo;

public class RelatorioAction extends Action {
	
	public static final String HTML = "html";
	public static final String EXCEL = "excel";
	
	private CompromissoBusiness business = new CompromissoBusiness();

	private String dataInicial;
	private String dataFinal;
	
	private List<CompromissoVo> compromissos = new ArrayList<>();

	private InputStream arquivoExcel;
	private String nomeArquivo;

	public String compromissos() {
		return SUCCESS;
	}

	public String visualizarCompromissos() {
		try {
			compromissos = business.gerarRelatorio(dataInicial, dataFinal);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
			return SUCCESS;
		}
		
		return HTML;
	}

	public String exportarCompromissos() {
		try {
			List<CompromissoVo> lista = business.gerarRelatorio(dataInicial, dataFinal);
			
			arquivoExcel = new ByteArrayInputStream(RelatorioCompromissoExcel.gerar(lista, getPeriodoFormatado()));
			nomeArquivo = "relatorio-compromissos.xlsx";
		}catch (BusinessException e) {
			addActionError(e.getMessage());
			return SUCCESS;
		}
		
		return EXCEL;
	}
	
	public String getPeriodoFormatado() {
		return formatar(dataInicial) + " a " + formatar(dataFinal);
	}
	
	private String formatar(String data) {
		try {
			return LocalDate.parse(data).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}catch (DateTimeParseException | NullPointerException e) {
			return data;
		}
	}
	
	public String getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(String dataInicial) {
		this.dataInicial = dataInicial;
	}

	public String getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(String dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<CompromissoVo> getCompromissos() {
		return compromissos;
	}

	public InputStream getArquivoExcel() {
		return arquivoExcel;
	}

	public String getNomeArquivo() {
		return nomeArquivo;
	}
}
