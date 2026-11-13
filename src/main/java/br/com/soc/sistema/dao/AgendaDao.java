package br.com.soc.sistema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.infra.TextoUtil;
import br.com.soc.sistema.vo.AgendaVo;

public class AgendaDao extends Dao {
	
	private static final String SELECT_AGENDA = 
			"SELECT rowid id, nm_agenda nome, periodo_disponivel periodo FROM agenda ";
	
	public void insertAgenda(AgendaVo agendaVo){
		StringBuilder query = new StringBuilder("INSERT INTO agenda (nm_agenda, periodo_disponivel) values (?, ?)");
		try(
			Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setString(i++, agendaVo.getNome());
			ps.setInt(i++, Integer.parseInt(agendaVo.getPeriodoDisponivel()));
			ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao incluir a agenda", e);
		}
	}

	public int updateAgenda(AgendaVo agendaVo){
		StringBuilder query = new StringBuilder("UPDATE agenda SET nm_agenda = ?, periodo_disponivel = ? WHERE rowid = ?");
		try(
			Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setString(i++, agendaVo.getNome());
			ps.setInt(i++, Integer.parseInt(agendaVo.getPeriodoDisponivel()));
			ps.setLong(i++, Long.parseLong(agendaVo.getRowid()));
			return ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao alterar a agenda", e);
		}
	}

	public int deleteAgenda(Integer codigo){
		StringBuilder query = new StringBuilder("DELETE FROM agenda WHERE rowid = ?");
		try(
			Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			
			int i=1;
			ps.setInt(i, codigo);
			return ps.executeUpdate();
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao excluir a agenda", e);
		}
	}
	
	public List<AgendaVo> findAllAgendas(){
		StringBuilder query = new StringBuilder(SELECT_AGENDA).append("ORDER BY rowid");
		try(
			Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString());
			ResultSet rs = ps.executeQuery()){
			
			List<AgendaVo> agendas = new ArrayList<>();
			while (rs.next()) {
				agendas.add(montarAgenda(rs));
			}
			return agendas;
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar as agendas", e);
		}
	}
	
	public List<AgendaVo> findAllByNome(String nome){
		StringBuilder query = new StringBuilder(SELECT_AGENDA)
								.append("WHERE ").append(TextoUtil.condicaoLike("nm_agenda")).append(" ORDER BY rowid");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setString(i, TextoUtil.padraoLike(nome));
			
			try(ResultSet rs = ps.executeQuery()){
				List<AgendaVo> agendas = new ArrayList<>();
				
				while (rs.next()) {
					agendas.add(montarAgenda(rs));
				}
				return agendas;
			}
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar as agendas por nome", e);
		}
	}
	
	public AgendaVo findByCodigo(Integer codigo){
		StringBuilder query = new StringBuilder(SELECT_AGENDA).append("WHERE rowid = ?");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setInt(i, codigo);
			
			try(ResultSet rs = ps.executeQuery()){
				if (rs.next()) {
					return montarAgenda(rs);
				}
				return null;
			}
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao consultar a agenda", e);
		}
	}
	
	private AgendaVo montarAgenda(ResultSet rs) throws SQLException {
		AgendaVo vo = new AgendaVo();
		vo.setRowid(rs.getString("id"));
		vo.setNome(rs.getString("nome"));
		vo.setPeriodoDisponivel(rs.getString("periodo"));
		return vo;
	}
}
