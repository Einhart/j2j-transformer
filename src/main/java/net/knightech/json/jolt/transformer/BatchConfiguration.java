package net.knightech.json.jolt.transformer;

import static com.bazaarvoice.jolt.JsonUtils.*;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;


	@Value("file:./ext-lib/json/schema/joltSchema.json")
	Resource spec;
	
	@Value("file:./ext-lib/json/output/output.txt")
	private Resource output;
	
	@Value("file:./ext-lib/json/input/input.txt")
	private Resource input;

	private static Object mapLine(String line, int lineNumber) {
		return jsonToObject(line, Charset.forName("ISO-8859-1").toString());
	}

	@Bean
	public FlatFileItemReader<Object> reader() {

		FlatFileItemReader<Object> reader = new FlatFileItemReader<>();
		reader.setResource(input);
		reader.setLineMapper(getObjectLineMapper());
		reader.setRecordSeparatorPolicy(new SepPol());
		return reader;
	}


	private LineMapper<Object> getObjectLineMapper() {
		return BatchConfiguration::mapLine;
	}

	@Bean
	public JsonItemProcessor processor() {
		return new JsonItemProcessor();
	}

	@Bean
	public Job importJsonJob() {
		return jobBuilderFactory.get("importJsonJob").incrementer(new RunIdIncrementer()).flow(step1()).end().build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Object, String>chunk(2).reader(reader()).processor(processor())
				.writer(writer()).build();
	}

	@Bean
	public ItemWriter<String> writer() {

		FlatFileItemWriter<String> writer = new FlatFileItemWriter<>();
		writer.setResource(output);
		writer.setLineAggregator(item -> item.toString());
		
		return writer;
	}

	@Bean
	public Chainr transformSpecification() throws Exception {
		
		String path = spec.getFile().getAbsolutePath();
		String specContent = new String(Files.readAllBytes(new File(path).toPath()));
		List chainrSpecJSON = JsonUtils.jsonToList(specContent);
		return Chainr.fromSpec(chainrSpecJSON);

	}
}
