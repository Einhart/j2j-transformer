package net.knightech.json.jolt.transformer;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;

@Deprecated 
public class JsonItemWriter implements ItemWriter<Object> {

	private static final Logger log = LoggerFactory.getLogger(JsonItemWriter.class);
	
	@Override
	public void write(List<? extends Object> items) throws Exception {
		  log.info("Converted Json (" + items + ")");
	}

}
