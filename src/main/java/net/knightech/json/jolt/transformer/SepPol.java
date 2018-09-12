package net.knightech.json.jolt.transformer;

import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.util.StringUtils;

public class SepPol extends SimpleRecordSeparatorPolicy{

  public static final String EOF_CHARACTER = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";

  @Override
  public boolean isEndOfRecord(String line) {
    return line.endsWith(EOF_CHARACTER);
  }

  @Override
  public String postProcess(String record) {
    return StringUtils.trimTrailingCharacter(record, '~');
  }
  
}
