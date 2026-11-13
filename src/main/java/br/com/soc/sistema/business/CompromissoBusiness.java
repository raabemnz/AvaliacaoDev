package br.com.soc.sistema.business;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.dao.AgendaDao;
import br.com.soc.sistema.dao.CompromissoDao;
import br.com.soc.sistema.dao.FuncionarioDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.BuscaFilter;
import br.com.soc.sistema.infra.PeriodoDisponivel;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.FuncionarioVo;

import static br.com.soc.sistema.infra.Mensagens.FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO;

public class CompromissoBusiness {

	public static final String COMPROMISSO_NAO_ENCONTRADO = "Compromisso não encontrado.";
	private CompromissoDao dao;
	private FuncionarioDao funcionarioDao;
	private AgendaDao agendaDao;
	
	public CompromissoBusiness() {
		this.dao = new CompromissoDao();
		this.funcionarioDao = new FuncionarioDao();
		this.agendaDao = new AgendaDao();
	}
	
	public List<CompromissoVo> trazerTodosOsCompromissos(){
		return dao.findAllCompromissos();
	}
	
	public List<FuncionarioVo> trazerFuncionarios(){
		return funcionarioDao.findAllFuncionarios();
	}
	
	public List<AgendaVo> trazerAgendas(){
		return agendaDao.findAllAgendas();
	}

	public List<CompromissoVo> gerarRelatorio(String dataInicial, String dataFinal) {
		LocalDate inicio = converterData(dataInicial, "Data inicial");
		LocalDate fim = converterData(dataFinal, "Data final");
		
		if(fim.isBefore(inicio))
			throw new BusinessException("Data final não pode ser anterior à data inicial.");
		
		return dao.findAllByPeriodo(inicio, fim);
	}
	
	public void salvarCompromisso(CompromissoVo compromissoVo) {
		validarCompromisso(compromissoVo);
		
		dao.insertCompromisso(compromissoVo);
	}
	
	public List<CompromissoVo> filtrarCompromissos(BuscaFilter filter){
		List<CompromissoVo> compromissos = new ArrayList<>();
		
		switch (filter.getOpcoesCombo()) {
			case ID:
				try {
					Integer codigo = Integer.parseInt(filter.getValorBusca());
					CompromissoVo encontrado = dao.findByCodigo(codigo);
					
					if(encontrado != null)
						compromissos.add(encontrado);
				}catch (NumberFormatException e) {
					throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
				}
			break;

			case NOME:
				compromissos.addAll(dao.findAllByNomeFuncionario(filter.getValorBusca()));
			break;
		}
		
		return compromissos;
	}
	
	public CompromissoVo buscarCompromissoPor(String codigo) {
		CompromissoVo compromisso;
		
		try {
			Integer cod = Integer.parseInt(codigo);
			compromisso = dao.findByCodigo(cod);
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
		
		if(compromisso == null)
			throw new BusinessException(COMPROMISSO_NAO_ENCONTRADO);
		
		return compromisso;
	}
	
	public void alterarCompromisso(CompromissoVo compromissoVo) {
		validarCompromisso(compromissoVo);
		
		try {
			Integer.parseInt(compromissoVo.getRowid());
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
		
		if(dao.updateCompromisso(compromissoVo) == 0)
			throw new BusinessException(COMPROMISSO_NAO_ENCONTRADO);
	}
	
	public void excluirCompromisso(String codigo) {
		int excluidos;
		
		try {
			Integer cod = Integer.parseInt(codigo);
			excluidos = dao.deleteCompromisso(cod);
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
		
		if(excluidos == 0)
			throw new BusinessException(COMPROMISSO_NAO_ENCONTRADO);
	}
	
	private void validarCompromisso(CompromissoVo compromissoVo) {
		Integer codigoFuncionario = converterCodigo(compromissoVo.getCdFuncionario(), "Funcionário deve ser informado.");
		Integer codigoAgenda = converterCodigo(compromissoVo.getCdAgenda(), "Agenda deve ser informada.");
		converterData(compromissoVo.getData());
		LocalTime horario = converterHorario(compromissoVo.getHorario());
		
		if(funcionarioDao.findByCodigo(codigoFuncionario) == null)
			throw new BusinessException(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO);
		
		AgendaVo agenda = agendaDao.findByCodigo(codigoAgenda);
		if(agenda == null)
			throw new BusinessException(AgendaBusiness.AGENDA_NAO_ENCONTRADA);
		
		validarDisponibilidadeDaAgenda(agenda, horario);
	}

	private void validarDisponibilidadeDaAgenda(AgendaVo agenda, LocalTime horario) {
		PeriodoDisponivel periodo = PeriodoDisponivel.buscarPor(agenda.getPeriodoDisponivel());
		
		if(!periodo.aceita(horario))
			throw new BusinessException("Horário fora da disponibilidade da agenda (período disponível: "
					+ periodo.getDescricao() + ").");
	}
	
	private Integer converterCodigo(String codigo, String mensagemObrigatorio) {
		if(codigo == null || codigo.trim().isEmpty())
			throw new BusinessException(mensagemObrigatorio);
		
		try {
			return Integer.parseInt(codigo);
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
	}
	
	private LocalDate converterData(String data) {
		return converterData(data, "Data");
	}
	
	private LocalDate converterData(String data, String nomeCampo) {
		if(data == null || data.trim().isEmpty())
			throw new BusinessException(nomeCampo + " deve ser informada.");
		
		try {
			return LocalDate.parse(data);
		}catch (DateTimeParseException e) {
			throw new BusinessException(nomeCampo + " inválida.");
		}
	}
	
	private LocalTime converterHorario(String horario) {
		if(horario == null || horario.trim().isEmpty())
			throw new BusinessException("Horário deve ser informado.");
		
		try {
			return LocalTime.parse(horario);
		}catch (DateTimeParseException e) {
			throw new BusinessException("Horário inválido.");
		}
	}
}
