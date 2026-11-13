package br.com.soc.sistema.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.infra.TextoUtil;
import br.com.soc.sistema.vo.CompromissoVo;

public class CompromissoDao extends Dao {
	
	private static final String SELECT_COMPROMISSO = 
			"SELECT c.rowid id, c.cd_funcionario cdfunc, f.nm_funcionario nmfunc, "
			+ "c.cd_agenda cdagenda, a.nm_agenda nmagenda, "
			+ "c.dt_compromisso dt, c.hr_compromisso hr "
			+ "FROM compromisso c "
			+ "INNER JOIN funcionario f ON f.rowid = c.cd_funcionario "
			+ "INNER JOIN agenda a ON a.rowid = c.cd_agenda ";
	
	private static final String ORDENACAO = "ORDER BY c.dt_compromisso, c.hr_compromisso, c.rowid";
	
	public void insertCompromisso(CompromissoVo compromissoVo){
		StringBuilder query = new StringBuilder("INSERT INTO compromisso ")
								.append("(cd_funcionario, cd_agenda, dt_compromisso, hr_compromisso) values (?, ?, ?, ?)");
		try(
			Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setLong(i++, Long.parseLong(compromissoVo.getCdFuncionario()));
			ps.setLong(i++, Long.parseLong(compromissoVo.getCdAgenda()));
			ps.setDate(i++, Date.valueOf(LocalDate.parse(compromissoVo.getData())));
			ps.setTime(i++, Time.valueOf(LocalTime.parse(compromissoVo.getHorario())));
			ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao incluir o compromisso", e);
		}
	}
	
	public int updateCompromisso(CompromissoVo compromissoVo){
		StringBuilder query = new StringBuilder("UPDATE compromisso SET ")
								.append("cd_funcionario = ?, cd_agenda = ?, dt_compromisso = ?, hr_compromisso = ? ")
								.append("WHERE rowid = ?");
		try(
			Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setLong(i++, Long.parseLong(compromissoVo.getCdFuncionario()));
			ps.setLong(i++, Long.parseLong(compromissoVo.getCdAgenda()));
			ps.setDate(i++, Date.valueOf(LocalDate.parse(compromissoVo.getData())));
			ps.setTime(i++, Time.valueOf(LocalTime.parse(compromissoVo.getHorario())));
			ps.setLong(i++, Long.parseLong(compromissoVo.getRowid()));
			return ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao alterar o compromisso", e);
		}
	}
	
	public int deleteCompromisso(Integer codigo){
		StringBuilder query = new StringBuilder("DELETE FROM compromisso WHERE rowid = ?");
		try(
			Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setInt(i, codigo);
			return ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao excluir o compromisso", e);
		}
	}
	
	public List<CompromissoVo> findAllCompromissos(){
		StringBuilder query = new StringBuilder(SELECT_COMPROMISSO).append(ORDENACAO);
		try(
			Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString());
			ResultSet rs = ps.executeQuery()){
			
			List<CompromissoVo> compromissos = new ArrayList<>();
			while (rs.next()) {
				compromissos.add(montarCompromisso(rs));
			}
			return compromissos;
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar os compromissos", e);
		}
	}
	
	public List<CompromissoVo> findAllByNomeFuncionario(String nome){
		StringBuilder query = new StringBuilder(SELECT_COMPROMISSO)
								.append("WHERE ").append(TextoUtil.condicaoLike("f.nm_funcionario")).append(" ")
								.append(ORDENACAO);
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setString(i, TextoUtil.padraoLike(nome));
			
			try(ResultSet rs = ps.executeQuery()){
				List<CompromissoVo> compromissos = new ArrayList<>();
				
				while (rs.next()) {
					compromissos.add(montarCompromisso(rs));
				}
				return compromissos;
			}
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar os compromissos por funcionario", e);
		}
	}
	
	public List<CompromissoVo> findAllByPeriodo(LocalDate inicio, LocalDate fim){
		StringBuilder query = new StringBuilder(SELECT_COMPROMISSO)
								.append("WHERE c.dt_compromisso BETWEEN ? AND ? ")
								.append(ORDENACAO);
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setDate(i++, Date.valueOf(inicio));
			ps.setDate(i++, Date.valueOf(fim));
			
			try(ResultSet rs = ps.executeQuery()){
				List<CompromissoVo> compromissos = new ArrayList<>();
				
				while (rs.next()) {
					compromissos.add(montarCompromisso(rs));
				}
				return compromissos;
			}
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar os compromissos por periodo", e);
		}
	}
	
	public CompromissoVo findByCodigo(Integer codigo){
		StringBuilder query = new StringBuilder(SELECT_COMPROMISSO).append("WHERE c.rowid = ?");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setInt(i, codigo);
			
			try(ResultSet rs = ps.executeQuery()){
				if (rs.next()) {
					return montarCompromisso(rs);
				}
				return null;
			}
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar o compromisso", e);
		}
	}
	
	public List<CompromissoVo> findAllByAgenda(Integer codigoAgenda){
		StringBuilder query = new StringBuilder(SELECT_COMPROMISSO)
								.append("WHERE c.cd_agenda = ? ")
								.append(ORDENACAO);
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setInt(i, codigoAgenda);
			
			try(ResultSet rs = ps.executeQuery()){
				List<CompromissoVo> compromissos = new ArrayList<>();
				
				while (rs.next()) {
					compromissos.add(montarCompromisso(rs));
				}
				return compromissos;
			}
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar os compromissos da agenda", e);
		}
	}
	
	public long countByAgenda(Integer codigoAgenda){
		StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM compromisso WHERE cd_agenda = ?");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setInt(i, codigoAgenda);
			
			try(ResultSet rs = ps.executeQuery()){
				rs.next();
				return rs.getLong(1);
			}
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar os compromissos da agenda", e);
		}
	}
	
	private CompromissoVo montarCompromisso(ResultSet rs) throws SQLException {
		CompromissoVo vo = new CompromissoVo();
		vo.setRowid(rs.getString("id"));
		vo.setCdFuncionario(rs.getString("cdfunc"));
		vo.setNomeFuncionario(rs.getString("nmfunc"));
		vo.setCdAgenda(rs.getString("cdagenda"));
		vo.setNomeAgenda(rs.getString("nmagenda"));
		vo.setData(rs.getDate("dt").toLocalDate().toString());
		vo.setHorario(rs.getTime("hr").toLocalTime().toString());
		return vo;
	}
}
