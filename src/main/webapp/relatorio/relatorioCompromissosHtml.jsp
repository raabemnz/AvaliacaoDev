<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title><s:text name="label.titulo.pagina"/></title>
		<link rel='stylesheet' href='webjars/bootstrap/5.1.3/css/bootstrap.min.css'>
		<style>
			@media print {
				body { background-color: #fff !important; }
			}
		</style>
	</head>
	<body class="bg-secondary">

		<div class="container">
			<div class="card mt-5 mb-5">
				<div class="card-header">
					<div class="row align-items-center">
						<div class="col-sm">
							<h5 class="mb-1"><s:text name="label.titulo.pagina"/></h5>
							<div>
								<strong><s:text name="label.resultado.periodo"/></strong>
								<s:property value="periodoFormatado"/>
							</div>
						</div>
						
						<div class="col-sm-auto d-print-none">
							<button type="button" class="btn btn-primary" onclick="window.print()">
								<s:text name="label.botao.imprimir"/>
							</button>
						</div>
					</div>
				</div>
				
				<div class="card-body">
					<s:if test="compromissos.isEmpty()">
						<div class="alert alert-warning mb-0" role="alert">
							<s:text name="label.resultado.vazio"/>
						</div>
					</s:if>
					<s:else>
						<table class="table table-light table-striped align-middle">
							<thead>
								<tr>
									<th><s:text name="label.coluna.cd.funcionario"/></th>
									<th><s:text name="label.coluna.nm.funcionario"/></th>
									<th><s:text name="label.coluna.cd.agenda"/></th>
									<th><s:text name="label.coluna.nm.agenda"/></th>
									<th><s:text name="label.coluna.data"/></th>
									<th><s:text name="label.coluna.hora"/></th>
								</tr>
							</thead>
							
							<tbody>
								<s:iterator value="compromissos">
									<tr>
										<td><s:property value="cdFuncionario"/></td>
										<td class="text-break"><s:property value="nomeFuncionario"/></td>
										<td><s:property value="cdAgenda"/></td>
										<td class="text-break"><s:property value="nomeAgenda"/></td>
										<td><s:property value="dataFormatada"/></td>
										<td><s:property value="horarioFormatado"/></td>
									</tr>
								</s:iterator>
							</tbody>
						</table>
						
						<div>
							<strong><s:text name="label.resultado.total"/></strong>
							<s:property value="compromissos.size()"/>
						</div>
					</s:else>
				</div>
			</div>
		</div>
	</body>
</html>
