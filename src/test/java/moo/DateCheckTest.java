package moo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static com.bazaarvoice.jolt.JsonUtils.jsonToObject;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class DateCheckTest {

    @TestConfiguration
    public static class Moo {

        // schema using date-time (to check date validity) and then hard regex format (for midnight EST timezone)
        private String aSkema = "{\n" +
                "  \"definitions\": {},\n" +
                "  \"$schema\": \"http://json-schema.org/draft-04/schema#\",\n" +
                "  \"type\": \"array\",\n" +
                "  \"items\": {\n" +
                "    \"properties\": {\n" +
                "      \"type\": {\n" +
                "        \"type\": \"string\",\n" +
                "        \"pattern\": \"^(.*)$\"\n" +
                "      },\n" +
                "      \"startDate\": {\n" +
                "        \"format\": \"date-time\",\n" +
                "        \"pattern\": \"^(\\\\d{4}-\\\\d{2}-\\\\d{2}T\\\\d{2}:\\\\d{2}:\\\\d{2})-05:00$\"\n" +
                "\n" +
                "      },\n" +
                "      \"endDate\": {\n" +
                "        \"format\": \"date-time\",\n" +
                "        \"pattern\": \"^(\\\\d{4}-\\\\d{2}-\\\\d{2}T\\\\d{2}:\\\\d{2}:\\\\d{2})-05:00$\"\n" +
                "\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";


        @Value("classpath:schema.json")
        private Resource schema;

        @Bean
        public JsonSchema jsonSchema() throws Exception {
            return JsonSchemaFactory.byDefault().getJsonSchema(schema.getURI().toString());

        }

        @Bean
        public JsonSchema jsonSkema() throws Exception {
            JsonNode jsonNode = JsonLoader.fromString(aSkema);
            return JsonSchemaFactory.byDefault().getJsonSchema(jsonNode);

        }
    }

    @Autowired
    JsonSchema jsonSchema;

    @Autowired
    JsonSchema jsonSkema;


    @Test
    public void asdf() throws IOException, ProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        JsonNode dataNode = JsonLoader.fromString("[{\"type\": \"inbd\", \"startDate\": \"2020-02-29T21:46:54-05:00\", \"endDate\":\"2020-02-29T21:46:54-05:00\"}]");

        assertThat(jsonSchema).isNotNull();

        ProcessingReport validate = jsonSchema.validate(dataNode);

        if(!validate.isSuccess())System.out.println(">>>" + validate.iterator().next().getMessage());
        assertThat(validate.isSuccess()).isTrue();

        List<Thing> things = objectMapper.convertValue(dataNode, new TypeReference<ArrayList<Thing>>(){});


        assertThat(things).isNotNull();

        System.out.println("09090909090909090"+things.get(0).toString());

    }

    @Test
    public void sdfg() throws IOException, ProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        JsonNode dataNode = JsonLoader.fromString("[{\"type\": \"inbd\",\"startDate\": \"2020-02-29T21:46:54-05:00\",\"endDate\": \"2020-02-29T21:46:54-05:00\"}]");

        assertThat(jsonSkema).isNotNull();

        ProcessingReport validate = jsonSkema.validate(dataNode);

        if(!validate.isSuccess())System.out.println(">>>" + validate.iterator().next().getMessage());
        assertThat(validate.isSuccess()).isTrue();

        List<Thing> things = objectMapper.convertValue(dataNode, new TypeReference<ArrayList<Thing>>(){});
        assertThat(things).isNotNull();

        System.out.println("09090909090909090"+things.get(0).toString());

    }


    public static final String INFINITY = "9999-01-01T00:00:00-05:00";

    @Test
    public void shouldThrowErrorWhenTwoInboundAreEffective() throws IOException, ProcessingException{

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        // given
        List<Goat> goats = Arrays.asList(
                Goat.builder().type("inbd").media("6").startDate("2020-01-01T00:00:00-05:00").endDate("2020-01-02T00:00:00-05:00").build(),
                Goat.builder().type("inbd").media("6").startDate("2020-01-03T00:00:00-05:00").endDate("2020-01-04T00:00:00-05:00").build(),
                Goat.builder().type("inbd").media("6").startDate("2020-01-02T00:00:00-05:00").endDate("2020-01-03T00:00:00-05:00").build(),
                Goat.builder().type("inbd").media("6").startDate("2020-01-04T00:00:00-05:00").endDate(INFINITY).build(),

                Goat.builder().type("ntf").media("6").startDate("2020-01-02T00:00:00-05:00").endDate("2020-01-03T00:00:00-05:00").build(),
                Goat.builder().type("ntf").media("6").startDate("2020-01-03T00:00:00-05:00").endDate("2020-01-04T00:00:00-05:00").build(),
                Goat.builder().type("ntf").media("6").startDate("2020-01-04T00:00:00-05:00").endDate(INFINITY).build(),
                Goat.builder().type("ntf").media("6").startDate("2020-01-01T00:00:00-05:00").endDate("2020-01-02T00:00:00-05:00").build()

        );


        JsonNode dataNode = objectMapper.convertValue(goats, JsonNode.class);

        assertThat(jsonSkema).isNotNull();

        ProcessingReport validate = jsonSkema.validate(dataNode);

        if(!validate.isSuccess())System.out.println(">>>" + validate.iterator().next().getMessage());
        assertThat(validate.isSuccess()).isTrue();



        List<Thing> things = objectMapper.convertValue(dataNode, new TypeReference<ArrayList<Thing>>(){});

        Map<Pair<String, String>, List<Thing>> collect = things.stream()
            .sorted(Comparator.comparing(Thing::getStartDate, Comparator.nullsFirst(Comparator.naturalOrder())))
                .collect(groupingBy(o -> Pair.of(o.getType(), o.getMedia()), mapping(o -> o, toList())));



        assertThat(collect).isNotNull();


        // when
        List<Thing> duplicatesToReport = things.stream()
                .filter(isInboundEffective.or(isOutboundEffective)) // is effective
                .collect(groupingBy(Thing::getType)) // group by inbd or ntf
                .entrySet().stream().filter(isDuplicate) // is a duplicate
                .collect(collectDupeList()); // list of dupes

        // then
        assertThat(duplicatesToReport).hasSize(4).extracting(Thing::getEndDate).contains(ZonedDateTime.parse(INFINITY));


    }

    private Collector<Map.Entry<String, List<Thing>>, ?, List<Thing>> collectDupeList() {
        return flatMapping(duplicates -> duplicates.getValue().stream(), toList());
    }

    private Predicate<Thing> isInboundEffective = inbound -> inbound.getType().equals("inbd");
    private Predicate<Thing> isOutboundEffective = outbound -> outbound.getType().equals("ntf") && outbound.getEndDate().equals(ZonedDateTime.parse(INFINITY));
    private Predicate<Map.Entry<String, List<Thing>>> isDuplicate = dupe -> dupe.getValue().size() > 1;


    @Data
    @Builder
    private static class Goat {

        private String type;
        private String media;
        private String startDate;
        private String endDate;

    }

    @Data
    private static class Thing {
        @JsonProperty
        private String type;
        @JsonProperty
        private String media;
        @JsonProperty
        private ZonedDateTime startDate;
        @JsonProperty
        private ZonedDateTime endDate;
    }
 }


