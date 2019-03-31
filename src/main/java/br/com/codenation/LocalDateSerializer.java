package br.com.codenation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/*Como método put() do OjbectNode não suporta o tipo LocalDate, 
 * é necessário deserializar e serializar a variável contendo
 * a data de nascimento dos jogadores e de criação dos times.
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
