package net.knightech.json.jolt.transformer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("")
public class CustomProperties {

	private final Map<String, String> joltspec = new HashMap<>();

	public Map<String, String> getJoltspec() {
		return joltspec;
	}
}
