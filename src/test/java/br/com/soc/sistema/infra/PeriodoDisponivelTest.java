package br.com.soc.sistema.infra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.time.LocalTime;

import org.junit.Test;

import br.com.soc.sistema.exception.BusinessException;

public class PeriodoDisponivelTest {

	@Test
	public void manhaAceitaAteAsOnzeECinquentaENove() {
		assertTrue(PeriodoDisponivel.MANHA.aceita(LocalTime.of(0, 0)));
		assertTrue(PeriodoDisponivel.MANHA.aceita(LocalTime.of(9, 0)));
		assertTrue(PeriodoDisponivel.MANHA.aceita(LocalTime.of(11, 59)));
		assertFalse(PeriodoDisponivel.MANHA.aceita(LocalTime.NOON));
		assertFalse(PeriodoDisponivel.MANHA.aceita(LocalTime.of(18, 0)));
	}

	@Test
	public void tardeAceitaAPartirDoMeioDia() {
		assertFalse(PeriodoDisponivel.TARDE.aceita(LocalTime.of(9, 0)));
		assertFalse(PeriodoDisponivel.TARDE.aceita(LocalTime.of(11, 59)));
		assertTrue(PeriodoDisponivel.TARDE.aceita(LocalTime.NOON));
		assertTrue(PeriodoDisponivel.TARDE.aceita(LocalTime.of(23, 59)));
	}

	@Test
	public void ambosAceitaQualquerHorario() {
		assertTrue(PeriodoDisponivel.AMBOS.aceita(LocalTime.of(0, 0)));
		assertTrue(PeriodoDisponivel.AMBOS.aceita(LocalTime.NOON));
		assertTrue(PeriodoDisponivel.AMBOS.aceita(LocalTime.of(23, 59)));
	}

	@Test
	public void buscaPeloCodigo() {
		assertEquals(PeriodoDisponivel.MANHA, PeriodoDisponivel.buscarPor("1"));
		assertEquals(PeriodoDisponivel.TARDE, PeriodoDisponivel.buscarPor("2"));
		assertEquals(PeriodoDisponivel.AMBOS, PeriodoDisponivel.buscarPor("3"));
	}

	@Test
	public void recusaCodigoInvalido() {
		BusinessException e = assertThrows(BusinessException.class, () -> PeriodoDisponivel.buscarPor("9"));
		assertEquals("Período disponível inválido.", e.getMessage());
	}

	@Test
	public void recusaCodigoNaoInformado() {
		BusinessException nulo = assertThrows(BusinessException.class, () -> PeriodoDisponivel.buscarPor(null));
		BusinessException vazio = assertThrows(BusinessException.class, () -> PeriodoDisponivel.buscarPor("  "));

		assertEquals("Período disponível deve ser informado.", nulo.getMessage());
		assertEquals("Período disponível deve ser informado.", vazio.getMessage());
	}

	@Test
	public void buscaOpcionalNaoLancaExcecao() {
		assertFalse(PeriodoDisponivel.buscarOpcional("9").isPresent());
		assertTrue(PeriodoDisponivel.buscarOpcional("2").isPresent());
	}
}
