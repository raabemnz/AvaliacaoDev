package br.com.soc.sistema.soap;

import java.util.function.Supplier;

import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFactory;
import javax.xml.ws.soap.SOAPFaultException;

import br.com.soc.sistema.business.FuncionarioBusiness;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.vo.FuncionarioVo;

@WebService(endpointInterface = "br.com.soc.sistema.soap.WebServiceFuncionarios" )
public class WebServiceFuncionariosImpl implements WebServiceFuncionarios {

	private FuncionarioBusiness business;
	
	public WebServiceFuncionariosImpl() {
		this.business = new FuncionarioBusiness();
	}
	
	@Override
	public String buscarFuncionario(String codigo) {
		return executar(() -> {
			FuncionarioVo funcionarioVo = business.buscarFuncionarioPor(codigo);
			
			if(funcionarioVo == null)
				throw new BusinessException(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO);
			
			return funcionarioVo.toString();
		});
	}
	
	@Override
	public FuncionarioResponse incluirFuncionario(String nome) {
		return executar(() -> {
			FuncionarioVo funcionarioVo = new FuncionarioVo(null, nome);
			
			business.salvarFuncionario(funcionarioVo);
			
			return new FuncionarioResponse(funcionarioVo.getRowid(), funcionarioVo.getNome());
		});
	}
	
	@Override
	public FuncionarioResponse alterarFuncionario(String codigo, String nome) {
		return executar(() -> {
			FuncionarioVo funcionarioVo = new FuncionarioVo(codigo, nome);
			
			business.alterarFuncionario(funcionarioVo);
			
			return new FuncionarioResponse(funcionarioVo.getRowid(), funcionarioVo.getNome());
		});
	}
	
	@Override
	public String excluirFuncionario(String codigo) {
		return executar(() -> {
			business.excluirFuncionario(codigo);
			
			return "Funcionário " + codigo + " excluído com sucesso.";
		});
	}

	private <T> T executar(Supplier<T> operacao) {
		try {
			return operacao.get();
		}catch (BusinessException e) {
			throw faultDeCliente(e);
		}
	}
	
	private SOAPFaultException faultDeCliente(BusinessException e) {
		try {
			QName codigoClient = new QName(SOAPConstants.URI_NS_SOAP_1_1_ENVELOPE, "Client", "S");
			
			return new SOAPFaultException(SOAPFactory.newInstance().createFault(e.getMessage(), codigoClient));
		}catch (SOAPException ex) {
			throw new TechnicalException("Erro ao montar a resposta de erro do Web Service", ex);
		}
	}
}
