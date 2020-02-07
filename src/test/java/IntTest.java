import com.bazaarvoice.jolt.Chainr;
import com.bazaarvoice.jolt.JsonUtils;
import com.bazaarvoice.jolt.utils.JoltUtils;
import jdk.internal.jline.internal.TestAccessible;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import static com.bazaarvoice.jolt.JsonUtils.jsonToObject;
import static com.bazaarvoice.jolt.utils.JoltUtils.*;

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
    public void asdf(){

        String goat = "{\"Photos\":[{\"Id\":\"327703\",\"Caption\":\"TEST>>photo1\",\"Url\":\"http://bob.com/0001/327703/photo.jpg\"},{\"Id\":\"327704\",\"Caption\":\"TEST>>photo2\",\"Url\":\"http://bob.com/0001/327704/photo.jpg\"}]}";

        Object dsfg = jsonToObject(goat);

        Object transform = transformSpecification.transform(dsfg);

        final String outbound = JsonUtils.toJsonString(transform);

        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<"+outbound);

    }
}
