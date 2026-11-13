package br.com.soc.sistema.vo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CompromissoVo {
	private String rowid;
	private String cdFuncionario;
	private String cdAgenda;
	private String data;
	private String horario;

	private String nomeFuncionario;
	private String nomeAgenda;
	
	public CompromissoVo() {
		// Construtor sem argumentos exigido pelo Struts, que instancia o Vo e preenche os campos pelos setters.
	}

	public String getRowid() {
		return rowid;
	}
	public void setRowid(String rowid) {
		this.rowid = (rowid == null || rowid.trim().isEmpty()) ? null : rowid;
	}
	public String getCdFuncionario() {
		return cdFuncionario;
	}
	public void setCdFuncionario(String cdFuncionario) {
		this.cdFuncionario = cdFuncionario;
	}
	public String getCdAgenda() {
		return cdAgenda;
	}
	public void setCdAgenda(String cdAgenda) {
		this.cdAgenda = cdAgenda;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getHorario() {
		return horario;
	}
	public void setHorario(String horario) {
		this.horario = horario;
	}
	public String getNomeFuncionario() {
		return nomeFuncionario;
	}
	public void setNomeFuncionario(String nomeFuncionario) {
		this.nomeFuncionario = nomeFuncionario;
	}
	public String getNomeAgenda() {
		return nomeAgenda;
	}
	public void setNomeAgenda(String nomeAgenda) {
		this.nomeAgenda = nomeAgenda;
	}
	
	public String getFuncionarioDescricao() {
		return cdFuncionario + " - " + nomeFuncionario;
	}
	
	public String getAgendaDescricao() {
		return cdAgenda + " - " + nomeAgenda;
	}
	
	public String getHorarioFormatado() {
		try {
			return LocalTime.parse(horario).format(DateTimeFormatter.ofPattern("HH:mm"));
		}catch (DateTimeParseException | NullPointerException e) {
			return horario;
		}
	}
	
	public String getDataFormatada() {
		try {
			return LocalDate.parse(data).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		}catch (DateTimeParseException | NullPointerException e) {
			return data;
		}
	}
	
	@Override
	public String toString() {
		return "CompromissoVo [rowid=" + rowid + ", cdFuncionario=" + cdFuncionario + ", cdAgenda=" + cdAgenda
				+ ", data=" + data + ", horario=" + horario + "]";
	}
}
