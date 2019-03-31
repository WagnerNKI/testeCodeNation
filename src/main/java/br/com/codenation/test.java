package br.com.codenation;

import java.math.BigDecimal;
import java.time.LocalDate;

public class test {

	public static void main(String[] args) {

		DesafioMeuTimeApplication testInstance = new DesafioMeuTimeApplication();
		LocalDate data = LocalDate.of(1999, 1, 15);
		BigDecimal salario = new BigDecimal(500);

		try {
			//testInstance.incluirJogador(100l,1002l, "def", data, 100 ,salario);
			testInstance.incluirTime(2002L,"Barcelona",data,"amarelo","verde");
			//testInstance.buscarNomeJogador(6L);
			//testInstance.buscarJogadoresDoTime (1l);
			//testInstance.buscarCorCamisaTimeDeFora(1L,3L);
			//testInstance.definirCapitao(3l);
			//testInstance.buscarTopJogadores(3);
			//testInstance.buscarTimes();
		} catch (Exception e) {
			// TODO Auto-generated catch block'
			e.printStackTrace();
		}
	}
}
