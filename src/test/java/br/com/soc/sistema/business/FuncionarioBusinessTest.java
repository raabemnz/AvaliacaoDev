package br.com.soc.sistema.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.soc.sistema.BaseBancoTest;
import br.com.soc.sistema.exception.BusinessException;
import br.com.soc.sistema.filter.BuscaFilter;
import br.com.soc.sistema.vo.FuncionarioVo;

public class FuncionarioBusinessTest extends BaseBancoTest {

	private FuncionarioBusiness business;

	@Before
	public void criarBusiness() {
		business = new FuncionarioBusiness();
	}

	private static String repetir(int quantidade) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < quantidade; i++)
			sb.append('a');
		return sb.toString();
	}

	private List<FuncionarioVo> buscarPorNome(String nome) {
		return business.filtrarFuncionarios(new BuscaFilter().setOpcoesCombo("2").setValorBusca(nome));
	}

	@Test
	public void incluiFuncionarioEDevolveOCodigoGerado() {
		FuncionarioVo vo = new FuncionarioVo(null, "Ana");

		business.salvarFuncionario(vo);

		assertEquals("5", vo.getRowid());
		assertEquals(5, business.trazerTodosOsFuncionarios().size());
		assertEquals("Ana", business.buscarFuncionarioPor("5").getNome());
	}

	@Test
	public void recusaNomeEmBranco() {
		for (String nome : new String[] { null, "", "   " }) {
			FuncionarioVo funcionario = new FuncionarioVo(null, nome);

			BusinessException e = assertThrows(BusinessException.class, () -> business.salvarFuncionario(funcionario));
			assertEquals("Nome não pode ser em branco.", e.getMessage());
		}
		assertEquals(4, business.trazerTodosOsFuncionarios().size());
	}

	@Test
	public void aceitaNomeComCemCaracteresERecusaComCentoEUm() {
		business.salvarFuncionario(new FuncionarioVo(null, repetir(100)));
		FuncionarioVo nomeGrande = new FuncionarioVo(null, repetir(101));

		BusinessException e = assertThrows(BusinessException.class, () -> business.salvarFuncionario(nomeGrande));

		assertEquals("Nome deve ter no máximo 100 caracteres.", e.getMessage());
		assertEquals(5, business.trazerTodosOsFuncionarios().size());
	}

	@Test
	public void alteraONomeDoFuncionario() {
		business.alterarFuncionario(new FuncionarioVo("2", "Maria Silva"));

		assertEquals("Maria Silva", business.buscarFuncionarioPor("2").getNome());
	}

	@Test
	public void alteracaoRecusaNomeEmBrancoECodigoInvalido() {
		FuncionarioVo nomeEmBranco = new FuncionarioVo("2", " ");
		FuncionarioVo codigoTexto = new FuncionarioVo("abc", "Nome");
		FuncionarioVo semCodigoInformado = new FuncionarioVo(null, "Nome");

		BusinessException branco = assertThrows(BusinessException.class, () -> business.alterarFuncionario(nomeEmBranco));
		BusinessException texto = assertThrows(BusinessException.class, () -> business.alterarFuncionario(codigoTexto));
		BusinessException semCodigo = assertThrows(BusinessException.class, () -> business.alterarFuncionario(semCodigoInformado));

		assertEquals("Nome não pode ser em branco.", branco.getMessage());
		assertEquals("Foi informado um caracter no lugar de um número.", texto.getMessage());
		assertEquals("Foi informado um caracter no lugar de um número.", semCodigo.getMessage());
	}

	@Test
	public void alteracaoDeFuncionarioInexistenteInformaQueNaoFoiEncontrado() {
		FuncionarioVo inexistente = new FuncionarioVo("999", "Nome");

		BusinessException e = assertThrows(BusinessException.class, () -> business.alterarFuncionario(inexistente));

		assertEquals(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO, e.getMessage());
	}

	@Test
	public void excluiOFuncionario() {
		business.excluirFuncionario("4");

		assertNull(business.buscarFuncionarioPor("4"));
		assertEquals(3, business.trazerTodosOsFuncionarios().size());
	}

	@Test
	public void regra4_excluirFuncionarioTambemExcluiOsSeusCompromissos() {
		CompromissoBusiness compromissos = new CompromissoBusiness();
		assertEquals(3, compromissos.trazerTodosOsCompromissos().size());

		business.excluirFuncionario("1");

		assertEquals(2, compromissos.trazerTodosOsCompromissos().size());
		assertTrue(compromissos.trazerTodosOsCompromissos().stream()
				.noneMatch(c -> "1".equals(c.getCdFuncionario())));
	}

	@Test
	public void exclusaoRecusaFuncionarioInexistenteECodigoInvalido() {
		BusinessException inexistente = assertThrows(BusinessException.class, () -> business.excluirFuncionario("999"));
		BusinessException texto = assertThrows(BusinessException.class, () -> business.excluirFuncionario("abc"));

		assertEquals(FuncionarioBusiness.FUNCIONARIO_NAO_ENCONTRADO, inexistente.getMessage());
		assertEquals("Foi informado um caracter no lugar de um número.", texto.getMessage());
	}

	@Test
	public void buscaPorNomeIgnoraAcentoEMaiuscula() {
		assertEquals(1, buscarPorNome("joao").size());
		assertEquals(1, buscarPorNome("JOÃO").size());
		assertEquals(1, buscarPorNome("jose").size());
		assertEquals(2, buscarPorNome("joa").size());
		assertEquals(0, buscarPorNome("zzz").size());
	}

	@Test
	public void buscaPorNomeTrataCuringasComoTextoComum() {
		business.salvarFuncionario(new FuncionarioVo(null, "Joao_Silva"));
		business.salvarFuncionario(new FuncionarioVo(null, "JoaoXSilva"));
		business.salvarFuncionario(new FuncionarioVo(null, "100% Real"));

		assertEquals(1, buscarPorNome("o_s").size());
		assertEquals("Joao_Silva", buscarPorNome("o_s").get(0).getNome());
		assertEquals(1, buscarPorNome("%").size());
		assertEquals("100% Real", buscarPorNome("%").get(0).getNome());
	}

	@Test
	public void buscaPorIdNaoDevolveListaComElementoNulo() {
		List<FuncionarioVo> inexistente = business
				.filtrarFuncionarios(new BuscaFilter().setOpcoesCombo("1").setValorBusca("999"));
		List<FuncionarioVo> existente = business
				.filtrarFuncionarios(new BuscaFilter().setOpcoesCombo("1").setValorBusca("2"));

		assertTrue(inexistente.isEmpty());
		assertEquals(1, existente.size());
		assertEquals("Maria", existente.get(0).getNome());
	}

	@Test
	public void buscaPorIdRecusaTexto() {
		BuscaFilter filtro = new BuscaFilter().setOpcoesCombo("1").setValorBusca("Joao");

		BusinessException e = assertThrows(BusinessException.class, () -> business.filtrarFuncionarios(filtro));

		assertEquals("Foi informado um caracter no lugar de um número.", e.getMessage());
	}
}
