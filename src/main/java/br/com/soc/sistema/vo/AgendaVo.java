package br.com.soc.sistema.vo;

import br.com.soc.sistema.infra.PeriodoDisponivel;

public class AgendaVo {
	private String rowid;
	private String nome;
	private String periodoDisponivel;
	
	public AgendaVo() {
		// Construtor sem argumentos exigido pelo Struts, que instancia o Vo e preenche os campos pelos setters.
	}
	
	public AgendaVo(String rowid, String nome, String periodoDisponivel) {
		this.rowid = normalizarCodigo(rowid);
		this.nome = nome;
		this.periodoDisponivel = periodoDisponivel;
	}

	public String getRowid() {
		return rowid;
	}
	public void setRowid(String rowid) {
		this.rowid = normalizarCodigo(rowid);
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getPeriodoDisponivel() {
		return periodoDisponivel;
	}
	public void setPeriodoDisponivel(String periodoDisponivel) {
		this.periodoDisponivel = periodoDisponivel;
	}
	
	public String getDescricaoPeriodo() {
		return PeriodoDisponivel.buscarOpcional(periodoDisponivel)
				.map(PeriodoDisponivel::getDescricao)
				.orElse("");
	}

	private static String normalizarCodigo(String codigo) {
		return (codigo == null || codigo.trim().isEmpty()) ? null : codigo;
	}
	
	@Override
	public String toString() {
		return "AgendaVo [rowid=" + rowid + ", nome=" + nome + ", periodoDisponivel=" + periodoDisponivel + "]";
	}
}
