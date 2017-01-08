package net.binggl.mydms.features.documents;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import net.binggl.mydms.features.documents.viewmodels.DocumentViewModel;


public class DocumentViewModelResultTransformer implements org.hibernate.transform.ResultTransformer {

 	private static final long serialVersionUID = 1L;

	@Override
    public Object transformTuple(Object[] objects, String[] strings) {

        DocumentViewModel result = new DocumentViewModel();
        for (int i = 0; i < objects.length; i++) {
            setField(result, strings[i], objects[i]);
        }
        return result;
    }

    private void setField(DocumentViewModel result, String string, Object object) {
        if (string.equalsIgnoreCase("id")) {
            result.setId((String) object);
        } else if (string.equalsIgnoreCase("title")) {
            result.setTitle((String) object);
        } else if (string.equalsIgnoreCase("fileName")) {
            result.setFileName((String) object);
        } else if (string.equalsIgnoreCase("alternativeId")) {
            result.setAlternativeId((String) object);
        } else if (string.equalsIgnoreCase("previewLink")) {
            result.setPreviewLink((String) object);
        } else if (string.equalsIgnoreCase("amount")) {
        	double amount = 0.0;
        	if(object instanceof Double) {
        		amount = (Double) object;
        	} else if(object instanceof java.math.BigDecimal) {
        		amount = ((java.math.BigDecimal)object).doubleValue();
        	}
            result.setAmount(amount);
        } else if (string.equalsIgnoreCase("created")) {
            result.setCreated((Date) object);
        } else if (string.equalsIgnoreCase("modified")) {
            result.setModified((Date) object);
        } else if (string.equalsIgnoreCase("taglist")) {
            String list = (String)object;
            if(StringUtils.isNotEmpty(list)) {
	            List<String> tags = Arrays.asList(list.split(";"));
	            result.setTags(tags);
            }
        } else if (string.equalsIgnoreCase("senderlist")) {
            String list = (String)object;
            if(StringUtils.isNotEmpty(list)) {
	            List<String> senders = Arrays.asList(list.split(";"));
	            result.setSenders(senders);
            }
        } else {
            throw new RuntimeException("unknown field");
        }

    }

    @SuppressWarnings("rawtypes")
	@Override
    public List transformList(List list) {
        return list;
    }
}