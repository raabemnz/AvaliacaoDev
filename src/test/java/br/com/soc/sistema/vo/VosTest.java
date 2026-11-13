package br.com.soc.sistema.vo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

public class VosTest {

	@Test
	public void funcionarioNormalizaCodigoVazio() {
		assertNull(new FuncionarioVo("", "x").getRowid());
		assertNull(new FuncionarioVo("  ", "x").getRowid());
		assertNull(new FuncionarioVo(null, "x").getRowid());
		assertEquals("5", new FuncionarioVo("5", "x").getRowid());

		FuncionarioVo vo = new FuncionarioVo();
		vo.setRowid("");
		assertNull(vo.getRowid());
		vo.setRowid("7");
		assertEquals("7", vo.getRowid());
	}

	@Test
	public void agendaNormalizaCodigoVazio() {
		assertNull(new AgendaVo("", "x", "1").getRowid());
		assertNull(new AgendaVo(" ", "x", "1").getRowid());
		assertEquals("3", new AgendaVo("3", "x", "1").getRowid());

		AgendaVo vo = new AgendaVo();
		vo.setRowid("");
		assertNull(vo.getRowid());
	}

	@Test
	public void compromissoNormalizaCodigoVazio() {
		CompromissoVo vo = new CompromissoVo();
		vo.setRowid("");
		assertNull(vo.getRowid());
		vo.setRowid("2");
		assertEquals("2", vo.getRowid());
	}

	@Test
	public void agendaDescrevePeriodo() {
		assertEquals("Manhã", new AgendaVo("1", "x", "1").getDescricaoPeriodo());
		assertEquals("Ambos", new AgendaVo("1", "x", "3").getDescricaoPeriodo());
		assertEquals("", new AgendaVo("1", "x", "9").getDescricaoPeriodo());
	}

	@Test
	public void compromissoFormataDataEHorario() {
		CompromissoVo vo = new CompromissoVo();
		vo.setData("2026-11-10");
		vo.setHorario("09:00:00");

		assertEquals("10/11/2026", vo.getDataFormatada());
		assertEquals("09:00", vo.getHorarioFormatado());
	}
}
