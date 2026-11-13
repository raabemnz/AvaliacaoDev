package br.com.soc.sistema.vo;

public class FuncionarioVo {
	private String rowid;
	private String nome;	
	
	public FuncionarioVo() {
		// Construtor sem argumentos exigido pelo Struts, que instancia o Vo e preenche os campos pelos setters.
	}
		
	public FuncionarioVo(String rowid, String nome) {
		this.rowid = normalizarCodigo(rowid);
		this.nome = nome;
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

	private static String normalizarCodigo(String codigo) {
		return (codigo == null || codigo.trim().isEmpty()) ? null : codigo;
	}
	
	@Override
	public String toString() {
		return "FuncionarioVo [rowid=" + rowid + ", nome=" + nome + "]";
	}
}
