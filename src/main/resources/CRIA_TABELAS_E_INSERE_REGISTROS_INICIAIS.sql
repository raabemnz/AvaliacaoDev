-- Este script pode ser executado mais de uma vez no mesmo banco sem erro e sem duplicar a carga inicial:
-- as tabelas usam IF NOT EXISTS e cada carga inicial so entra quando a tabela esta vazia.

CREATE TABLE IF NOT EXISTS funcionario (rowid bigint auto_increment PRIMARY KEY, nm_funcionario VARCHAR(255));

INSERT INTO funcionario (nm_funcionario)
	SELECT nome FROM (VALUES ('João'), ('Maria'), ('José'), ('Joana')) AS carga(nome)
	WHERE NOT EXISTS (SELECT 1 FROM funcionario);

CREATE TABLE IF NOT EXISTS agenda (rowid bigint auto_increment PRIMARY KEY, nm_agenda VARCHAR(255), periodo_disponivel INT);

INSERT INTO agenda (nm_agenda, periodo_disponivel)
	SELECT nome, periodo FROM (VALUES ('Reuniao de equipe', 1), ('Atendimento ao cliente', 2), ('Plantao', 3)) AS carga(nome, periodo)
	WHERE NOT EXISTS (SELECT 1 FROM agenda);

-- Regra 4: ao excluir um funcionario, seus compromissos sao excluidos (ON DELETE CASCADE)
-- Regra 5: agenda com compromissos nao pode ser excluida (sem CASCADE, o banco recusa a exclusao)
CREATE TABLE IF NOT EXISTS compromisso (
	rowid bigint auto_increment PRIMARY KEY,
	cd_funcionario bigint NOT NULL,
	cd_agenda bigint NOT NULL,
	dt_compromisso DATE NOT NULL,
	hr_compromisso TIME NOT NULL,
	CONSTRAINT fk_compromisso_funcionario FOREIGN KEY (cd_funcionario) REFERENCES funcionario(rowid) ON DELETE CASCADE,
	CONSTRAINT fk_compromisso_agenda FOREIGN KEY (cd_agenda) REFERENCES agenda(rowid)
);

INSERT INTO compromisso (cd_funcionario, cd_agenda, dt_compromisso, hr_compromisso)
	SELECT funcionario, agenda, data, hora
	FROM (VALUES (1, 1, DATE '2026-11-10', TIME '09:00:00'),
	             (2, 2, DATE '2026-11-10', TIME '14:30:00'),
	             (3, 3, DATE '2026-11-11', TIME '18:00:00')) AS carga(funcionario, agenda, data, hora)
	WHERE NOT EXISTS (SELECT 1 FROM compromisso);
