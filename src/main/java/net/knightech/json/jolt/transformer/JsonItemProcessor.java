package net.knightech.json.jolt.transformer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

/**
 * This class performs the transformation of the input json to the spec held in the Jolt Chainr processor.
 *
 * @author pknigh2
 */
public class JsonItemProcessor implements ItemProcessor<Object, String> {

  @Autowired
  private Chainr transformSpecification;

  @Autowired
  OperationalActivityCodeLookupProperties operationalActivityCodeLookupProperties;

  @Value("file:./ext-lib/json/output/output.txt")
  private Resource output;

  @Value("file:./ext-lib/json/input/input.txt")
  private Resource input;

  @Value("file:./ext-lib/json/schema/inputSchema.json")
  private Resource inputSchema;

  @Value("file:./ext-lib/json/schema/outputSchema.json")
  private Resource outputSchema;

  private Map<String, Object> context;

  private JsonSchema inboundSkema;
  private JsonSchema outboundSkema;

  @PostConstruct
  public void init() {
  
  inboundSkema = getJsonSchema(inputSchema);
  outboundSkema = getJsonSchema(outputSchema);    
  context = new HashMap<>();
   context.put("activityCodes", operationalActivityCodeLookupProperties.getCodes());
}
  
  @Override
  public String process(final Object json) throws Exception {
    
    final String inbound = JsonUtils.toPrettyJsonString(json);
    
   validate(inbound, inboundSkema);


    
    Object transformed = transformSpecification.transform(json, context);
    final String outbound = JsonUtils.toJsonString(transformed);
    validate(outbound, outboundSkema);
    return outbound;
  }

  private JsonSchema getJsonSchema(final Resource resource) {
    try {
      final String schema = readClassPathResource(resource).toString();
      return JsonSchemaFactory.byDefault().getJsonSchema(JsonLoader.fromString(schema));
    } catch (IOException | ProcessingException e) {
      return null;
    }
  }

  private StringBuilder readClassPathResource(Resource resource) throws IOException {

    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
      StringBuilder stringBuilder = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {

        stringBuilder.append(line);
      }
      br.close();
      return stringBuilder;
    } catch (IOException ex) {
      throw new IOException("Resource not found", ex);

    }
  }

  /**
   * validate
   *
   * @param toValidate
   * @param schemaToValidateAgainst
   * @throws Exception
   */
  private void validate(String toValidate, JsonSchema schemaToValidateAgainst) {

    JsonNode toValidateNode = null;
    try {
      
      toValidateNode = JsonLoader.fromString(toValidate);
      ProcessingReport report;
      report = schemaToValidateAgainst.validate(toValidateNode);

      if (!report.isSuccess()) {
        throw new RuntimeException("JSON failed validation: " + report.toString());
      }
      else
        System.out.println("Schema validated successfully: " + toValidate);
    }
    catch (Exception e) {
      
      throw new RuntimeException("JSON failed validation: " + e.getMessage());
    }
  }

}
