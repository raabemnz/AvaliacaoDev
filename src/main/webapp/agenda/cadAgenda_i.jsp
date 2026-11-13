<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title><s:text name="label.titulo.pagina.cadastro"/></title>
		<link rel='stylesheet' href='webjars/bootstrap/5.1.3/css/bootstrap.min.css'>
	</head>
	<body class="bg-secondary">

		<div class="container">
			<s:form action="%{agendaVo.rowid == null ? 'novoAgendas' : 'salvarAlteracaoAgendas'}">

				<div class="card mt-5">
					<div class="card-header">
						<div class="row">
							<div class="col-sm-5">
								<s:url action="todosAgendas" var="todos"/>
								<a href="${todos}" class="btn btn-primary"><s:text name="label.agendas"/></a>
							</div>
							
							<div class="col-sm">
								<h5 class="card-title">
									<s:if test="agendaVo.rowid == null">
										<s:text name="label.titulo.novo"/>
									</s:if>
									<s:else>
										<s:text name="label.titulo.editar"/>
									</s:else>
								</h5>
							</div>
						</div>
					</div>
					
					<div class="card-body">
						<s:if test="hasActionErrors()">
							<div class="alert alert-danger" role="alert">
								<s:iterator value="actionErrors">
									<div><s:property/></div>
								</s:iterator>
							</div>
						</s:if>
						
						<div class="row align-items-center">
							<label for="id" class="col-sm-2 col-form-label text-center">
								<s:text name="label.campo.codigo"/>
							</label>	

							<div class="col-sm-2">
								<s:textfield cssClass="form-control" id="id" name="agendaVo.rowid" readonly="true" tabindex="-1" cssStyle="pointer-events: none;"/>							
							</div>	
						</div>
						
						<div class="row align-items-center mt-3">
							<label for="nome" class="col-sm-2 col-form-label text-center">
								<s:text name="label.campo.nome"/>
							</label>	

							<div class="col-sm-5">
								<s:textfield cssClass="form-control" id="nome" name="agendaVo.nome" maxlength="%{tamanhoMaximoNome}"/>							
							</div>	
						</div>
						
						<div class="row align-items-center mt-3">
							<label for="periodo" class="col-sm-2 col-form-label text-center">
								<s:text name="label.campo.periodo"/>
							</label>	

							<div class="col-sm-3">
								<s:select 
									cssClass="form-select" 
									id="periodo" 
									name="agendaVo.periodoDisponivel" 
									list="listaPeriodos" 
									listKey="codigo" 
									listValue="descricao" 
									headerKey="" 
									headerValue="Escolha..."/>
							</div>	
						</div>
					</div>

					<div class="card-footer">
						<div class="form-row">
							<button class="btn btn-primary col-sm-4 offset-sm-1"><s:text name="label.botao.salvar"/></button>
							<button type="reset" class="btn btn-secondary col-sm-4 offset-sm-2"><s:text name="label.botao.limpar"/></button>
						</div>
					</div>
				</div>
			</s:form>			
		</div>
		
		<script src="webjars/bootstrap/5.1.3/js/bootstrap.bundle.min.js"></script>
	</body>
</html>
