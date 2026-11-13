package br.com.soc.sistema.infra;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Optional;

import br.com.soc.sistema.exception.BusinessException;

public enum PeriodoDisponivel {
	MANHA("1", "Manh\u00E3"),
	TARDE("2", "Tarde"),
	AMBOS("3", "Ambos");
	
	private String codigo;
	private String descricao;
	
	private PeriodoDisponivel(String codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}
	
	public static PeriodoDisponivel buscarPor(String codigo) {
		if(codigo == null || codigo.trim().isEmpty())
			throw new BusinessException("Período disponível deve ser informado.");
		
		return buscarOpcional(codigo)
				.orElseThrow(() -> new BusinessException("Período disponível inválido."));
	}
	
	public static Optional<PeriodoDisponivel> buscarOpcional(String codigo){
		return Arrays.stream(values())
				.filter(periodo -> periodo.getCodigo().equals(codigo))
				.findFirst();
	}

	public boolean aceita(LocalTime horario) {
		switch (this) {
			case MANHA:
				return horario.isBefore(LocalTime.NOON);
			case TARDE:
				return !horario.isBefore(LocalTime.NOON);
			default:
				return true;
		}
	}
	
	public String getCodigo() {
		return codigo;
	}
	
	public String getDescricao() {
		return descricao;
	}
}
