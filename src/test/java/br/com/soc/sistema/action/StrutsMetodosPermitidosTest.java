package br.com.soc.sistema.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;

import org.apache.struts2.dispatcher.Dispatcher;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.opensymphony.xwork2.ActionProxyFactory;
import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.config.RuntimeConfiguration;
import com.opensymphony.xwork2.config.entities.ActionConfig;

public class StrutsMetodosPermitidosTest {

	private static final java.util.List<String> METODOS_PERMITIDOS_PELO_STRUTS = Arrays.asList("execute", "input",
			"back", "cancel", "browse", "save", "delete", "list", "index");

	private static final String SUFIXOS = "(?:Funcionarios|Agendas|Compromissos|Relatorios)";

	private static Dispatcher dispatcher;
	private static RuntimeConfiguration configuracao;

	@BeforeClass
	public static void iniciarStruts() {
		final Map<String, Object> atributos = new HashMap<String, Object>();

		ServletContext servletContext = (ServletContext) Proxy.newProxyInstance(
				StrutsMetodosPermitidosTest.class.getClassLoader(), new Class<?>[] { ServletContext.class },
				(proxy, metodo, args) -> {
					switch (metodo.getName()) {
						case "getAttribute": return atributos.get(args[0]);
						case "setAttribute": atributos.put((String) args[0], args[1]); return null;
						case "removeAttribute": atributos.remove(args[0]); return null;
						case "getAttributeNames":
						case "getInitParameterNames": return Collections.emptyEnumeration();
						case "getServerInfo": return "teste";
						case "getMajorVersion": return 3;
						case "getMinorVersion": return 1;
						default:
							Class<?> tipo = metodo.getReturnType();
							if (tipo == boolean.class) return false;
							if (tipo == int.class) return 0;
							return null;
					}
				});

		dispatcher = new Dispatcher(servletContext, new HashMap<String, String>());
		dispatcher.init();
		configuracao = dispatcher.getConfigurationManager().getConfiguration().getRuntimeConfiguration();
	}

	@AfterClass
	public static void encerrarStruts() {
		if (dispatcher != null)
			dispatcher.cleanup();
	}

	private static ActionConfig acao(String url) {
		return configuracao.getActionConfig("", url);
	}

	@Test
	public void opacoteUsaOModoEstrito() {
		boolean estrito = dispatcher.getConfigurationManager().getConfiguration()
				.getPackageConfig("br.com.soc.sistema").isStrictMethodInvocation();

		assertTrue(estrito);
	}

	@Test
	public void asUrlsDeTelaContinuamPermitidas() {
		String[] urls = { "todosFuncionarios", "filtrarFuncionarios", "novoFuncionarios", "editarFuncionarios",
				"salvarAlteracaoFuncionarios", "excluirFuncionarios",
				"todosAgendas", "filtrarAgendas", "novoAgendas", "editarAgendas", "salvarAlteracaoAgendas", "excluirAgendas",
				"todosCompromissos", "filtrarCompromissos", "novoCompromissos", "editarCompromissos",
				"salvarAlteracaoCompromissos", "excluirCompromissos",
				"compromissosRelatorios", "visualizarCompromissosRelatorios", "exportarCompromissosRelatorios" };

		for (String url : urls) {
			ActionConfig config = acao(url);

			assertNotNull(url, config);
			assertTrue(url + " -> " + config.getMethodName(), config.isAllowedMethod(config.getMethodName()));
		}
	}

	@Test
	public void ometodoDaUrlVemDoTextoAntesDoSufixo() {
		assertEquals("excluir", acao("excluirFuncionarios").getMethodName());
		assertEquals("salvarAlteracao", acao("salvarAlteracaoAgendas").getMethodName());
		assertEquals("exportarCompromissos", acao("exportarCompromissosRelatorios").getMethodName());
	}

	@Test
	public void metodosQueNaoSaoAcoesNaoPodemSerChamadosPorUrl() {
		String[] urls = { "getPeriodoFormatadoRelatorios", "getListaFuncionariosCompromissos", "getFuncionariosFuncionarios",
				"getTamanhoMaximoNomeAgendas", "validateFuncionarios", "clearErrorsFuncionarios",
				"isSubmissaoAgendas", "setRowidFuncionarios" };

		for (String url : urls) {
			ActionConfig config = acao(url);

			// o Struts ou nao mapeia a URL, ou mapeia e marca o metodo como nao permitido
			if (config != null)
				assertFalse(url + " -> " + config.getMethodName(), config.isAllowedMethod(config.getMethodName()));
		}
	}

	@Test
	public void oStrutsRecusaDeVerdadeAChamadaDeMetodoNaoPermitido() {
		ActionProxyFactory fabrica = dispatcher.getContainer().getInstance(ActionProxyFactory.class);
		Map<String, Object> contexto = new HashMap<String, Object>();

		ConfigurationException e = assertThrows(ConfigurationException.class,
				() -> fabrica.createActionProxy("", "getPeriodoFormatadoRelatorios", null, contexto, false, false));

		assertTrue(e.getMessage(),
				e.getMessage().contains("is not allowed") || e.getMessage().contains("There is no Action mapped"));
	}

	@Test
	public void oStrutsAceitaAChamadaDeMetodoPermitido() {
		ActionProxyFactory fabrica = dispatcher.getContainer().getInstance(ActionProxyFactory.class);
		Map<String, Object> contexto = new HashMap<String, Object>();

		assertNotNull(fabrica.createActionProxy("", "todosFuncionarios", null, contexto, false, false));
		assertNotNull(fabrica.createActionProxy("", "excluirAgendas", null, contexto, false, false));
	}

	@Test
	public void todoMetodoPermitidoExisteNaActionERetornaString() throws Exception {
		for (String url : new String[] { "todosFuncionarios", "todosAgendas", "todosCompromissos", "compromissosRelatorios" }) {
			ActionConfig config = acao(url);
			Class<?> classe = Class.forName(config.getClassName());

			for (String nome : config.getAllowedMethods()) {
				if (METODOS_PERMITIDOS_PELO_STRUTS.contains(nome))
					continue; // permitidos por padrao pelo Struts, nao fazem parte da lista do projeto

				Method metodo = classe.getMethod(nome);
				assertEquals(classe.getSimpleName() + "." + nome, String.class, metodo.getReturnType());
			}
		}
	}

	@Test
	public void todaAcaoPublicaDeclaradaNasActionsEstaNaListaDePermitidos() {
		for (String url : new String[] { "todosFuncionarios", "todosAgendas", "todosCompromissos", "compromissosRelatorios" }) {
			ActionConfig config = acao(url);
			Class<?> classe;
			try {
				classe = Class.forName(config.getClassName());
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}

			for (Method metodo : classe.getDeclaredMethods()) {
				boolean publico = Modifier.isPublic(metodo.getModifiers());
				boolean candidatoAAcao = publico && metodo.getParameterCount() == 0
						&& metodo.getReturnType() == String.class
						&& !metodo.getName().startsWith("get") && !metodo.getName().startsWith("is");

				if (candidatoAAcao)
					assertTrue(classe.getSimpleName() + "." + metodo.getName() + " nao esta em allowed-methods",
							config.getAllowedMethods().contains(metodo.getName()));
			}
		}
	}

	@Test
	public void todaUrlUsadaNasTelasEPermitida() throws IOException {
		Pattern comExtensao = Pattern.compile("\\b([a-z]\\w*?" + SUFIXOS + ")\\.action\\b");
		Pattern atributoAction = Pattern.compile("\\baction=\"(\\w+" + SUFIXOS + ")\"");
		Pattern textoEntreAspas = Pattern.compile("'([a-z]\\w*?" + SUFIXOS + ")'");
		Pattern redirecionamento = Pattern.compile("redirectAction\">(\\w+)<");

		Set<String> urls = new TreeSet<String>();
		for (Path arquivo : arquivosDeTela()) {
			String conteudo = new String(Files.readAllBytes(arquivo), StandardCharsets.UTF_8);
			for (Pattern padrao : new Pattern[] { comExtensao, atributoAction, textoEntreAspas, redirecionamento }) {
				Matcher m = padrao.matcher(conteudo);
				while (m.find())
					urls.add(m.group(1));
			}
		}

		assertTrue("deveria encontrar as URLs usadas nas telas", urls.size() >= 15);
		for (String url : urls) {
			ActionConfig config = acao(url);

			assertNotNull(url, config);
			assertTrue("URL usada em tela mas nao permitida: " + url + " -> " + config.getMethodName(),
					config.isAllowedMethod(config.getMethodName()));
		}
	}

	private static java.util.List<Path> arquivosDeTela() throws IOException {
		java.util.List<Path> arquivos = new java.util.ArrayList<Path>();
		try (Stream<Path> jsps = Files.walk(Paths.get("src/main/webapp"))) {
			arquivos.addAll(jsps.filter(p -> p.toString().endsWith(".jsp")).collect(Collectors.toList()));
		}
		arquivos.add(Paths.get("src/main/resources/struts.xml"));
		return arquivos;
	}
}
