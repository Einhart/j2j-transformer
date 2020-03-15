package moo;

import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.utils.JoltUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static com.bazaarvoice.jolt.JsonUtils.jsonToObject;
import static com.bazaarvoice.jolt.utils.JoltUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class IntTest {


    @TestConfiguration
    public static class Moo {

        @Value("classpath:test.json")
        Resource spec;

        @Bean
        public Chainr transformSpecification() throws Exception {

            String path = spec.getFile().getAbsolutePath();
            String specContent = new String(Files.readAllBytes(new File(path).toPath()));
            List chainrSpecJSON = JsonUtils.jsonToList(specContent);
            return Chainr.fromSpec(chainrSpecJSON);

        }
    }

    @Autowired
    private Chainr transformSpecification;

    @Test
    public void asdf() throws IOException {

        /* String goat = "{\"Photos\":[{\"Id\":\"327703\",\"Caption\":\"TEST>>photo1\",\"Url\":\"http://bob.com/0001/327703/photo.jpg\"},{\"Id\":\"327704\",\"Caption\":\"TEST>>photo2\",\"Url\":\"http://bob.com/0001/327704/photo.jpg\"}]}";
        Object toObject = jsonToObject(goat);*/

        // JsonNode renders the Json without the root
        String json = "[{\"Id\":\"327703\",\"Caption\":\"TEST>>photo1\",\"Url\":\"http://bob.com/0001/327703/photo.jpg\"},{\"Id\":\"327704\",\"Caption\":\"TEST>>photo2\",\"Url\":\"http://bob.com/0001/327704/photo.jpg\"}]";
        ObjectMapper objectMapper = new ObjectMapper();

        // add the root back on
        ArrayNode arrayNode = (ArrayNode)objectMapper.readTree(json);
        ObjectNode objectNode = objectMapper.createObjectNode();
        JsonNode main = objectNode.set("Photos", arrayNode);

        // prepare the object for transformation
        Object toObject = jsonToObject(main.toString());

        // and transformation...
        Object transform = transformSpecification.transform(toObject);

        final String outbound = JsonUtils.toJsonString(transform);

        assertThat(outbound).isNotNull();
        System.out.println("+ + + : " + outbound + " + + +");

    }
}
