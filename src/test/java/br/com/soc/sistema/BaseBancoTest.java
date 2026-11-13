package br.com.soc.sistema;

import org.junit.Before;

/**
 * Base dos testes que acessam o banco: antes de cada teste os dados voltam ao estado inicial.
 * 
 * Dados iniciais: funcionarios 1 Joao, 2 Maria, 3 Jose, 4 Joana;
 * agendas 1 (Manha), 2 (Tarde), 3 (Ambos);
 * compromissos (funcionario, agenda, data, hora): (1, 1, 2026-11-10, 09:00), (2, 2, 2026-11-10, 14:30), (3, 3, 2026-11-11, 18:00).
 */
public abstract class BaseBancoTest {

	@Before
	public void restaurarBanco() {
		BancoEmMemoria.restaurar();
	}
}
