package net.knightech.json.jolt.transformer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("")
public class AmountProperties {

	private final Map<String, Object> amountprops = new HashMap<>();

	public Map<String, Object> getAmountprops() {
		return amountprops;
	}
}
