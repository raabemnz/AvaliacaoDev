package br.com.soc.sistema.business;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.dao.AgendaDao;
import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.BuscaFilter;
import br.com.soc.sistema.infra.PeriodoDisponivel;
import br.com.soc.sistema.vo.AgendaVo;

import static br.com.soc.sistema.infra.Limites.TAMANHO_MAXIMO_NOME;
import static br.com.soc.sistema.infra.Mensagens.FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO;
import static br.com.soc.sistema.infra.Mensagens.NOME_EXCEDE_O_TAMANHO_MAXIMO;
import static br.com.soc.sistema.infra.Mensagens.NOME_NAO_PODE_SER_EM_BRANCO;

public class AgendaBusiness {

	public static final String AGENDA_NAO_ENCONTRADA = "Agenda não encontrada.";
	private AgendaDao dao;
	private CompromissoDao compromissoDao;
	
	public AgendaBusiness() {
		this.dao = new AgendaDao();
		this.compromissoDao = new CompromissoDao();
	}
	
	public List<AgendaVo> trazerTodasAsAgendas(){
		return dao.findAllAgendas();
	}
	
	public void salvarAgenda(AgendaVo agendaVo) {
		validarAgenda(agendaVo);
		
		dao.insertAgenda(agendaVo);
	}
	
	public List<AgendaVo> filtrarAgendas(BuscaFilter filter){
		List<AgendaVo> agendas = new ArrayList<>();
		
		switch (filter.getOpcoesCombo()) {
			case ID:
				try {
					Integer codigo = Integer.parseInt(filter.getValorBusca());
					AgendaVo encontrada = dao.findByCodigo(codigo);
					
					if(encontrada != null)
						agendas.add(encontrada);
				}catch (NumberFormatException e) {
					throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
				}
			break;

			case NOME:
				agendas.addAll(dao.findAllByNome(filter.getValorBusca()));
			break;
		}
		
		return agendas;
	}
	
	public AgendaVo buscarAgendaPor(String codigo) {
		AgendaVo agenda;
		
		try {
			Integer cod = Integer.parseInt(codigo);
			agenda = dao.findByCodigo(cod);
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
		
		if(agenda == null)
			throw new BusinessException(AGENDA_NAO_ENCONTRADA);
		
		return agenda;
	}
	
	public void alterarAgenda(AgendaVo agendaVo) {
		validarAgenda(agendaVo);
		
		try {
			Integer.parseInt(agendaVo.getRowid());
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
		
		validarNovoPeriodoComCompromissos(agendaVo);
		
		if(dao.updateAgenda(agendaVo) == 0)
			throw new BusinessException(AGENDA_NAO_ENCONTRADA);
	}
	
	public void excluirAgenda(String codigo) {
		int excluidas;
		
		try {
			Integer cod = Integer.parseInt(codigo);

			if(compromissoDao.countByAgenda(cod) > 0)
				throw new BusinessException("Não é possível excluir a agenda: existem compromissos cadastrados.");
			
			excluidas = dao.deleteAgenda(cod);
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
		
		if(excluidas == 0)
			throw new BusinessException(AGENDA_NAO_ENCONTRADA);
	}

	private void validarNovoPeriodoComCompromissos(AgendaVo agendaNova) {
		Integer codigo = Integer.parseInt(agendaNova.getRowid());
		
		AgendaVo agendaAtual = dao.findByCodigo(codigo);
		if(agendaAtual == null)
			throw new BusinessException(AGENDA_NAO_ENCONTRADA);
		
		if(agendaAtual.getPeriodoDisponivel().equals(agendaNova.getPeriodoDisponivel()))
			return;
		
		PeriodoDisponivel novoPeriodo = PeriodoDisponivel.buscarPor(agendaNova.getPeriodoDisponivel());
		
		long foraDaDisponibilidade = compromissoDao.findAllByAgenda(codigo).stream()
				.filter(compromisso -> !novoPeriodo.aceita(LocalTime.parse(compromisso.getHorario())))
				.count();
		
		if(foraDaDisponibilidade > 0)
			throw new BusinessException("Não é possível alterar o período disponível para " + novoPeriodo.getDescricao()
					+ ": existe(m) " + foraDaDisponibilidade + " compromisso(s) fora dessa disponibilidade.");
	}
	
	private void validarAgenda(AgendaVo agendaVo) {
		if(agendaVo.getNome() == null || agendaVo.getNome().trim().isEmpty())
			throw new BusinessException(NOME_NAO_PODE_SER_EM_BRANCO);
		
		if(agendaVo.getNome().length() > TAMANHO_MAXIMO_NOME)
			throw new BusinessException(NOME_EXCEDE_O_TAMANHO_MAXIMO);
		
		PeriodoDisponivel.buscarPor(agendaVo.getPeriodoDisponivel());
	}
}
