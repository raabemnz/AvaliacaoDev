package br.com.soc.sistema.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.soc.sistema.business.FuncionarioBusiness;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.BuscaFilter;
import br.com.soc.sistema.infra.Action;
import br.com.soc.sistema.infra.OpcoesComboBuscar;
import br.com.soc.sistema.vo.FuncionarioVo;

import static br.com.soc.sistema.infra.Mensagens.SELECIONE_O_TIPO_DE_BUSCA;

public class FuncionarioAction extends Action {
	
	
	private List<FuncionarioVo> funcionarios = new ArrayList<>();
	private FuncionarioBusiness business = new FuncionarioBusiness();
	private BuscaFilter filtrar = new BuscaFilter();
	private FuncionarioVo funcionarioVo = new FuncionarioVo();
	
	public String todos() {
		funcionarios.addAll(business.trazerTodosOsFuncionarios());	

		return SUCCESS;
	}
	
	public String filtrar() {
		if(filtrar.isNullOpcoesCombo())
			return voltarParaListagem(SELECIONE_O_TIPO_DE_BUSCA);
		
		try {
			funcionarios = business.filtrarFuncionarios(filtrar);
			
			if(funcionarios.isEmpty())
				addActionError(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
		}
		
		return SUCCESS;
	}
	
	public String novo() {
		if(!isSubmissao())
			return INPUT;
		
		try {
			business.salvarFuncionario(funcionarioVo);
		}catch (BusinessException e) {
			addActionError(e.getMessage());
			return INPUT;
		}
		
		return REDIRECT;
	}
	
	public String editar() {
		if(funcionarioVo.getRowid() == null)
			return REDIRECT;
		
		FuncionarioVo encontrado;
		
		try {
			encontrado = business.buscarFuncionarioPor(funcionarioVo.getRowid());
		}catch (BusinessException e) {
			return voltarParaListagem(e.getMessage());
		}
		
		if(encontrado == null)
			return voltarParaListagem(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO);
		
		funcionarioVo = encontrado;
		
		return INPUT;
	}
	
	public String salvarAlteracao() {
	    if(funcionarioVo.getRowid() == null)
	        return REDIRECT;

	    try {
	        business.alterarFuncionario(funcionarioVo);
	    }catch (BusinessException e) {
	        addActionError(e.getMessage());
	        return INPUT;
	    }

	    return REDIRECT;
	}
	
	public String excluir() {
		if(!isSubmissao())
			return REDIRECT;
		
	    if(funcionarioVo.getRowid() == null)
	        return REDIRECT;

	    try {
	        business.excluirFuncionario(funcionarioVo.getRowid());
	    }catch (BusinessException e) {
	        return voltarParaListagem(e.getMessage());
	    }

	    return REDIRECT;
	}

	private String voltarParaListagem(String mensagem) {
		addActionError(mensagem);
		funcionarios.addAll(business.trazerTodosOsFuncionarios());
		
		return SUCCESS;
	}

	
	public List<OpcoesComboBuscar> getListaOpcoesCombo(){
		return Arrays.asList(OpcoesComboBuscar.values());
	}
	
	public List<FuncionarioVo> getFuncionarios() {
		return funcionarios;
	}

	public void setFuncionarios(List<FuncionarioVo> funcionarios) {
		this.funcionarios = funcionarios;
	}

	public BuscaFilter getFiltrar() {
		return filtrar;
	}

	public void setFiltrar(BuscaFilter filtrar) {
		this.filtrar = filtrar;
	}

	public FuncionarioVo getFuncionarioVo() {
		return funcionarioVo;
	}

	public void setFuncionarioVo(FuncionarioVo funcionarioVo) {
		this.funcionarioVo = funcionarioVo;
	}
}
