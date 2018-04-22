/*
 * Created by Hiran Fernando
 */
package simplenem12;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class SimpleNem12ParserImpl implements SimpleNem12Parser {

	private static final Logger LOGGER = Logger.getLogger(SimpleNem12ParserImpl.class.getName());

	public Collection<MeterRead> parseSimpleNem12(File simpleNem12File) {

		List<MeterRead> meterReads = new ArrayList<>();
		String start = "";
		String current = "";
		MeterRead meterRead;

		try {

			Scanner inputStream = new Scanner(simpleNem12File);

			while (inputStream.hasNext()) {

				String data = inputStream.next();
				String[] p = data.split(",");

				if (p[0].equals("200") || p[0].equals("300")) {

					start = "200".equals(p[0]) ? "200" : "0";
					current = p[0];

					if (start.equals(current)) {
						meterRead = new MeterRead(p[1], EnergyUnit.valueOf(p[2]));
						meterReads.add(meterRead);
					} else {
						MeterVolume meterVolume = new MeterVolume(new BigDecimal(p[2]), Quality.valueOf(p[3]));
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
						LocalDate readDate = LocalDate.parse(p[1], formatter);
						MeterRead updateMeterRead = meterReads.get(meterReads.size() - 1);
						updateMeterRead.getVolumes().put(readDate, meterVolume);
					}
				}
			}
			// Close the stream
			inputStream.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return meterReads;
	}

	public static void main(String args[]) {
		File simpleNem12File = new File(args[0]);
		new SimpleNem12ParserImpl().parseSimpleNem12(simpleNem12File);
	}
}
