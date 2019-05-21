import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Clase compilada con Java 1.6
public class Desafio1 {
	
	public static void main(String[] args) throws MalformedURLException, IOException, JSONException, ParseException {
		
		StringBuffer buffer = new StringBuffer();
		if (args.length == 0) {
			
			// Se conecta a la dirección donde está el servicio de Generador de Datos
			HttpURLConnection con = (HttpURLConnection) new URL("http://127.0.0.1:8080/periodos/api").openConnection();
			con.setRequestProperty("Accept", "application/json");
			InputStream is = null;
			InputStreamReader isr = null;
			BufferedReader reader = null;
			try {
				is = con.getInputStream();
				isr = new InputStreamReader(is, "UTF-8");
				reader = new BufferedReader(isr);
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					buffer.append(line);
				}
			} finally {
				if (reader != null) reader.close();
				if (isr != null) isr.close();
				if (is != null) is.close();
			}
		} else {
			
			/*
			 * Si se desea usar un archivo local como entrada
			 * se puede agregar la ruta como argumento
			 */
			FileReader fr = null;
			BufferedReader reader = null;
			try {
				fr = new FileReader(args[0]);
				reader = new BufferedReader(fr);
				for (String line = reader.readLine(); line != null; line = reader.readLine()) {
					buffer.append(line);
				}
			} finally {
				if (reader != null) reader.close();
				if (fr != null) fr.close();
			}
			
		}
		
		// Se arma el objeto JSON y se obtienen las fechas de creación y de fin
		JSONObject json = new JSONObject(buffer.toString());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse(json.getString("fechaCreacion"));
		Date endDate = sdf.parse(json.getString("fechaFin"));
		Calendar cal = Calendar.getInstance();
		
		// Revisión de las fechas
		cal.setTime(startDate);
		if (cal.get(Calendar.DAY_OF_MONTH) != 1) {
			System.out.println("La fecha de creación no corresponde al principio del mes");
			return;
		}
		cal.setTime(endDate);
		if (cal.get(Calendar.DAY_OF_MONTH) != 1) {
			System.out.println("La fecha de fin no corresponde al principio del mes");
			return;
		}

		// Se obtienen las fechas recibidas y se ordenan en una lista
		JSONArray datesArray = json.getJSONArray("fechas");
		int n = datesArray.length();
		List<Date> datesFound = new ArrayList<Date>(n);
		for (int i = 0; i < n; ++i) {
			datesFound.add(sdf.parse(datesArray.getString(i)));
		}
		Collections.sort(datesFound);
		
		/*
		 * Se itera sobre las fechas entre la de creación y de fin (añadiendo 1 mes a la vez)
		 * y al mismo tiempo se itera sobre la lista de fechas recibidas (que está ordenada).
		 */
		List<Date> datesMissing = new LinkedList<Date>();
		cal.setTime(startDate);
		int idx = 0;
		while (cal.getTimeInMillis() <= endDate.getTime()) {
			if (idx < n) {
				long ct = cal.getTimeInMillis();
				long dt = datesFound.get(idx).getTime();
				if (ct < dt) {
					// No es una fecha recibida, agregar fecha faltante
					datesMissing.add(cal.getTime());
					cal.add(Calendar.MONTH, 1);
				} else if (ct == dt) {
					// Es una fecha recibida, no agregar
					++idx;
					cal.add(Calendar.MONTH, 1);
				} else {
					// Ajuste por si acaso
					++idx;
				}
			} else {
				// Se terminó la lista de fechas recibidas, agregar por defecto
				datesMissing.add(cal.getTime());
				cal.add(Calendar.MONTH, 1);
			}
		}
		
		// Imprimir archivo de salida
		FileWriter fw = null;
		PrintWriter writer = null;
		try {
			fw = new FileWriter("salida.txt");
			writer = new PrintWriter(fw);
			writer.println("fecha creación: " + sdf.format(startDate));
			writer.println("fecha fin: " + sdf.format(endDate));
			writer.print("fechas recibidas: ");
			if (n > 0) {
				for (int i = 0; i < n-1; ++i) {
					writer.print(sdf.format(datesFound.get(i)) + ", ");
				}
				writer.println(sdf.format(datesFound.get(n-1)));
			}
			writer.print("fechas faltantes: ");
			if (!datesMissing.isEmpty()) {
				int m = datesMissing.size();
				for (int i = 0; i < m-1; ++i) {
					writer.print(sdf.format(datesMissing.get(i)) + ", ");
				}
				writer.println(sdf.format(datesMissing.get(m-1)));
			}
		} finally {
			if (writer != null) writer.close();
			if (fw != null) fw.close();
		}
	}

}
