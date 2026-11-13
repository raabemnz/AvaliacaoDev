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
import br.com.soc.sistema.vo.AgendaVo;

public class AgendaBusinessTest extends BaseBancoTest {

	private static final String MANHA = "1";
	private static final String TARDE = "2";
	private static final String AMBOS = "3";

	private AgendaBusiness business;

	@Before
	public void criarBusiness() {
		business = new AgendaBusiness();
	}

	private static String repetir(int quantidade) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < quantidade; i++)
			sb.append('a');
		return sb.toString();
	}

	private void alterarPeriodo(String codigo, String nome, String periodo) {
		business.alterarAgenda(new AgendaVo(codigo, nome, periodo));
	}

	@Test
	public void incluiAgenda() {
		business.salvarAgenda(new AgendaVo(null, "Treinamento", TARDE));

		List<AgendaVo> agendas = business.trazerTodasAsAgendas();
		assertEquals(4, agendas.size());
		assertEquals("Treinamento", agendas.get(3).getNome());
		assertEquals("Tarde", agendas.get(3).getDescricaoPeriodo());
	}

	@Test
	public void recusaDadosInvalidosNaInclusao() {
		AgendaVo semNome = new AgendaVo(null, "  ", MANHA);
		AgendaVo nomeGrande = new AgendaVo(null, repetir(101), MANHA);
		AgendaVo semPeriodo = new AgendaVo(null, "X", "");
		AgendaVo periodoInvalido = new AgendaVo(null, "X", "9");

		assertEquals("Nome não pode ser em branco.", assertThrows(BusinessException.class,
				() -> business.salvarAgenda(semNome)).getMessage());
		assertEquals("Nome deve ter no máximo 100 caracteres.", assertThrows(BusinessException.class,
				() -> business.salvarAgenda(nomeGrande)).getMessage());
		assertEquals("Período disponível deve ser informado.", assertThrows(BusinessException.class,
				() -> business.salvarAgenda(semPeriodo)).getMessage());
		assertEquals("Período disponível inválido.", assertThrows(BusinessException.class,
				() -> business.salvarAgenda(periodoInvalido)).getMessage());

		assertEquals(3, business.trazerTodasAsAgendas().size());
	}

	@Test
	public void buscaDeAgendaInexistenteOuComCodigoInvalido() {
		assertEquals(AgendaBusiness.AGENDA_NAO_ENCONTRADA, assertThrows(BusinessException.class,
				() -> business.buscarAgendaPor("999")).getMessage());
		assertEquals("Foi informado um caracter no lugar de um número.", assertThrows(BusinessException.class,
				() -> business.buscarAgendaPor("abc")).getMessage());
	}

	@Test
	public void buscaPorNomeIgnoraAcentoEMaiuscula() {
		BuscaFilter filtro = new BuscaFilter().setOpcoesCombo("2").setValorBusca("REUNIÃO");

		assertEquals(1, business.filtrarAgendas(filtro).size());
	}

	@Test
	public void regra5_naoExcluiAgendaComCompromissosCadastrados() {
		BusinessException e = assertThrows(BusinessException.class, () -> business.excluirAgenda("1"));

		assertEquals("Não é possível excluir a agenda: existem compromissos cadastrados.", e.getMessage());
		assertEquals(3, business.trazerTodasAsAgendas().size());
	}

	@Test
	public void excluiAgendaSemCompromissos() {
		business.salvarAgenda(new AgendaVo(null, "Sem compromissos", MANHA));
		String codigo = business.trazerTodasAsAgendas().get(3).getRowid();

		business.excluirAgenda(codigo);

		assertEquals(3, business.trazerTodasAsAgendas().size());
	}

	@Test
	public void exclusaoRecusaAgendaInexistenteECodigoInvalido() {
		assertEquals(AgendaBusiness.AGENDA_NAO_ENCONTRADA, assertThrows(BusinessException.class,
				() -> business.excluirAgenda("999")).getMessage());
		assertEquals("Foi informado um caracter no lugar de um número.", assertThrows(BusinessException.class,
				() -> business.excluirAgenda("abc")).getMessage());
	}

	@Test
	public void alterarSomenteONomeNaoEAfetadoPelaRegraDosCompromissos() {
		alterarPeriodo("1", "Reuniao de equipe 2", MANHA);

		assertEquals("Reuniao de equipe 2", business.buscarAgendaPor("1").getNome());
	}

	@Test
	public void regra6_naoTrocaOPeriodoSeUmCompromissoFicarForaDaDisponibilidade() {
		// agenda 1 (Manha) tem um compromisso as 09:00
		BusinessException paraTarde = assertThrows(BusinessException.class,
				() -> alterarPeriodo("1", "Reuniao de equipe", TARDE));
		// agenda 2 (Tarde) tem um compromisso as 14:30
		BusinessException paraManha = assertThrows(BusinessException.class,
				() -> alterarPeriodo("2", "Atendimento ao cliente", MANHA));
		// agenda 3 (Ambos) tem um compromisso as 18:00
		BusinessException ambosParaManha = assertThrows(BusinessException.class,
				() -> alterarPeriodo("3", "Plantao", MANHA));

		assertTrue(paraTarde.getMessage().startsWith("Não é possível alterar o período disponível para Tarde"));
		assertTrue(paraManha.getMessage().startsWith("Não é possível alterar o período disponível para Manhã"));
		assertTrue(ambosParaManha.getMessage().contains("1 compromisso(s)"));
		assertEquals("1", business.buscarAgendaPor("1").getPeriodoDisponivel());
	}

	@Test
	public void trocaOPeriodoQuandoOsCompromissosContinuamDentroDaDisponibilidade() {
		alterarPeriodo("1", "Reuniao de equipe", AMBOS);
		alterarPeriodo("1", "Reuniao de equipe", MANHA); // 09:00 cabe na manha
		alterarPeriodo("3", "Plantao", TARDE); // 18:00 cabe na tarde

		assertEquals("1", business.buscarAgendaPor("1").getPeriodoDisponivel());
		assertEquals("2", business.buscarAgendaPor("3").getPeriodoDisponivel());
	}

	@Test
	public void agendaSemCompromissosTrocaDePeriodoLivremente() {
		business.salvarAgenda(new AgendaVo(null, "Livre", MANHA));
		String codigo = business.trazerTodasAsAgendas().get(3).getRowid();

		alterarPeriodo(codigo, "Livre", TARDE);

		assertEquals("2", business.buscarAgendaPor(codigo).getPeriodoDisponivel());
	}

	@Test
	public void alteracaoRecusaAgendaInexistente() {
		assertEquals(AgendaBusiness.AGENDA_NAO_ENCONTRADA, assertThrows(BusinessException.class,
				() -> alterarPeriodo("999", "X", MANHA)).getMessage());
	}
}
