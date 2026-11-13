package br.com.soc.sistema.soap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import javax.xml.ws.soap.SOAPFaultException;

import org.junit.Before;
import org.junit.Test;

import br.com.soc.sistema.BaseBancoTest;
import br.com.soc.sistema.business.FuncionarioBusiness;

public class WebServiceFuncionariosTest extends BaseBancoTest {

	private WebServiceFuncionariosImpl ws;

	@Before
	public void criarServico() {
		ws = new WebServiceFuncionariosImpl();
	}
	
	private void assertFalhaDoCliente(String mensagemEsperada, Runnable chamada) {
		SOAPFaultException fault = assertThrows(SOAPFaultException.class, chamada::run);

		assertEquals("Client", fault.getFault().getFaultCodeAsQName().getLocalPart());
		assertEquals(mensagemEsperada, fault.getFault().getFaultString());
	}

	@Test
	public void incluiEDevolveCodigoENome() {
		FuncionarioResponse resposta = ws.incluirFuncionario("Ana");

		assertEquals("5", resposta.getCodigo());
		assertEquals("Ana", resposta.getNome());
		assertTrue(ws.buscarFuncionario("5").contains("nome=Ana"));
	}

	@Test
	public void alteraEDevolveCodigoENome() {
		FuncionarioResponse resposta = ws.alterarFuncionario("2", "Maria Silva");

		assertEquals("2", resposta.getCodigo());
		assertEquals("Maria Silva", resposta.getNome());
		assertTrue(ws.buscarFuncionario("2").contains("nome=Maria Silva"));
	}

	@Test
	public void excluiEConfirma() {
		assertEquals("Funcionário 4 excluído com sucesso.", ws.excluirFuncionario("4"));

		assertFalhaDoCliente(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO, () -> ws.buscarFuncionario("4"));
	}

	@Test
	public void inclusaoComNomeInvalidoVoltaComoFalhaDoCliente() {
		assertFalhaDoCliente("Nome não pode ser em branco.", () -> ws.incluirFuncionario(" "));
		assertFalhaDoCliente("Nome não pode ser em branco.", () -> ws.incluirFuncionario(null));
	}

	@Test
	public void alteracaoDeFuncionarioInexistenteVoltaComoFalhaDoCliente() {
		assertFalhaDoCliente(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO, () -> ws.alterarFuncionario("999", "X"));
	}

	@Test
	public void codigoComTextoVoltaComoFalhaDoCliente() {
		String mensagem = "Foi informado um caracter no lugar de um número.";

		assertFalhaDoCliente(mensagem, () -> ws.buscarFuncionario("Flavia"));
		assertFalhaDoCliente(mensagem, () -> ws.alterarFuncionario("abc", "X"));
		assertFalhaDoCliente(mensagem, () -> ws.excluirFuncionario("abc"));
	}

	@Test
	public void buscaDeFuncionarioInexistenteVoltaComoFalhaDoClienteEmVezDeErroGenerico() {
		assertFalhaDoCliente(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO, () -> ws.buscarFuncionario("999"));
	}

	@Test
	public void exclusaoDeFuncionarioInexistenteVoltaComoFalhaDoCliente() {
		assertFalhaDoCliente(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO, () -> ws.excluirFuncionario("999"));
	}
}
