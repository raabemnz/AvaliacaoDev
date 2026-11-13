package br.com.soc.sistema.business;

import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.dao.FuncionarioDao;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.BuscaFilter;
import br.com.soc.sistema.vo.FuncionarioVo;

import static br.com.soc.sistema.infra.Limites.TAMANHO_MAXIMO_NOME;
import static br.com.soc.sistema.infra.Mensagens.FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO;
import static br.com.soc.sistema.infra.Mensagens.NOME_EXCEDE_O_TAMANHO_MAXIMO;
import static br.com.soc.sistema.infra.Mensagens.NOME_NAO_PODE_SER_EM_BRANCO;

public class FuncionarioBusiness {

	public static final String FUNCIONARIO_NAO_ENCONTRADO = "Funcionário não encontrado.";
	private FuncionarioDao dao;
	
	public FuncionarioBusiness() {
		this.dao = new FuncionarioDao();
	}
	
	public List<FuncionarioVo> trazerTodosOsFuncionarios(){
		return dao.findAllFuncionarios();
	}	
	
	public void salvarFuncionario(FuncionarioVo funcionarioVo) {
		validarNome(funcionarioVo);
		
		funcionarioVo.setRowid(dao.insertFuncionario(funcionarioVo));
	}	
	
	private void validarNome(FuncionarioVo funcionarioVo) {
		if(funcionarioVo.getNome() == null || funcionarioVo.getNome().trim().isEmpty())
			throw new BusinessException(NOME_NAO_PODE_SER_EM_BRANCO);
		
		if(funcionarioVo.getNome().length() > TAMANHO_MAXIMO_NOME)
			throw new BusinessException(NOME_EXCEDE_O_TAMANHO_MAXIMO);
	}
	
	public List<FuncionarioVo> filtrarFuncionarios(BuscaFilter filter){
		List<FuncionarioVo> funcionarios = new ArrayList<>();
		
		switch (filter.getOpcoesCombo()) {
			case ID:
				try {
					Integer codigo = Integer.parseInt(filter.getValorBusca());
					FuncionarioVo encontrado = dao.findByCodigo(codigo);
					
					if(encontrado != null)
						funcionarios.add(encontrado);
				}catch (NumberFormatException e) {
					throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
				}
			break;

			case NOME:
				funcionarios.addAll(dao.findAllByNome(filter.getValorBusca()));
			break;
		}
		
		return funcionarios;
	}
	
	public FuncionarioVo buscarFuncionarioPor(String codigo) {
		try {
			Integer cod = Integer.parseInt(codigo);
			return dao.findByCodigo(cod);
		}catch (NumberFormatException e) {
			throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
		}
	}
	
	public void alterarFuncionario(FuncionarioVo funcionarioVo) {
	    validarNome(funcionarioVo);

	    try {
	        Integer.parseInt(funcionarioVo.getRowid());
	    }catch (NumberFormatException e) {
	        throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
	    }

	    if(dao.updateFuncionario(funcionarioVo) == 0)
	        throw new BusinessException(FUNCIONARIO_NAO_ENCONTRADO);
	}
	
	public void excluirFuncionario(String codigo) {
	    int excluidos;
	    
	    try {
	        Integer cod = Integer.parseInt(codigo);
	        excluidos = dao.deleteFuncionario(cod);
	    }catch (NumberFormatException e) {
	        throw new BusinessException(FOI_INFORMADO_CARACTER_NO_LUGAR_DE_UM_NUMERO);
	    }
	    
	    if(excluidos == 0)
	        throw new BusinessException(FUNCIONARIO_NAO_ENCONTRADO);
	}


}
