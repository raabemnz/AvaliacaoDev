package br.com.soc.sistema.soap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface WebServiceFuncionarios {
	@WebMethod
	public String buscarFuncionario(String codigo);

	@WebMethod
	public FuncionarioResponse incluirFuncionario(@WebParam(name = "nome") String nome);

	@WebMethod
	public FuncionarioResponse alterarFuncionario(@WebParam(name = "codigo") String codigo, @WebParam(name = "nome") String nome);

	@WebMethod
	public String excluirFuncionario(@WebParam(name = "codigo") String codigo);
}
