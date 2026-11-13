package br.com.soc.sistema.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.soc.sistema.business.CompromissoBusiness;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.BuscaFilter;
import br.com.soc.sistema.infra.Action;
import br.com.soc.sistema.infra.OpcoesComboBuscar;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.FuncionarioVo;

import static br.com.soc.sistema.infra.Mensagens.SELECIONE_O_TIPO_DE_BUSCA;

public class CompromissoAction extends Action {
	
	
	private List<CompromissoVo> compromissos = new ArrayList<>();
	private CompromissoBusiness business = new CompromissoBusiness();
	private BuscaFilter filtrar = new BuscaFilter();
	private CompromissoVo compromissoVo = new CompromissoVo();
	
	public String todos() {
		compromissos.addAll(business.trazerTodosOsCompromissos());
		
		return SUCCESS;
	}
	
	public String filtrar() {
		if(filtrar.isNullOpcoesCombo())
			return voltarParaListagem(SELECIONE_O_TIPO_DE_BUSCA);
		
		try {
			compromissos = business.filtrarCompromissos(filtrar);
			
			if(compromissos.isEmpty())
				addActionError(CompromissoBusiness.COMPROMISSO_NAO_ENCONTRADO);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
		}
		
		return SUCCESS;
	}
	
	public String novo() {
		if(!isSubmissao())
			return INPUT;
		
		try {
			business.salvarCompromisso(compromissoVo);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		return REDIRECT;
	}
	
	public String editar() {
		if(compromissoVo.getRowid() == null)
			return REDIRECT;
		
		try {
			compromissoVo = business.buscarCompromissoPor(compromissoVo.getRowid());
		}catch (BusinessException e) {
			return voltarParaListagem(e.getMessage());
		}
		
		return INPUT;
	}
	
	public String salvarAlteracao() {
		if(compromissoVo.getRowid() == null)
			return REDIRECT;
		
		try {
			business.alterarCompromisso(compromissoVo);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		return REDIRECT;
	}
	
	public String excluir() {
		if(!isSubmissao())
			return REDIRECT;
		
		if(compromissoVo.getRowid() == null)
			return REDIRECT;
		
		try {
			business.excluirCompromisso(compromissoVo.getRowid());
		}catch (BusinessException e) {
			return voltarParaListagem(e.getMessage());
		}
		
		return REDIRECT;
	}

	private String voltarParaListagem(String mensagem) {
		addActionError(mensagem);
		compromissos.addAll(business.trazerTodosOsCompromissos());
		
		return SUCCESS;
	}
	
	public List<OpcoesComboBuscar> getListaOpcoesCombo(){
		return Arrays.asList(OpcoesComboBuscar.values());
	}
	
	public List<FuncionarioVo> getListaFuncionarios(){
		return business.trazerFuncionarios();
	}
	
	public List<AgendaVo> getListaAgendas(){
		return business.trazerAgendas();
	}
	
	public List<CompromissoVo> getCompromissos() {
		return compromissos;
	}

	public void setCompromissos(List<CompromissoVo> compromissos) {
		this.compromissos = compromissos;
	}

	public BuscaFilter getFiltrar() {
		return filtrar;
	}

	public void setFiltrar(BuscaFilter filtrar) {
		this.filtrar = filtrar;
	}

	public CompromissoVo getCompromissoVo() {
		return compromissoVo;
	}

	public void setCompromissoVo(CompromissoVo compromissoVo) {
		this.compromissoVo = compromissoVo;
	}
}
