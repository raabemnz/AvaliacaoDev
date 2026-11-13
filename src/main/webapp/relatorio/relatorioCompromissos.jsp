<%@ page language="java" contentType="text/html; charset=UTF-8"  pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title><s:text name="label.titulo.pagina"/></title>
		<link rel='stylesheet' href='webjars/bootstrap/5.1.3/css/bootstrap.min.css'>
	</head>
	<body class="bg-secondary">

		<div class="container">
			<div class="card mt-5">
				<form id="formRelatorio" method="post" action="visualizarCompromissosRelatorios.action">
					<div class="card-header">
						<div class="row">
							<div class="col-sm-5">
								<s:url action="todosCompromissos" var="linkCompromissos"/>
								<a href="${linkCompromissos}" class="btn btn-primary"><s:text name="label.compromissos"/></a>
							</div>
							
							<div class="col-sm">
								<h5 class="card-title"><s:text name="label.titulo.filtros"/></h5>
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
							<label for="dataInicial" class="col-sm-2 col-form-label text-center">
								<s:text name="label.campo.data.inicial"/>
							</label>	

							<div class="col-sm-3">
								<div class="input-group">
									<input type="date" class="form-control" id="dataInicial" name="dataInicial" required
										value="<s:property value="dataInicial" default=""/>"/>
									
									<%-- Limpa somente esta data (o campo nativo apaga um trecho por vez) --%>
									<button type="button" class="btn btn-outline-secondary" id="limparDataInicial"
										title="<s:text name="label.botao.limpar.data.inicial"/>"
										aria-label="<s:text name="label.botao.limpar.data.inicial"/>">
										<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 16 16" aria-hidden="true">
											<path d="M3 3l10 10M13 3L3 13" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" fill="none"/>
										</svg>
									</button>
								</div>
							</div>	
						</div>
						
						<div class="row align-items-center mt-3">
							<label for="dataFinal" class="col-sm-2 col-form-label text-center">
								<s:text name="label.campo.data.final"/>
							</label>	

							<div class="col-sm-3">
								<div class="input-group">
									<input type="date" class="form-control" id="dataFinal" name="dataFinal" required
										value="<s:property value="dataFinal" default=""/>"/>
									
									<button type="button" class="btn btn-outline-secondary" id="limparDataFinal"
										title="<s:text name="label.botao.limpar.data.final"/>"
										aria-label="<s:text name="label.botao.limpar.data.final"/>">
										<svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 16 16" aria-hidden="true">
											<path d="M3 3l10 10M13 3L3 13" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" fill="none"/>
										</svg>
									</button>
								</div>
							</div>	
						</div>
					</div>

					<div class="card-footer">
						<%-- Abre o relatorio em HTML em uma nova aba --%>
						<button type="submit" class="btn btn-primary" 
							formaction="visualizarCompromissosRelatorios.action" formtarget="_blank">
							<s:text name="label.botao.gerar"/>
						</button>
						
						<%-- Baixa o arquivo Excel na propria aba --%>
						<button type="submit" class="btn btn-success ms-2" 
							formaction="exportarCompromissosRelatorios.action" formtarget="_self">
							<s:text name="label.botao.excel"/>
						</button>
					</div>
				</form>
			</div>
		</div>
		
		<script src="webjars/bootstrap/5.1.3/js/bootstrap.bundle.min.js"></script>
		<script>
		  // impede o envio quando a data final e anterior a data inicial
		  var dataInicial = document.getElementById('dataInicial');
		  var dataFinal = document.getElementById('dataFinal');
		  
		  function validarPeriodo() {
		    var invalido = dataInicial.value && dataFinal.value && dataFinal.value < dataInicial.value;
		    dataFinal.setCustomValidity(invalido ? 'Data final não pode ser anterior à data inicial.' : '');
		  }
		  
		  function limparCampo(campo) {
		    campo.value = '';
		    validarPeriodo();
		    campo.focus();
		  }
		  
		  dataInicial.addEventListener('change', validarPeriodo);
		  dataFinal.addEventListener('change', validarPeriodo);
		  validarPeriodo();
		  
		  document.getElementById('limparDataInicial').addEventListener('click', function () {
		    limparCampo(dataInicial);
		  });
		  
		  document.getElementById('limparDataFinal').addEventListener('click', function () {
		    limparCampo(dataFinal);
		  });
		</script>
	</body>
</html>
