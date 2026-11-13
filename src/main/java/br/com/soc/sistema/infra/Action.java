package br.com.soc.sistema.infra;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class Action extends ActionSupport {
	public static final String REDIRECT = "redirect";

	protected boolean isSubmissao() {
		return "POST".equalsIgnoreCase(ServletActionContext.getRequest().getMethod());
	}

	public int getTamanhoMaximoNome() {
		return Limites.TAMANHO_MAXIMO_NOME;
	}
}
