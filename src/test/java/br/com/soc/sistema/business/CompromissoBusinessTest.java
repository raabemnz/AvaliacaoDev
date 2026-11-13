package br.com.soc.sistema.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.soc.sistema.BaseBancoTest;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.BuscaFilter;
import br.com.soc.sistema.vo.CompromissoVo;

public class CompromissoBusinessTest extends BaseBancoTest {

	private static final String AGENDA_MANHA = "1";
	private static final String AGENDA_TARDE = "2";
	private static final String AGENDA_AMBOS = "3";

	private CompromissoBusiness business;

	@Before
	public void criarBusiness() {
		business = new CompromissoBusiness();
	}

	private static CompromissoVo compromisso(String funcionario, String agenda, String data, String horario) {
		CompromissoVo vo = new CompromissoVo();
		vo.setCdFuncionario(funcionario);
		vo.setCdAgenda(agenda);
		vo.setData(data);
		vo.setHorario(horario);
		return vo;
	}

	private String erroAoSalvar(CompromissoVo vo) {
		return assertThrows(BusinessException.class, () -> business.salvarCompromisso(vo)).getMessage();
	}

	@Test
	public void incluiCompromisso() {
		business.salvarCompromisso(compromisso("4", AGENDA_AMBOS, "2026-12-01", "10:30"));

		List<CompromissoVo> todos = business.trazerTodosOsCompromissos();
		assertEquals(4, todos.size());
		CompromissoVo novo = todos.get(3);
		assertEquals("4", novo.getCdFuncionario());
		assertEquals("Joana", novo.getNomeFuncionario());
		assertEquals("3", novo.getCdAgenda());
		assertEquals("2026-12-01", novo.getData());
		assertEquals("10:30", novo.getHorario());
	}

	@Test
	public void regra6_agendaDaManhaSoAceitaHorarioAntesDoMeioDia() {
		assertEquals("Horário fora da disponibilidade da agenda (período disponível: Manhã).",
				erroAoSalvar(compromisso("1", AGENDA_MANHA, "2026-12-01", "15:00")));
		assertEquals("Horário fora da disponibilidade da agenda (período disponível: Manhã).",
				erroAoSalvar(compromisso("1", AGENDA_MANHA, "2026-12-01", "12:00")));

		business.salvarCompromisso(compromisso("1", AGENDA_MANHA, "2026-12-01", "11:59"));
		assertEquals(4, business.trazerTodosOsCompromissos().size());
	}

	@Test
	public void regra6_agendaDaTardeSoAceitaHorarioAPartirDoMeioDia() {
		assertEquals("Horário fora da disponibilidade da agenda (período disponível: Tarde).",
				erroAoSalvar(compromisso("1", AGENDA_TARDE, "2026-12-01", "09:00")));
		assertEquals("Horário fora da disponibilidade da agenda (período disponível: Tarde).",
				erroAoSalvar(compromisso("1", AGENDA_TARDE, "2026-12-01", "11:59")));

		business.salvarCompromisso(compromisso("1", AGENDA_TARDE, "2026-12-01", "12:00"));
		assertEquals(4, business.trazerTodosOsCompromissos().size());
	}

	@Test
	public void regra6_agendaAmbosAceitaQualquerHorario() {
		business.salvarCompromisso(compromisso("1", AGENDA_AMBOS, "2026-12-01", "00:00"));
		business.salvarCompromisso(compromisso("1", AGENDA_AMBOS, "2026-12-01", "23:59"));

		assertEquals(5, business.trazerTodosOsCompromissos().size());
	}

	@Test
	public void regra6_tambemVale_aoAlterarOCompromisso() {
		CompromissoVo existente = business.buscarCompromissoPor("1"); // agenda 1 (Manha), 09:00
		existente.setHorario("15:00");

		BusinessException e = assertThrows(BusinessException.class, () -> business.alterarCompromisso(existente));

		assertTrue(e.getMessage().startsWith("Horário fora da disponibilidade da agenda"));
		assertEquals("09:00", business.buscarCompromissoPor("1").getHorario());
	}

	@Test
	public void alteraOCompromisso() {
		CompromissoVo existente = business.buscarCompromissoPor("1");
		existente.setHorario("10:15");
		existente.setData("2026-11-20");

		business.alterarCompromisso(existente);

		CompromissoVo alterado = business.buscarCompromissoPor("1");
		assertEquals("10:15", alterado.getHorario());
		assertEquals("2026-11-20", alterado.getData());
	}

	@Test
	public void recusaFuncionarioEAgendaNaoInformadosOuInexistentes() {
		assertEquals("Funcionário deve ser informado.", erroAoSalvar(compromisso("", AGENDA_AMBOS, "2026-12-01", "10:00")));
		assertEquals("Agenda deve ser informada.", erroAoSalvar(compromisso("1", "", "2026-12-01", "10:00")));
		assertEquals(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO,
				erroAoSalvar(compromisso("999", AGENDA_AMBOS, "2026-12-01", "10:00")));
		assertEquals(AgendaBusiness.AGENDA_NAO_ENCONTRADA,
				erroAoSalvar(compromisso("1", "999", "2026-12-01", "10:00")));
	}

	@Test
	public void recusaDataEHorarioNaoInformadosOuInvalidos() {
		assertEquals("Data deve ser informada.", erroAoSalvar(compromisso("1", AGENDA_AMBOS, "", "10:00")));
		assertEquals("Data inválida.", erroAoSalvar(compromisso("1", AGENDA_AMBOS, "10/12/2026", "10:00")));
		assertEquals("Horário deve ser informado.", erroAoSalvar(compromisso("1", AGENDA_AMBOS, "2026-12-01", "")));
		assertEquals("Horário inválido.", erroAoSalvar(compromisso("1", AGENDA_AMBOS, "2026-12-01", "xx")));
		assertEquals(3, business.trazerTodosOsCompromissos().size());
	}

	@Test
	public void buscaExclusaoESemCompromisso() {
		business.excluirCompromisso("2");

		assertEquals(2, business.trazerTodosOsCompromissos().size());
		assertEquals(CompromissoBusiness.COMPROMISSO_NAO_ENCONTRADO, assertThrows(BusinessException.class,
				() -> business.excluirCompromisso("2")).getMessage());
		assertEquals(CompromissoBusiness.COMPROMISSO_NAO_ENCONTRADO, assertThrows(BusinessException.class,
				() -> business.buscarCompromissoPor("999")).getMessage());
	}

	@Test
	public void buscaPorNomeDoFuncionarioIgnoraAcento() {
		BuscaFilter filtro = new BuscaFilter().setOpcoesCombo("2").setValorBusca("joao");

		List<CompromissoVo> encontrados = business.filtrarCompromissos(filtro);

		assertEquals(1, encontrados.size());
		assertEquals("João", encontrados.get(0).getNomeFuncionario());
	}

	@Test
	public void relatorioIncluiAsDuasDatasDoPeriodo() {
		assertEquals(2, business.gerarRelatorio("2026-11-10", "2026-11-10").size());
		assertEquals(1, business.gerarRelatorio("2026-11-11", "2026-11-11").size());
		assertEquals(3, business.gerarRelatorio("2026-11-10", "2026-11-11").size());
		assertEquals(3, business.gerarRelatorio("2026-01-01", "2026-12-31").size());
		assertEquals(0, business.gerarRelatorio("2026-12-01", "2026-12-31").size());
	}

	@Test
	public void relatorioVemOrdenadoPorDataEHora() {
		List<CompromissoVo> relatorio = business.gerarRelatorio("2026-11-10", "2026-11-11");

		assertEquals("09:00", relatorio.get(0).getHorario());
		assertEquals("14:30", relatorio.get(1).getHorario());
		assertEquals("18:00", relatorio.get(2).getHorario());
	}

	@Test
	public void relatorioRecusaPeriodoInvalido() {
		assertEquals("Data inicial deve ser informada.", assertThrows(BusinessException.class,
				() -> business.gerarRelatorio("", "2026-11-11")).getMessage());
		assertEquals("Data final deve ser informada.", assertThrows(BusinessException.class,
				() -> business.gerarRelatorio("2026-11-10", null)).getMessage());
		assertEquals("Data inicial inválida.", assertThrows(BusinessException.class,
				() -> business.gerarRelatorio("10/11/2026", "2026-11-11")).getMessage());
		assertEquals("Data final não pode ser anterior à data inicial.", assertThrows(BusinessException.class,
				() -> business.gerarRelatorio("2026-11-11", "2026-11-10")).getMessage());
	}
}
