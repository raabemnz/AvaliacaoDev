package br.com.soc.sistema.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.soc.sistema.exception.TechnicalException;
import br.com.soc.sistema.infra.TextoUtil;
import br.com.soc.sistema.vo.FuncionarioVo;

public class FuncionarioDao extends Dao {

	public String insertFuncionario(FuncionarioVo funcionarioVo){
		StringBuilder query = new StringBuilder("INSERT INTO funcionario (nm_funcionario) values (?)");
		try(
			Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS)){
			
			int i=1;
			ps.setString(i++, funcionarioVo.getNome());
			ps.executeUpdate();
			
			try(ResultSet chaves = ps.getGeneratedKeys()){
				return chaves.next() ? chaves.getString(1) : null;
			}
		}catch (SQLException e) {
			throw new TechnicalException("Erro ao incluir o funcionario", e);
		}
	}
	
	public int updateFuncionario(FuncionarioVo funcionarioVo){
	    StringBuilder query = new StringBuilder("UPDATE funcionario SET nm_funcionario = ? WHERE rowid = ?");
	    try(
	        Connection con = getConexao();
	        PreparedStatement ps = con.prepareStatement(query.toString())){

	        int i=1;
	        ps.setString(i++, funcionarioVo.getNome());
	        ps.setLong(i++, Long.parseLong(funcionarioVo.getRowid()));
	        return ps.executeUpdate();
	    }catch (SQLException e) {
	        throw new TechnicalException("Erro ao alterar o funcionario", e);
	    }
	}

	public int deleteFuncionario(Integer codigo){
	    StringBuilder query = new StringBuilder("DELETE FROM funcionario WHERE rowid = ?");
	    try(
	        Connection con = getConexao();
	        PreparedStatement ps = con.prepareStatement(query.toString())){

	        int i=1;
	        ps.setInt(i, codigo);
	        return ps.executeUpdate();
	    }catch (SQLException e) {
	        throw new TechnicalException("Erro ao excluir o funcionario", e);
	    }
	}

	
	public List<FuncionarioVo> findAllFuncionarios(){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_funcionario nome FROM funcionario");
		try(
			Connection con = getConexao();
			PreparedStatement  ps = con.prepareStatement(query.toString());
			ResultSet rs = ps.executeQuery()){
			
			FuncionarioVo vo =  null;
			List<FuncionarioVo> funcionarios = new ArrayList<>();
			while (rs.next()) {
				vo = new FuncionarioVo();
				vo.setRowid(rs.getString("id"));
				vo.setNome(rs.getString("nome"));	
				
				funcionarios.add(vo);
			}
			return funcionarios;
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return Collections.emptyList();
	}
	
	public List<FuncionarioVo> findAllByNome(String nome){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_funcionario nome FROM funcionario ")
								.append("WHERE ").append(TextoUtil.condicaoLike("nm_funcionario"));
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setString(i, TextoUtil.padraoLike(nome));
			
			try(ResultSet rs = ps.executeQuery()){
				FuncionarioVo vo =  null;
				List<FuncionarioVo> funcionarios = new ArrayList<>();
				
				while (rs.next()) {
					vo = new FuncionarioVo();
					vo.setRowid(rs.getString("id"));
					vo.setNome(rs.getString("nome"));	
					
					funcionarios.add(vo);
				}
				return funcionarios;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}		
		return Collections.emptyList();
	}
	
	public FuncionarioVo findByCodigo(Integer codigo){
		StringBuilder query = new StringBuilder("SELECT rowid id, nm_funcionario nome FROM funcionario ")
								.append("WHERE rowid = ?");
		
		try(Connection con = getConexao();
			PreparedStatement ps = con.prepareStatement(query.toString())){
			int i = 1;
			
			ps.setInt(i, codigo);
			
			try(ResultSet rs = ps.executeQuery()){
				FuncionarioVo vo =  null;
				
				while (rs.next()) {
					vo = new FuncionarioVo();
					vo.setRowid(rs.getString("id"));
					vo.setNome(rs.getString("nome"));	
				}
				return vo;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}		
		return null;
	}
}