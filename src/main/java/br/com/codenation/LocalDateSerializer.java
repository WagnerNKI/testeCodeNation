package br.com.codenation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/*Como m�todo put() do OjbectNode n�o suporta o tipo LocalDate, 
 * � necess�rio deserializar e serializar a vari�vel contendo
 * a data de nascimento dos jogadores e de cria��o dos times.
 */

public class LocalDateSerializer extends StdSerializer<LocalDate>{
	
	public LocalDateSerializer () {
		super(LocalDate.class);
	}	
	@Override
	
	public void serialize(LocalDate value, JsonGenerator generator, SerializerProvider provider) throws IOException {
        generator.writeString(value.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}
