package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.client.gui;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.privacytrust.privacyprotection.api.dataobfuscation.ObfuscationLevels;

public class test {

	public static void main(String[] args) throws ParseException{
		for (int i=0; i<ObfuscationLevels.getApplicableObfuscationLevels(CtxAttributeTypes.LOCATION_SYMBOLIC); i++){
		System.out.println("Level: "+i+" =>"+(getObfuscatedCtxAttribute(i, CtxAttributeTypes.LOCATION_SYMBOLIC, "EM1.69")).getStringValue());
		}
		
		for (int i=0; i<ObfuscationLevels.getApplicableObfuscationLevels(CtxAttributeTypes.NAME); i++){
			System.out.println("Level: "+i+" =>"+(getObfuscatedCtxAttribute(i, CtxAttributeTypes.NAME, "Eliza Papadopoulou")).getStringValue());
		}
			
		String strDate = "01-03-1982";
		for (int i=0; i<ObfuscationLevels.getApplicableObfuscationLevels(CtxAttributeTypes.BIRTHDAY); i++){
			System.out.println("Level: "+i+" =>"+(getObfuscatedCtxAttribute(i, CtxAttributeTypes.BIRTHDAY, strDate)).getStringValue());
		}
		
	}
	
	public static CtxAttribute getObfuscatedCtxAttribute(Integer obfuscationLevel, String type, String value){
		CtxEntityIdentifier eId = new  CtxEntityIdentifier("bla", CtxEntityTypes.PERSON, new Long(1));
		CtxAttributeIdentifier id = new CtxAttributeIdentifier(eId, type, new Long(2));
		CtxAttribute ctxAttribute = new CtxAttribute(id);
		ctxAttribute.setStringValue(value);
		return (CtxAttribute) obfuscate(obfuscationLevel, ctxAttribute);
	}
	private static CtxModelObject obfuscate(Integer obfuscationLevel,
			CtxAttribute ctxAttribute) {
		String stringValue = ctxAttribute.getStringValue();
		if (stringValue==null || stringValue.isEmpty()){
			return ctxAttribute;
		}
		if (ctxAttribute.getType().equalsIgnoreCase(CtxAttributeTypes.LOCATION_SYMBOLIC)){

			Integer levels = ObfuscationLevels.getApplicableObfuscationLevels(CtxAttributeTypes.LOCATION_SYMBOLIC);
			if (obfuscationLevel==levels){
				ctxAttribute.setStringValue("Earth");
				return ctxAttribute;
			}
			if (obfuscationLevel==(levels-1)){
				ctxAttribute.setStringValue("Europe");
				return ctxAttribute;
			}
			String[] split = stringValue.split(",");
			StringBuilder sb = new StringBuilder();
			if (split.length==7){
				for (int i = obfuscationLevel; i<split.length; i++){
					sb.append(split[i].trim());
					if (i<(split.length-1)){
						sb.append(",");
					}
				}
			}else if (split.length==1){
				String defaultLocation = "MACS, Riccarton, EH14 4AS, Edinburgh, Scotland, UK";
				ctxAttribute.setStringValue(stringValue+", "+defaultLocation);
				return obfuscate(obfuscationLevel, ctxAttribute);
			}
			ctxAttribute.setStringValue(sb.toString());

		}else if (ctxAttribute.getType().equalsIgnoreCase(CtxAttributeTypes.NAME)){
			if (obfuscationLevel==0){
				return ctxAttribute;
			}
			String[] split = stringValue.trim().split(" ");
			if (split.length>1){
				StringBuilder sb = new StringBuilder();
				switch (obfuscationLevel){
				case 1: 
					sb = new StringBuilder();
					for (int i = 0; i <split.length-1; i++){
						sb.append(split[i].charAt(0));
						sb.append(". ");
					}
					sb.append(split[split.length-1]);
					ctxAttribute.setStringValue(sb.toString().trim());
					break;
				case 2: 
					sb = new StringBuilder();
					sb.append(split[0]);
					for (int i = 1; i< split.length; i++){
						sb.append(" ");
						sb.append(split[i].charAt(0));
						sb.append(".");
					}
					ctxAttribute.setStringValue(sb.toString().trim());
					break;
				case 3:
					sb = new StringBuilder();

					for (int i = 0; i< split.length; i++){
						sb.append(split[i].charAt(0));
						sb.append(". ");
					}
					ctxAttribute.setStringValue(sb.toString().trim());
					break;
				case 4:
					ctxAttribute.setStringValue("user");
					break;
				}
			}
		}else if (ctxAttribute.getType().equalsIgnoreCase(CtxAttributeTypes.BIRTHDAY)){
			if (obfuscationLevel==0){
				return ctxAttribute;
			}

			String delim = " ";
			if (stringValue.contains("/")){
				delim = "/";
			}
			else if (stringValue.contains("-")){
				delim = "-";
			}
			System.out.println("Delim is: "+delim);


			try {
				SimpleDateFormat sdf=new SimpleDateFormat("dd"+delim+"MM"+delim+"yyyy");	
				Date parsedDate = sdf.parse(stringValue);
				System.out.println("parsed date "+parsedDate.toString()+" obf: "+obfuscationLevel );
				switch (obfuscationLevel){
				case 1:
					System.out.println("Case 1");
					SimpleDateFormat sdfCase1 = new SimpleDateFormat("MMM yyyy");
					System.out.println(sdfCase1.format(parsedDate));
					ctxAttribute.setStringValue(sdfCase1.format(parsedDate));
					break;
				case 2:
					System.out.println("Case 2");
					SimpleDateFormat sdfCase2 = new SimpleDateFormat("yyyy");
					ctxAttribute.setStringValue(sdfCase2.format(parsedDate));
					break;
				case 3:
					System.out.println("Case 3");
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.YEAR, -18);
					
					Calendar birthday = Calendar.getInstance();
					birthday.setTime(parsedDate);
					
					SimpleDateFormat sdfCase3 = new SimpleDateFormat("yyyy");
					if (birthday.after(cal)){
						
						ctxAttribute.setStringValue("After "+sdfCase3.format(cal.getTime()));
					}else{
						
						ctxAttribute.setStringValue("Before "+sdfCase3.format(cal.getTime()));
					}
					
					
				}
			} catch (ParseException e) {

				System.out.println("Could not parse date from string "+stringValue);
				e.printStackTrace();
				return ctxAttribute;
			}
		}else if (ctxAttribute.getType().equalsIgnoreCase(CtxAttributeTypes.EMAIL)){
			if (obfuscationLevel==1){
				ctxAttribute.setStringValue("anonymous230489324");

			}
		}
		return ctxAttribute;

	}
}
