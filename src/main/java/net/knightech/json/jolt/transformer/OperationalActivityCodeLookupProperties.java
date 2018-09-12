package net.knightech.json.jolt.transformer;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("operationalActivity")
public class OperationalActivityCodeLookupProperties {

	private final Map<String, Object> codes = new HashMap<>();

	public Map<String, Object> getCodes() {
		return codes;
	}
}
