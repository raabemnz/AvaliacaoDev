<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title><s:text name="label.titulo.pagina.consulta"/></title>
		<link rel='stylesheet' href='webjars/bootstrap/5.1.3/css/bootstrap.min.css'>
	</head>
	<body class="bg-secondary">	
		<div class="container">
			<div class="row mt-5 mb-2">
				<div class="col-sm p-0">
					<s:form action="/filtrarCompromissos.action">
						<div class="input-group">
							<span class="input-group-text">
								<strong><s:text name="label.buscar.por"/></strong>
							</span>	
								<s:select  
									cssClass="form-select" 
									name="filtrar.opcoesCombo" 
									list="listaOpcoesCombo"  
									headerKey=""  
									headerValue="Escolha..." 
									listKey="codigo" 
									listValue="descricao"
									value="filtrar.opcoesCombo.codigo"									
								/>
								
								<s:textfield cssClass="form-control" id="nome" name="filtrar.valorBusca"/>
								<button class="btn btn-primary" type="submit"><s:text name="label.pesquisar"/></button>
						</div>
					</s:form>			
				</div>				
			</div>

			<div class="row">
				<s:if test="hasActionErrors()">
					<div class="alert alert-danger" role="alert">
						<s:iterator value="actionErrors">
							<div><s:property/></div>
						</s:iterator>
					</div>
				</s:if>
				
				<table class="table table-light table-striped align-middle">
					<thead>
						<tr>
							<th><s:text name="label.id"/></th>
							<th><s:text name="label.funcionario"/></th>
							<th><s:text name="label.agenda"/></th>
							<th><s:text name="label.data"/></th>
							<th><s:text name="label.horario"/></th>
							<th class="text-end mt-5"><s:text name="label.acao"/></th>
						</tr>
					</thead>
					
					<tbody>
						<s:iterator value="compromissos" >
							<tr>
								<td><s:property value="rowid"/></td>
								<td class="text-break"><s:property value="funcionarioDescricao"/></td>
								<td class="text-break"><s:property value="agendaDescricao"/></td>
								<td><s:property value="dataFormatada"/></td>
								<td><s:property value="horario"/></td>
								<td class="text-end text-nowrap">
									<s:url action="editarCompromissos" var="editar">
										<s:param name="compromissoVo.rowid" value="rowid"></s:param>
									</s:url>

									<a href="${editar}" class="btn btn-warning text-white">
										<s:text name="label.editar"/>
									</a>

									<a href="#" class="btn btn-danger" data-id="<s:property value="rowid"/>" data-bs-toggle="modal" data-bs-target="#confirmarExclusao">
										<s:text name="label.excluir"/>
									</a>
								</td>
							</tr>
						</s:iterator>
					</tbody>
					
					<tfoot class="table-secondary">
						<tr>
							<td colspan="6">
								<s:url action="novoCompromissos" var="novo"/>
								<s:url action="todosFuncionarios" var="funcionarios"/>
								<s:url action="todosAgendas" var="agendas"/>
								
								<a href="${novo}" class="btn btn-primary">
									<s:text name="label.novo"/>
								</a>
								
								<a href="${funcionarios}" class="btn btn-primary ms-2">
									<s:text name="label.funcionarios"/>
								</a>
								
								<a href="${agendas}" class="btn btn-primary ms-2">
									<s:text name="label.agendas"/>
								</a>
								
								<s:url action="compromissosRelatorios" var="relatorio"/>
								
								<a href="${relatorio}" class="btn btn-primary ms-2">
									<s:text name="label.relatorio"/>
								</a>
							</td>
						</tr>
					</tfoot>				
				</table>
			</div>
		</div>
		
		<div class="modal fade" id="confirmarExclusao" 
			data-bs-backdrop="static" data-bs-keyboard="false"
			tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
		  <div class="modal-dialog" role="document">
		    <div class="modal-content">
		      <div class="modal-header">
		        <h5 class="modal-title"><s:text name="label.modal.titulo"/></h5>
		        
		        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
		      </div>
		      
		      <div class="modal-body">
		      	<span><s:text name="label.modal.corpo"/></span>
		      </div>
		      
		      <div class="modal-footer">
	        	<a class="btn btn-secondary" data-bs-dismiss="modal" aria-label="Close">
					<s:text name="label.nao"/>
				</a>
	        	
				<a id="excluir" href="#" class="btn btn-primary" style="width: 75px;">
					<s:text name="label.sim"/>
				</a>						
		      </div>
		    </div>		    
		  </div>
		</div>
		
		<%-- Exclusao por POST: o botao "Sim" do modal envia este formulario --%>
		<form id="formExclusao" method="post" action="excluirCompromissos.action" class="d-none">
			<input type="hidden" name="compromissoVo.rowid" id="rowidExclusao"/>
		</form>
		
		<script src="webjars/bootstrap/5.1.3/js/bootstrap.bundle.min.js"></script>
		<script>
		  document.getElementById('excluir').addEventListener('click', function (event) {
		    event.preventDefault();
		    document.getElementById('formExclusao').submit();
		  });
		  
		  document.getElementById('confirmarExclusao').addEventListener('show.bs.modal', function (event) {
		    var id = event.relatedTarget.getAttribute('data-id');
		    document.getElementById('rowidExclusao').value = id;

		  });
		</script>
	</body>
</html>
