package com.comerzzia.pos.core.services.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import com.comerzzia.omnichannel.barcode.decoders.model.BarcodeDecoded;
import com.comerzzia.omnichannel.barcode.decoders.model.BarcodeDecoderFieldModel;
import com.comerzzia.omnichannel.barcode.decoders.model.BarcodeDecoderModel;
import com.comerzzia.omnichannel.barcode.decoders.model.BarcodeDecoderPrefixModel;
import com.comerzzia.omnichannel.barcode.decoders.service.BarcodeDecoderManager;
import com.comerzzia.omnichannel.barcode.decoders.service.BarcodeDecodersConfiguration;
import com.comerzzia.omnichannel.facade.model.catalog.ItemSpecialBarcodeConfig;
import com.comerzzia.omnichannel.facade.service.catalog.ItemSpecialBarcodeServiceFacade;

@Configuration
@Import(BarcodeDecodersConfiguration.class)
public class POSBarcodeDecodersConfiguration {
		
	@Bean
	@Lazy
	@ConditionalOnMissingBean
	public BarcodeDecoderManager getBarcodeDecoderManager(ItemSpecialBarcodeServiceFacade barcodeService) {		
		return new BarcodeDecoderManager(getConfigurationFromDatabase(barcodeService));
	}
	
	/*
	 * Create barcode provider model from database table
	 */
	protected List<BarcodeDecoderModel> getConfigurationFromDatabase(ItemSpecialBarcodeServiceFacade barcodeService) {
		List<BarcodeDecoderModel> result = new ArrayList<>();
		
		// only file configuration
		if (barcodeService == null) return result;

		BarcodeDecoderModel model = new BarcodeDecoderModel();
		List<BarcodeDecoderPrefixModel> prefixs = new ArrayList<>();
		model.setPrefixs(prefixs);
		
		model.setProvider("barcodeDefaultDecoder");
		model.setMinLength(255);

		for (ItemSpecialBarcodeConfig codBarrasEspecial : barcodeService.findAll()) {
			// create model
			BarcodeDecoderPrefixModel prefix = new BarcodeDecoderPrefixModel();
			prefixs.add(prefix);
			List<BarcodeDecoderFieldModel> fields = new ArrayList<>();
			prefix.setFields(fields);
			
			prefix.setDescription(codBarrasEspecial.getDescription());
			prefix.setPrefix(codBarrasEspecial.getPrefix());
			
			// create fields
			int minPrefixLength = 0;
			
			if (codBarrasEspecial.getLoyalty()) {
				minPrefixLength = prefix.getPrefix().length()+1;
				
				BarcodeDecoderFieldModel field = new BarcodeDecoderFieldModel();
				field.setFieldName(BarcodeDecoded.LOYALTY);
				field.setInit(1);
				field.setLength(50);
				fields.add(field);
			} else {			
				Map<String, String> fieldList = new HashMap<>();
				fieldList.put(BarcodeDecoded.ITEM_CODE, codBarrasEspecial.getItemCode());
				fieldList.put(BarcodeDecoded.WEIGHT, codBarrasEspecial.getQuantity());
				fieldList.put(BarcodeDecoded.PRICE, codBarrasEspecial.getPrice());
				fieldList.put(BarcodeDecoded.DOCUMENT_CODE, codBarrasEspecial.getDocumentCode());
				
				
				for (Entry<String, String> entry : fieldList.entrySet() ) {
					BarcodeDecoderFieldModel field = splitField(entry.getKey(), entry.getValue());
					
					if (field != null) {
						if (minPrefixLength < (field.getInit() + field.getLength() - 1)) {
							minPrefixLength = field.getInit() + field.getLength() -1;
						}					
						
						fields.add(field);
					}
				}
			}
			
			// update model minimum length from minimum prefix length
			if (model.getMinLength() > minPrefixLength) {
				model.setMinLength(minPrefixLength);
			}
		}
		
		if (prefixs.size() > 0) {
			result.add(model);
		}

		return result;
	}

	protected BarcodeDecoderFieldModel splitField(String fieldName, String expression) {
		if (StringUtils.isBlank(expression)) return null;
		
		String[] splitValues = expression.split("\\|");

		if (splitValues.length < 2) return null;
		
		BarcodeDecoderFieldModel field = new BarcodeDecoderFieldModel();
		field.setFieldName(fieldName);
		field.setInit(Integer.valueOf(splitValues[0]));
		field.setLength(Integer.valueOf(splitValues[1]));
		if (splitValues.length == 3) {
			field.setDecimalPlaces(Integer.valueOf(splitValues[2]));
			field.setLength(field.getLength() + field.getDecimalPlaces());
		}
		
		// empty or disabled configuration
		if (field.getInit() == 0 && field.getLength() == 0) return null;
		
		return field;		
	}
}
