package util;

public class ErrorModule {

	public static String process(String origen, String destino, String transformation) {

			String error;
			if ((origen == "null") && (destino != "null")) {
				error = "El registro no existe en ORIGEN. Es posible que se trate de un borrado f�sico en la BBDD origen.";
			} else if ((origen != "null") && (destino == "null")) {
				error = "El registro no existe en DESTINO. Esto puede ser debido a un error en la condici�n de cruce.";
			} else {
				error = "El resultado no coincide. Existe un error a la hora de realizar la transformaci�n: " + transformation;
			}
			
			return error;
		
		}
	
}
