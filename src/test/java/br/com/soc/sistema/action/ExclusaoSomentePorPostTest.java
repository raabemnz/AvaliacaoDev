package br.com.soc.sistema.action;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Proxy;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.StrutsStatics;
import org.junit.After;
import org.junit.Test;

import com.opensymphony.xwork2.ActionContext;

import br.com.soc.sistema.BaseBancoTest;
import br.com.soc.sistema.business.AgendaBusiness;
import br.com.soc.sistema.business.CompromissoBusiness;
import br.com.soc.sistema.business.FuncionarioBusiness;
import br.com.soc.sistema.vo.AgendaVo;
import br.com.soc.sistema.vo.CompromissoVo;
import br.com.soc.sistema.vo.FuncionarioVo;

public class ExclusaoSomentePorPostTest extends BaseBancoTest {

	private void simularRequisicao(final String metodoHttp) {
		HttpServletRequest request = (HttpServletRequest) Proxy.newProxyInstance(
				getClass().getClassLoader(), new Class<?>[] { HttpServletRequest.class },
				(proxy, method, args) -> "getMethod".equals(method.getName()) ? metodoHttp : null);

		ActionContext contexto = new ActionContext(new HashMap<String, Object>());
		contexto.put(StrutsStatics.HTTP_REQUEST, request);
		ActionContext.setContext(contexto);
	}

	@After
	public void limparContexto() {
		ActionContext.setContext(null);
	}

	private static CompromissoVo compromissoComCodigo(String codigo) {
		CompromissoVo vo = new CompromissoVo();
		vo.setRowid(codigo);
		return vo;
	}

	@Test
	public void funcionarioNaoEExcluidoPorGet() {
		simularRequisicao("GET");
		FuncionarioAction action = new FuncionarioAction();
		action.setFuncionarioVo(new FuncionarioVo("4", null));

		assertEquals("redirect", action.excluir());
		assertEquals(4, new FuncionarioBusiness().trazerTodosOsFuncionarios().size());
	}

	@Test
	public void funcionarioEExcluidoPorPost() {
		simularRequisicao("POST");
		FuncionarioAction action = new FuncionarioAction();
		action.setFuncionarioVo(new FuncionarioVo("4", null));

		assertEquals("redirect", action.excluir());
		assertEquals(3, new FuncionarioBusiness().trazerTodosOsFuncionarios().size());
	}

	@Test
	public void agendaNaoEExcluidaPorGet() {
		new AgendaBusiness().salvarAgenda(new AgendaVo(null, "Sem compromissos", "1"));
		simularRequisicao("GET");
		AgendaAction action = new AgendaAction();
		action.setAgendaVo(new AgendaVo("4", null, null));

		assertEquals("redirect", action.excluir());
		assertEquals(4, new AgendaBusiness().trazerTodasAsAgendas().size());
	}

	@Test
	public void agendaEExcluidaPorPost() {
		new AgendaBusiness().salvarAgenda(new AgendaVo(null, "Sem compromissos", "1"));
		simularRequisicao("POST");
		AgendaAction action = new AgendaAction();
		action.setAgendaVo(new AgendaVo("4", null, null));

		assertEquals("redirect", action.excluir());
		assertEquals(3, new AgendaBusiness().trazerTodasAsAgendas().size());
	}

	@Test
	public void agendaComCompromissosNaoEExcluidaNemPorPostEMostraAMensagem() {
		simularRequisicao("POST");
		AgendaAction action = new AgendaAction();
		action.setAgendaVo(new AgendaVo("1", null, null));

		assertEquals("success", action.excluir());
		assertEquals(3, new AgendaBusiness().trazerTodasAsAgendas().size());
		assertEquals(1, action.getActionErrors().size());
		assertEquals("Não é possível excluir a agenda: existem compromissos cadastrados.",
				action.getActionErrors().iterator().next());
	}

	@Test
	public void compromissoNaoEExcluidoPorGet() {
		simularRequisicao("GET");
		CompromissoAction action = new CompromissoAction();
		action.setCompromissoVo(compromissoComCodigo("1"));

		assertEquals("redirect", action.excluir());
		assertEquals(3, new CompromissoBusiness().trazerTodosOsCompromissos().size());
	}

	@Test
	public void compromissoEExcluidoPorPost() {
		simularRequisicao("POST");
		CompromissoAction action = new CompromissoAction();
		action.setCompromissoVo(compromissoComCodigo("1"));

		assertEquals("redirect", action.excluir());
		assertEquals(2, new CompromissoBusiness().trazerTodosOsCompromissos().size());
	}

	@Test
	public void abrirFormulariosPorGetNaoGravaNada() {
		simularRequisicao("GET");

		FuncionarioAction funcionario = new FuncionarioAction();
		funcionario.setFuncionarioVo(new FuncionarioVo(null, "Nome por GET"));
		AgendaAction agenda = new AgendaAction();
		agenda.setAgendaVo(new AgendaVo(null, "Agenda por GET", "1"));

		assertEquals("input", funcionario.novo());
		assertEquals("input", agenda.novo());
		assertEquals(4, new FuncionarioBusiness().trazerTodosOsFuncionarios().size());
		assertEquals(3, new AgendaBusiness().trazerTodasAsAgendas().size());
	}

	@Test
	public void incluirPorPostComNomeVazioVoltaAoFormularioComMensagem() {
		simularRequisicao("POST");
		FuncionarioAction action = new FuncionarioAction();
		action.setFuncionarioVo(new FuncionarioVo(null, ""));

		assertEquals("input", action.novo());
		assertEquals("Nome não pode ser em branco.", action.getActionErrors().iterator().next());
	}
}
