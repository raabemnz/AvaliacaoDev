package br.com.soc.sistema.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.soc.sistema.business.AgendaBusiness;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.BuscaFilter;
import br.com.soc.sistema.infra.Action;
import br.com.soc.sistema.infra.OpcoesComboBuscar;
import br.com.soc.sistema.infra.PeriodoDisponivel;
import br.com.soc.sistema.vo.AgendaVo;

import static br.com.soc.sistema.infra.Mensagens.SELECIONE_O_TIPO_DE_BUSCA;

public class AgendaAction extends Action {
	
	
	private List<AgendaVo> agendas = new ArrayList<>();
	private AgendaBusiness business = new AgendaBusiness();
	private BuscaFilter filtrar = new BuscaFilter();
	private AgendaVo agendaVo = new AgendaVo();
	
	public String todos() {
		agendas.addAll(business.trazerTodasAsAgendas());
		
		return SUCCESS;
	}
	
	public String filtrar() {
		if(filtrar.isNullOpcoesCombo())
			return voltarParaListagem(SELECIONE_O_TIPO_DE_BUSCA);
		
		try {
			agendas = business.filtrarAgendas(filtrar);
			
			if(agendas.isEmpty())
				addActionError(AgendaBusiness.AGENDA_NAO_ENCONTRADA);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
		}
		
		return SUCCESS;
	}
	
	public String novo() {
		if(!isSubmissao())
			return INPUT;
		
		try {
			business.salvarAgenda(agendaVo);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		return REDIRECT;
	}
	
	public String editar() {
		if(agendaVo.getRowid() == null)
			return REDIRECT;
		
		try {
			agendaVo = business.buscarAgendaPor(agendaVo.getRowid());
		}catch (BusinessException e) {
			return voltarParaListagem(e.getMessage());
		}
		
		return INPUT;
	}
	
	public String salvarAlteracao() {
		if(agendaVo.getRowid() == null)
			return REDIRECT;
		
		try {
			business.alterarAgenda(agendaVo);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		return REDIRECT;
	}
	
	public String excluir() {
		if(!isSubmissao())
			return REDIRECT;
		
		if(agendaVo.getRowid() == null)
			return REDIRECT;
		
		try {
			business.excluirAgenda(agendaVo.getRowid());
		}catch (BusinessException e) {
			return voltarParaListagem(e.getMessage());
		}
		
		return REDIRECT;
	}

	private String voltarParaListagem(String mensagem) {
		addActionError(mensagem);
		agendas.addAll(business.trazerTodasAsAgendas());
		
		return SUCCESS;
	}
	
	public List<OpcoesComboBuscar> getListaOpcoesCombo(){
		return Arrays.asList(OpcoesComboBuscar.values());
	}
	
	public List<PeriodoDisponivel> getListaPeriodos(){
		return Arrays.asList(PeriodoDisponivel.values());
	}
	
	public List<AgendaVo> getAgendas() {
		return agendas;
	}

	public void setAgendas(List<AgendaVo> agendas) {
		this.agendas = agendas;
	}

	public BuscaFilter getFiltrar() {
		return filtrar;
	}

	public void setFiltrar(BuscaFilter filtrar) {
		this.filtrar = filtrar;
	}

	public AgendaVo getAgendaVo() {
		return agendaVo;
	}

	public void setAgendaVo(AgendaVo agendaVo) {
		this.agendaVo = agendaVo;
	}
}
