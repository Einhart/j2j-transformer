package net.knightech.json.jolt.transformer;

import org.springframework.batch.item.file.separator.SimpleRecordSeparatorPolicy;
import org.springframework.util.StringUtils;

/**
 * Copyright (c)2018 DFS Services LLC
 * All rights reserved.
 *
 * @author pknigh2
 */
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
