package br.com.codenation;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import br.com.codenation.desafio.annotation.Desafio;
import br.com.codenation.desafio.app.MeuTimeInterface;
import br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException;
import br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException;
import br.com.codenation.desafio.exceptions.TimeNaoEncontradoException;
import br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException;

public class DesafioMeuTimeApplication implements MeuTimeInterface {

	String tipoVerificado1;
	String tipoVerificado2;
	boolean cadastro = false;
	String arquivoNome = "timesEjogadores.json";
	File arquivo = new File(arquivoNome);

	@Desafio("incluirTime")
	public void incluirTime(Long id, String nome, LocalDate dataCriacao, String corUniformePrincipal,
			String corUniformeSecundario) {

		cadastro = true;
		tipoVerificado1 = "time";
		String dataCriacaoStr;
		// Verificar se o time j· existe
		try {
			ChecarSeExisteTime(tipoVerificado1, id, cadastro);
			// Se n„o existe, inclui o time

			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(arquivo);

			// JsonNode times = root.path("times");
			// caso seja a primeira vez que o arquivo È criado, ou n„o exista

			ObjectNode novoTime = mapper.createObjectNode();
			mapper.registerModule(new JavaTimeModule());
			mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
			dataCriacaoStr = dataCriacao.toString();

			novoTime.put("id", id);
			novoTime.put("nome", nome);
			novoTime.put("dataCriacao", dataCriacaoStr);
			novoTime.put("corUniformePrincipal", corUniformePrincipal);
			novoTime.put("corUniformeSecundario", corUniformeSecundario);

			((ArrayNode) root).add(novoTime);

			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(arquivo, root);
			// Se existe, lanÁar erro
			// 'br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}

	}

	@Desafio("incluirJogador")
	public void incluirJogador(Long id, Long idTime, String nome, LocalDate dataNascimento, Integer nivelHabilidade,
			BigDecimal salario) {

		cadastro = true;
		tipoVerificado1 = "jogador";
		String dataNascimentoStr;
		boolean capitao = false;
		int qtdTimes = 0;
		Long idLido;

		try {
			// Verificar se o time existe
			ChecarSeExiste(tipoVerificado1, idTime, id, cadastro);

			// Se o time n„o existir, verificar se o jogador existe
			tipoVerificado2 = "time";
			cadastro = false;
			try {
				ChecarSeExiste(tipoVerificado2, idTime, id, cadastro);
				// Se o time existir, cadastrar jogador

				ObjectMapper mapper = new ObjectMapper();
				JsonNode root = mapper.readTree(arquivo);
				Time[] time = mapper.readValue(arquivo, Time[].class);
				ObjectNode novoJogador = mapper.createObjectNode();
				dataNascimentoStr = dataNascimento.toString();

				novoJogador.put("id", id);
				novoJogador.put("idTime", idTime);
				novoJogador.put("nome", nome);
				novoJogador.put("dataNascimento", dataNascimentoStr);
				novoJogador.put("nivelHabilidade", nivelHabilidade);
				novoJogador.put("salario", salario);
				novoJogador.put("capitao", capitao);

				qtdTimes = root.size();

				for (int contadorTime = 0; contadorTime < qtdTimes; contadorTime++) {

					idLido = time[contadorTime].getId();

					if (idLido.equals(idTime)) {

						JsonNode jogadorNode = root.get(contadorTime).path("jogadores");
						((ArrayNode) jogadorNode).add(novoJogador);
						ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
						writer.writeValue(arquivo, root);

						break;
					}
				}

				// Se o time n√£o existir, retornar erroidTime
				// 'br.com.codenation.desafio.exceptions.TimeNaoEncontradoException'
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Se o jogador existir, retornar erro
			// 'br.com.codenation.desafio.exceptions.IdentificadorUtilizadoException'

		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}

	}

	@Desafio("definirCapitao")
	public void definirCapitao(Long idJogador) {

		tipoVerificado1 = "jogador";
		cadastro = false;
		Long idTime = null;
		ObjectMapper mapper = new ObjectMapper();
		int qtdTimes = 0;
		int qtdJogadores = 0;
		Long idLido = null;
		boolean capitao = false;
		int timeCapitaoIndex = -1;

		try {
			// Verificar se o jogador existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);

			// Se existir, verificar se existe outro capitao no time
			JsonNode times = mapper.readTree(arquivo);
			qtdTimes = times.size();

			for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {
				JsonNode jogadores = times.get(contaTime).findPath("jogadores");
				qtdJogadores = jogadores.size();

				for (int contaJogador = 0; contaJogador < qtdJogadores; contaJogador++) {
					idLido = jogadores.get(contaJogador).path("id").asLong();
					capitao = jogadores.get(contaJogador).path("capitao").asBoolean();

					// Tornar o jogador escolhido como capitao
					if (idLido.equals(idJogador) && !capitao) {
						((ObjectNode) jogadores.get(contaJogador)).put("capitao", true);
						timeCapitaoIndex = contaTime;
						contaJogador = -1;

						// Se outro jogador for capitao, torna-lo jogador normal
					} else if (!idLido.equals(idJogador) && capitao && timeCapitaoIndex == contaTime) {
						((ObjectNode) jogadores.get(contaJogador)).put("capitao", false);

					}

				}

			}

			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			writer.writeValue(arquivo, times);

			// Se n√£o existir, retornar erro
			// 'br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();

		}

	}

	@Desafio("buscarCapitaoDoTime")
	public Long buscarCapitaoDoTime(Long idTime) {

		tipoVerificado1 = "time";
		cadastro = false;
		Long idJogador = null;
		String tipoBuscado = "capitao";

		try {

			// Verificar se o time existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);

			// Se existir, verificar se tem um capitao cadastrado

			tipoVerificado2 = "capitao";
			cadastro = false;

			try {
				ChecarSeExiste(tipoVerificado2, idTime, idJogador, cadastro);
				// Se tiver capitao, retornar o id do jogador

				idJogador = retornaId(idTime, tipoBuscado);
				System.out.println(idJogador);
				// Se n„o tiver capitao cadastrado, retornar erro
				// 'br.com.codenation.desafio.exceptions.CapitaoNaoInformadoException'
				// Se time n„o existir, retornar erro
				// 'br.com.codenation.desafio.exceptions.TimeNaoEncontradoException'
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();

		}
		return idJogador;
	}

	@Desafio("buscarNomeJogador")
	public String buscarNomeJogador(Long idJogador) {

		tipoVerificado1 = "jogador";
		cadastro = false;
		Long idTime = null;
		String nome;

		try {
			// Verificar se o jogador existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);
			// Se existir, retornar o nome do jogador

			nome = retornaNome(idJogador, tipoVerificado1);
			System.out.println(nome);

			// Se n√£o exisir, retorna erro
			// 'br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();

		}
		return nome;
	}

	@Desafio("buscarNomeTime")
	public String buscarNomeTime(Long idTime) {

		tipoVerificado1 = "time";
		cadastro = false;
		Long idJogador = null;
		String nome;

		try {
			// Verificar se o time existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);
			// Se existir, retornar o nome do time

			nome = retornaNome(idTime, tipoVerificado1);
			System.out.println(nome);

			// Se n√£o existir, retorna erro
			// 'br.com.codenation.desafio.exceptions.TimeNaoEncontradoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();

		}
		return nome;
	}

	@Desafio("buscarJogadoresDoTime")
	public List<Long> buscarJogadoresDoTime(Long idTime) {

		tipoVerificado1 = "time";
		cadastro = false;
		Long idJogador = null;
		List<Long> lista = null;
		try {
			// Verificar se o time existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);
			// Se existir, ordenar o jogadores por id e retornar a lista de id's dos
			// jogadores

			lista = retornaLista(idTime);
			System.out.println(lista);
			// Se n√£o existir, retorna erro
			// 'br.com.codenation.desafio.exceptions.TimeNaoEncontradoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw new UnsupportedOperationException();
		}
		return lista;
	}

	@Desafio("buscarMelhorJogadorDoTime")
	public Long buscarMelhorJogadorDoTime(Long idTime) {

		tipoVerificado1 = "time";
		cadastro = false;
		Long idJogador = null;
		String tipoBuscado = "melhorJogadorTime";

		try {
			// Verificar se o time existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);

			// Se existir, verificar qual o melhor jogador e retornar o id do jogador
			idJogador = retornaId(idTime, tipoBuscado);
			System.out.println(idJogador);
			// Se n√£o existir, retorna erro
			// 'br.com.codenation.desafio.exceptions.TimeNaoEncontradoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();

		}
		return idJogador;
	}

	@Desafio("buscarJogadorMaisVelho")
	public Long buscarJogadorMaisVelho(Long idTime) {

		tipoVerificado1 = "time";
		cadastro = false;
		Long idJogador = null;
		String tipoBuscado = "maisVelho";

		try {
			// Verificar se o time existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);

			// Se existir, verificar os jogador mais velho
			// Caso haja dois jogadores com mesma idade, usar o de menor id
			idJogador = retornaId(idTime, tipoBuscado);
			System.out.println(idJogador);
			// Se n√£o existir, retorna erro
			// 'br.com.codenation.desafio.exceptions.TimeNaoEncontradoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();

		}
		return idJogador;
	}

	@Desafio("buscarTimes")
	public List<Long> buscarTimes() {
		List<Long> lista = null;
		// Mostrar todos os times cadastrados em forma de lista ordenada por id
		// Retornar lista vazia, se n√£o tiverem times cadastrados
		try {
			lista = retornaLista(0l);
			System.out.println(lista);

		} catch (Exception e) {
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}

		return lista;
	}

	@Desafio("buscarJogadorMaiorSalario")
	public Long buscarJogadorMaiorSalario(Long idTime) {

		tipoVerificado1 = "time";
		cadastro = false;
		Long idJogador = null;
		String tipoBuscado = "maiorSalario";

		try {
			// Verificar se o time existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);
			// Se existir, verificar os jogador de maior sal√°rio
			// Caso haja dois jogadores com mesmo sal√°rio, usar o de menor id
			idJogador = retornaId(idTime, tipoBuscado);
			System.out.println(idJogador);
			// Se n√£o existir, retorna erro
			// 'br.com.codenation.desafio.exceptions.TimeNaoEncontradoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			throw new UnsupportedOperationException();
		}
		return idJogador;
	}

	@Desafio("buscarSalarioDoJogador")
	public BigDecimal buscarSalarioDoJogador(Long idJogador) {

		tipoVerificado1 = "jogador";
		cadastro = false;
		Long idTime = null;
		BigDecimal salario;

		try {
			// Verificar se o jogador existe
			ChecarSeExiste(tipoVerificado1, idTime, idJogador, cadastro);
			// Se existir, retornar o sal√°rio do jogador
			salario = retornaSalario(idJogador);
			System.out.println(salario);

			// Se n√£o existir, retorna erro
			// 'br.com.codenation.desafio.exceptions.JogadorNaoEncontradoException'
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}
		return salario;
	}

	@Desafio("buscarTopJogadores")
	public List<Long> buscarTopJogadores(Integer top) {

		ObjectMapper mapper = new ObjectMapper();
		int qtdTimes = 0;
		int qtdJogadores = 0;
		Long idJogador = null;
		List<Long> lista = new ArrayList<Long>();
		List<Jogadores> todosJogadores = new ArrayList<Jogadores>();

		try {
			Time[] times = mapper.readValue(arquivo, Time[].class);
			qtdTimes = times.length;
			for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {
				Jogadores[] jogadores = times[contaTime].getJogadores();
				qtdJogadores = jogadores.length;

				for (int contaJogador = 0; contaJogador < qtdJogadores; contaJogador++) {
					todosJogadores.add(jogadores[contaJogador]);

				}
			}

			Collections.sort(todosJogadores, Comparator.comparing(Jogadores::getId));
			Collections.sort(todosJogadores, Comparator.comparing(Jogadores::getNivelHabilidade).reversed());

			for (int contaTopJogadores = 0; contaTopJogadores < top; contaTopJogadores++) {
				idJogador = todosJogadores.get(contaTopJogadores).getId();
				lista.add(idJogador);
			}
			System.out.println(lista);

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new UnsupportedOperationException();
		}

		return lista;
	}

	@Desafio("buscarCorCamisaTimeDeFora")
	public String buscarCorCamisaTimeDeFora(Long timeDaCasa, Long timeDeFora) {

		tipoVerificado1 = "time";
		Long idJogador = null;
		cadastro = false;
		String[] uniformeCasa = null;
		String[] uniformeFora = null;
		String corUniformeFora = null;

		// Verificar se os dois times existem
		try {
			ChecarSeExiste(tipoVerificado1, timeDaCasa, idJogador, cadastro);
			ChecarSeExiste(tipoVerificado1, timeDeFora, idJogador, cadastro);
			// Se existirem
			// Selecionar a cor do uniforme principal do timeDaCasa
			uniformeCasa = retornaUniformes(timeDaCasa);
			// Selecionar a cor do uniforme principal do timeDeFora
			uniformeFora = retornaUniformes(timeDeFora);

			// Comparar as cores
			if (uniformeCasa[0].equals(uniformeFora[0])) {
				// Se forem iguais, retorna cor do uniforme secund√°rio do timeDeFora
				corUniformeFora = uniformeFora[1];

			} else {
				// Se forem diferentes, retornar cor do uniforme principal do timeDeFora
				corUniformeFora = uniformeFora[0];

			}
			System.out.println(corUniformeFora);
			return corUniformeFora;
		} catch (IdentificadorUtilizadoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JogadorNaoEncontradoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeNaoEncontradoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Se n√£o existir, retorna erro
		// 'br.com.codenation.desafio.exceptions.TimeNaoEncontradoException'

		throw new UnsupportedOperationException();
	}

	public boolean ChecarSeExisteTime(String tipoVerificado, Long idTime, boolean cadastro) {
		int qtdTimes = 0;
		Long idTimeLido = null;
		ObjectMapper mapper = new ObjectMapper();

		try {

			// utilizando o mÈtodo createNewFile tanto para checar se o arquivo j· foi
			// criado,
			// quanto para cria-lo

			if (arquivo.createNewFile()) {
				ArrayNode principal = mapper.createArrayNode();

				ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
				writer.writeValue(arquivo, principal);

				return false;

			} else {

				// pegar a quantidade de times cadastrados
				mapper.registerModule(new JavaTimeModule());
				mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

				JsonNode root = mapper.readTree(arquivo);
				qtdTimes = root.size();
				Time[] time = mapper.readValue(arquivo, Time[].class);

				// percorrer os id dos times para ver se algum possui o mesmo id
				for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {
					// verifica o tipoVerificado para cada caso
					// se for "time"

					// pegar o id do time
					idTimeLido = time[contaTime].getId();

					if (cadastro) {
						// Comparar com o id dado
						if (idTimeLido.equals(idTime)) {

							throw new IdentificadorUtilizadoException("Identificador Utilizado");

						} else if (contaTime == qtdTimes - 1) {

							return true;

						}

					} else {

						if (idTimeLido.equals(idTime)) {

							return true;

						} else if (contaTime == qtdTimes - 1) {

							throw new TimeNaoEncontradoException("Time N„o Encontrado");

						}
					}

				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public boolean ChecarSeExiste(String tipoVerificado, Long idTime, Long idJogador, boolean cadastro)
			throws IdentificadorUtilizadoException, JogadorNaoEncontradoException, TimeNaoEncontradoException {

		int qtdTimes = 0;
		Long idTimeLido = null;
		int qtdJogadores = 0;
		Long idJogadorLido = null;

		ObjectMapper mapper = new ObjectMapper();

		try {

			// utilizando o mÈtodo createNewFile tanto para checar se o arquivo j· foi
			// criado,
			// quanto para cria-lo

			if (arquivo.createNewFile()) {
				ArrayNode principal = mapper.createArrayNode();

				ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
				writer.writeValue(arquivo, principal);

				return false;

			} else {

				// pegar a quantidade de times cadastrados
				mapper.registerModule(new JavaTimeModule());
				mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

				JsonNode root = mapper.readTree(arquivo);
				qtdTimes = root.size();
				Time[] time = mapper.readValue(arquivo, Time[].class);

				// percorrer os id dos times para ver se algum possui o mesmo id
				for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {
					// verifica o tipoVerificado para cada caso
					// se for "time"

					if (tipoVerificado == "time") {
						// pegar o id do time
						idTimeLido = time[contaTime].getId();

						if (cadastro) {
							// Comparar com o id dado
							if (idTimeLido.equals(idTime)) {

								throw new IdentificadorUtilizadoException("Identificador Utilizado");

							} else if (contaTime == qtdTimes - 1) {

								return true;

							}

						} else {

							if (idTimeLido.equals(idTime)) {

								return true;

							} else if (contaTime == qtdTimes - 1) {

								throw new TimeNaoEncontradoException("Time N„o Encontrado");

							}
						}
					} else if (tipoVerificado == "jogador") {

						// Verificando se dentro do time j· existe um node "jogadores",
						// se n„o tiver e for um cadastro,
						// cria o node e continua as iteraÁıes dentro dos times,
						// para verificar o jogador via id

						JsonNode jogadorNode = root.get(contaTime).path("jogadores");

						if (jogadorNode.isMissingNode()) {

							if (cadastro) {
								JsonNode timeNode = root.get(contaTime);
								ArrayNode jogadores = mapper.createArrayNode();
								((ObjectNode) timeNode).set("jogadores", jogadores);
								ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
								writer.writeValue(arquivo, root);

								continue;

							} else {
								if (contaTime == qtdTimes - 1) {

									throw new JogadorNaoEncontradoException();
								}
							}
							// se o node "jogadores j· estiver criado, procura dentro do node
						} else {

							// pegar a quantidade de jogadores cadastrados
							Jogadores[] jogadores = time[contaTime].getJogadores();
							qtdJogadores = jogadores.length;

							// percorrer os jogadores para ver se algum possui o mesmo id
							for (int contaJogador = 0; contaJogador < qtdJogadores; contaJogador++) {
								// pegar o id do jogador
								idJogadorLido = jogadores[contaJogador].getId();

								if (cadastro) {
									// Comparar com o id dado
									if (idJogadorLido.equals(idJogador)) {

										throw new IdentificadorUtilizadoException("Identificador Utilizado");
									}

								} else {
									if (idJogadorLido.equals(idJogador)) {

										return true;

									} else if (contaTime == qtdTimes - 1 && contaJogador == qtdJogadores - 1) {

										throw new JogadorNaoEncontradoException();
										// caso n„o consiga encontrar, e tenha verificado todos os times,
										// retorna o erro "Jogador N„o Encontrado"
									}
								}
							}
						}
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return false;

	}

	public String retornaNome(Long idBuscado, String tipoBuscado) {

		ObjectMapper mapper = new ObjectMapper();
		int qtdTimes = 0;
		int qtdJogadores = 0;
		Long idLido = null;

		try {
			Time[] time = mapper.readValue(arquivo, Time[].class);
			qtdTimes = time.length;

			for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {

				if (tipoBuscado == "time") {
					idLido = time[contaTime].getId();

					if (idLido.equals(idBuscado)) {
						return time[contaTime].getNome();
					}
				} else if (tipoBuscado == "jogador") {
					Jogadores[] jogadores = time[contaTime].getJogadores();
					qtdJogadores = jogadores.length;

					for (int contaJogadores = 0; contaJogadores < qtdJogadores; contaJogadores++) {
						idLido = jogadores[contaJogadores].getId();

						if (idLido.equals(idBuscado)) {
							return jogadores[contaJogadores].getNome();
						}
					}
				}
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public Long retornaId(Long idTime, String tipoBuscado) throws CapitaoNaoInformadoException {

		ObjectMapper mapper = new ObjectMapper();
		int qtdTimes, qtdJogadores = 0;
		int melhorJogadorIndex = 0;
		int maisVelhoIndex = 0;
		int maiorSalarioIndex = 0;
		int nivelHabLido, nivelHabAtual = 0;
		Long idLido, idMelhorJogador, idMaisVelho, idMaiorSalario = null;
		LocalDate dataNascLida = null, dataNascMaisAntiga = null;
		BigDecimal salarioLido, salarioMaior;

		try {
			Time[] time = mapper.readValue(arquivo, Time[].class);
			qtdTimes = time.length;

			for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {

				if (idTime.equals(time[contaTime].getId())) {
					Jogadores[] jogadores = time[contaTime].getJogadores();
					qtdJogadores = jogadores.length;

					for (int contaJogador = 0; contaJogador < qtdJogadores; contaJogador++)

						if (tipoBuscado == "capitao") {
							if (jogadores[contaJogador].isCapitao()) {
								return jogadores[contaJogador].getId();

							} else if (contaJogador == qtdJogadores - 1) {

								throw new CapitaoNaoInformadoException("Capit„o N„o Encontrado");
							}

						} else if (tipoBuscado == "melhorJogadorTime") {

							nivelHabLido = jogadores[contaJogador].getNivelHabilidade();
							nivelHabAtual = jogadores[melhorJogadorIndex].getNivelHabilidade();

							if (nivelHabLido > nivelHabAtual) {
								melhorJogadorIndex = contaJogador;

							} else if (nivelHabLido == nivelHabAtual) {

								idLido = jogadores[contaJogador].getId();
								idMelhorJogador = jogadores[melhorJogadorIndex].getId();

								if (idLido.compareTo(idMelhorJogador) > 0) {
									melhorJogadorIndex = contaJogador;
								}
							}

							if (contaJogador == qtdJogadores - 1) {
								return jogadores[melhorJogadorIndex].getId();
							}
						} else if (tipoBuscado == "maisVelho") {

							dataNascLida = jogadores[contaJogador].getDataNascimento();
							dataNascMaisAntiga = jogadores[maisVelhoIndex].getDataNascimento();

							if (dataNascLida.isBefore(dataNascMaisAntiga)) {
								maisVelhoIndex = contaJogador;

							} else if (dataNascLida.isEqual(dataNascMaisAntiga)) {

								idLido = jogadores[contaJogador].getId();
								idMaisVelho = jogadores[melhorJogadorIndex].getId();

								if (idLido.compareTo(idMaisVelho) > 0) {
									maisVelhoIndex = contaJogador;
								}
							}

							if (contaJogador == qtdJogadores - 1) {
								return jogadores[maisVelhoIndex].getId();
							}

						} else if (tipoBuscado == "maiorSalario") {

							salarioLido = jogadores[contaJogador].getSalario();
							salarioMaior = jogadores[maiorSalarioIndex].getSalario();

							if (salarioLido.compareTo(salarioMaior) == 1) {
								maiorSalarioIndex = contaJogador;

							} else if (salarioLido.compareTo(salarioMaior) == 0) {

								idLido = jogadores[contaJogador].getId();
								idMaiorSalario = jogadores[melhorJogadorIndex].getId();

								if (idLido.compareTo(idMaiorSalario) > 0) {
									maiorSalarioIndex = contaJogador;
								}
							}

							if (contaJogador == qtdJogadores - 1) {
								return jogadores[maiorSalarioIndex].getId();
							}

						}

				}
			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public BigDecimal retornaSalario(Long idJogador) {

		int qtdTimes = 0;
		int qtdJogadores = 0;
		Long idLido = null;

		ObjectMapper mapper = new ObjectMapper();
		Time[] time;
		try {
			time = mapper.readValue(arquivo, Time[].class);
			qtdTimes = time.length;

			for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {

				Jogadores[] jogadores = time[contaTime].getJogadores();
				qtdJogadores = jogadores.length;

				for (int contaJogador = 0; contaJogador < qtdJogadores; contaJogador++) {
					idLido = jogadores[contaJogador].getId();

					if (idLido.equals(idJogador)) {
						return jogadores[contaJogador].getSalario();
					}

				}

			}
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public List<Long> retornaLista(Long idTime) {

		List<Long> lista = new ArrayList<Long>();
		ObjectMapper mapper = new ObjectMapper();
		int qtdTimes = 0;
		int qtdJogadores = 0;

		try {
			Time[] times = mapper.readValue(arquivo, Time[].class);
			qtdTimes = times.length;

			for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {

				if (idTime.equals(0l)) {
					lista.add(times[contaTime].getId());

				} else {
					if (idTime.equals(times[contaTime].getId())) {
						Jogadores[] jogadores = times[contaTime].getJogadores();
						qtdJogadores = jogadores.length;

						for (int contaJogador = 0; contaJogador < qtdJogadores; contaJogador++) {
							lista.add(jogadores[contaJogador].getId());
						}

					}

				}
			}
			// ordenando a lista por ordem ascendente
			lista.sort(null);

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lista;
	}

	public String[] retornaUniformes(Long idTime) {

		ObjectMapper mapper = new ObjectMapper();
		int qtdTimes = 0;
		String[] uniformes = new String[2];

		try {
			Time[] times = mapper.readValue(arquivo, Time[].class);
			qtdTimes = times.length;

			for (int contaTime = 0; contaTime < qtdTimes; contaTime++) {
				if (idTime.equals(times[contaTime].getId())) {
					uniformes[0] = times[contaTime].getCorUniformePrincipal();
					uniformes[1] = times[contaTime].getCorUniformeSecundario();
				}
			}

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return uniformes;
	}
}
