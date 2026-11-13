package br.com.soc.sistema.infra;

import java.text.Normalizer;
import java.util.Locale;

public final class TextoUtil {
	
	private static final String COM_ACENTO = 
			"\u00E1\u00E0\u00E2\u00E3\u00E4\u00E9\u00E8\u00EA\u00EB\u00ED\u00EC\u00EE\u00EF"
			+ "\u00F3\u00F2\u00F4\u00F5\u00F6\u00FA\u00F9\u00FB\u00FC\u00E7\u00F1";
	private static final String SEM_ACENTO = "aaaaaeeeeiiiiooooouuuucn";
	
	private TextoUtil() {}

	public static String colunaSemAcento(String coluna) {
		return "TRANSLATE(lower(" + coluna + "), '" + COM_ACENTO + "', '" + SEM_ACENTO + "')";
	}

	public static String condicaoLike(String coluna) {
		return colunaSemAcento(coluna) + " like ? escape '\\'";
	}

	public static String padraoLike(String valor) {
		String texto = removerAcentos(valor).toLowerCase(Locale.ROOT);
		
		String escapado = texto
				.replace("\\", "\\\\")
				.replace("%", "\\%")
				.replace("_", "\\_");
		
		return "%" + escapado + "%";
	}
	
	public static String removerAcentos(String texto) {
		if(texto == null)
			return "";
		
		return Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
	}
}
