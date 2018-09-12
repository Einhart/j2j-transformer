package net.knightech.json.jolt.transformer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import com.bazaarvoice.jolt.ContextualTransform;

public class LookupTransform implements ContextualTransform {

  public static final String CONTEXT_KEY = "activityCodes";
  public static final String CRTE_TMS = "crteTms";
  public static final String LSTUPD_TMS = "lstupdTms";

  @Inject
  public LookupTransform( ) {
    
  }

  @Override
  public Object transform( Object input, Map<String, Object> context ) {

    Map<String, Map> contextValue = (Map<String, Map>)  context.get( CONTEXT_KEY );

    final LinkedHashMap hydratedTransformedJson = (LinkedHashMap) input;
    
    LinkedHashMap recordControl =  (LinkedHashMap) hydratedTransformedJson.get("recordControl");

    final String createdTimeStampAsStringEST= (String) recordControl.get(CRTE_TMS);
    final String lastUpdatedTimeStampAsStringEST = (String) recordControl.get(LSTUPD_TMS);

    recordControl.put(CRTE_TMS, convertToZonedTimestamp(createdTimeStampAsStringEST));
    recordControl.put(LSTUPD_TMS, convertToZonedTimestamp(lastUpdatedTimeStampAsStringEST));
    
    List<LinkedHashMap> products =  (List<LinkedHashMap>) hydratedTransformedJson.get("products");
    
    if(products!=null){
      products.forEach(linkedHashMap -> {

        linkedHashMap.put( "productName", contextValue.get(linkedHashMap.get("productRef")) );

      });
    }

    return input;
  }
 
  /**
   * Apply format map to the string value received -> 2012-03-31-09.26.32.688568
   * @param timestampToConvert
   * @return 
   */
  private String convertToZonedTimestamp(String timestampToConvert) {
    
    if (timestampToConvert.length() == 26) {
      timestampToConvert = timestampToConvert.substring(0, 23);
    }
    
    final int i = timestampToConvert.lastIndexOf("-");
    final String t = replaceCharAt(timestampToConvert, i, 'T');
    return t+"-05:00";
  }

  private String replaceCharAt(String s, int pos, char c) {
    return s.substring(0, pos) + c + s.substring(pos + 1);
  }
  
  
}

