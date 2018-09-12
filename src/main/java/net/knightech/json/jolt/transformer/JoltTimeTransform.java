package net.knightech.json.jolt.transformer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.util.ResourceUtils;

import com.bazaarvoice.jolt.Transform;

public class JoltTimeTransform implements Transform {
	
	Properties config;
	
	public JoltTimeTransform (){
		
		 try {
			config = loadFromFile("jolt-time.properties");
			//printToSystemOutput(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Object transform(Object input) {
		
		DateTimeFormatter outFormatter = DateTimeFormatter.ofPattern(config.getProperty("output-long-date-time"));
		
		// YYMMDD
		List<String> shortDateFields = Arrays.asList(config.getProperty("fields-for-input-short-date").split(","));
		
		shortDateFields.forEach(entry -> {
			
			String inDate = (String) (((Map) input).get(entry));
			LocalDate parsedDate = LocalDate.parse(inDate, DateTimeFormatter.ofPattern(config.getProperty("input-short-date")));
			
			String formattedDateTime = parsedDate.atStartOfDay().format(outFormatter);
			((Map) input).put(entry, formattedDateTime);
			
		});
			
		List<String> longDateFields = Arrays.asList(config.getProperty("fields-for-input-long-date").split(","));
		
		longDateFields.forEach(entry -> {
			
			String inDate = (String) (((Map) input).get(entry));
			LocalDate parsedDate = LocalDate.parse(inDate,  DateTimeFormatter.ofPattern(config.getProperty("input-long-date")));
			
			String formattedDateTime = parsedDate.atStartOfDay().format(outFormatter);
			((Map) input).put(entry, formattedDateTime);
			
		});
				

		// use format mask for this
		List<String> timestampFields = Arrays.asList(config.getProperty("fields-for-input-timestamp").split(","));
		
		timestampFields.forEach(entry -> {
			
			String inDate = (String) (((Map) input).get(entry));
			LocalDateTime parsedDateTime = LocalDateTime.parse(inDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
			
			String formattedDateTime = parsedDateTime.format(outFormatter);
			((Map) input).put(entry, formattedDateTime);
			
		});
		
		return input;
	}
	
 
    private void printToSystemOutput(Properties config) throws IOException {
        config.store(System.out, "Loaded properties:");
    }
 
    private static Properties loadFromFile(String filename) throws IOException {
        Path configLocation = Paths.get(filename);
        System.out.println("Config location: " + configLocation.toString());
        
        File file = ResourceUtils.getFile("classpath:" + filename);
		
        try (FileInputStream stream = new FileInputStream(file)) {
            Properties config = new Properties();
            config.load(stream);
            return config;
        }
    }
}
