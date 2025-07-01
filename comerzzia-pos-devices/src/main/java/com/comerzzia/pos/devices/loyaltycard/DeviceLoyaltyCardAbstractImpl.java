package com.comerzzia.pos.devices.loyaltycard;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.modelmapper.ModelMapper;

import com.comerzzia.api.loyalty.client.CouponsApiClient;
import com.comerzzia.api.loyalty.client.LyCustomersApiClient;
import com.comerzzia.api.loyalty.client.model.Collective;
import com.comerzzia.api.loyalty.client.model.Coupon;
import com.comerzzia.api.loyalty.client.model.LyCustomerCardDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerDetail;
import com.comerzzia.api.loyalty.client.model.LyCustomerTag;
import com.comerzzia.core.facade.service.country.CountryServiceFacade;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketBalanceCard;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomer;
import com.comerzzia.omnichannel.facade.model.basket.header.BasketLoyalCustomerCoupon;
import com.comerzzia.pos.core.devices.configuration.DeviceConfiguration;
import com.comerzzia.pos.core.devices.device.DeviceAbstractImpl;
import com.comerzzia.pos.core.devices.device.DeviceCallback;
import com.comerzzia.pos.core.devices.device.loyaltycard.DeviceLoyaltyCard;
import com.comerzzia.pos.core.devices.device.loyaltycard.DeviceLoyaltyCardException;
import com.comerzzia.pos.core.gui.BackgroundTask;
import com.comerzzia.pos.util.config.SpringContext;
import com.comerzzia.pos.util.i18n.I18N;
import com.comerzzia.pos.util.xml.XMLDocumentNode;
import com.comerzzia.pos.util.xml.XMLDocumentNodeNotFoundException;

public abstract class DeviceLoyaltyCardAbstractImpl extends DeviceAbstractImpl implements DeviceLoyaltyCard {

	private static final Logger log = Logger.getLogger(DeviceLoyaltyCardAbstractImpl.class.getName());

	public static final String PARAMETRO_DOCUMENTO = "Ticket";

	// Apikey
	protected String wsApiKey;

	// Artículos
	protected List<String> prefijosTarj;

	protected FidelizacionTask currentTask;

	@Override
	protected void loadConfiguration(DeviceConfiguration config) {
		prefijosTarj = new ArrayList<>();
		try {
			XMLDocumentNode configNode = config.getModelConfiguration().getConnectionConfig()
					.getNode(DeviceLoyaltyCard.TAG_CONFIGURATION);
			XMLDocumentNode prefijosXML = configNode.getNode(DeviceLoyaltyCard.TAG_CONFIG_PREFIXES);
			List<XMLDocumentNode> prefijos = prefijosXML.getChildren(DeviceLoyaltyCard.TAG_CONFIG_PREFIX);

			for (XMLDocumentNode prefijo : prefijos) {
				prefijosTarj.add(prefijo.getValue());
			}
		} catch (XMLDocumentNodeNotFoundException ex) {
			log.error("Error recuperando la información de configuración de fidelización.", ex);
		}
	}

	@Override
	public boolean isLoyaltyCardPrefix(String numeroTarjeta) {
		if (StringUtils.isBlank(numeroTarjeta)) return false;

		for (String prefijo : prefijosTarj) {
			if (numeroTarjeta.startsWith(prefijo)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
    public BasketLoyalCustomer findLoyalCustomerAndCoupons(String cardCode) throws DeviceLoyaltyCardException {
	   return internalFindLoyalCustomerData(cardCode, true);
	}
	
	@Override
	public BasketLoyalCustomer findLoyalCustomer(String cardCode) throws DeviceLoyaltyCardException {
		return internalFindLoyalCustomerData(cardCode, false);
	}
	
	
    public BasketLoyalCustomer internalFindLoyalCustomerData(String cardCode, Boolean loadCoupons) throws DeviceLoyaltyCardException {
		BasketLoyalCustomer basketLoyalCustomer;
		
    	try {
			LyCustomersApiClient customersApiClient = SpringContext.getBean(LyCustomersApiClient.class);
	        LyCustomerDetail customer = customersApiClient.findLyCustomerByCard(cardCode).getBody();
	        
	        basketLoyalCustomer = new BasketLoyalCustomer();
	        
	        clientModelToBasketModel(customer, basketLoyalCustomer, cardCode);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DeviceLoyaltyCardException(e.getMessage(), e);
		}
    	
    	if (loadCoupons) {	        
		    try {
		    	setLoyalCustomerCoupons(basketLoyalCustomer);
		    }catch(Exception e) {
		    	log.error(e.getMessage(), e);
		    	
		    	throw new DeviceLoyaltyCardException(I18N.getText("El fidelizado se ha consultado correctamente, pero ha ocurrido un error cargando sus cupones disponibles. Consulte con el administrador."));
		    	
		    }
    	}
    
    	return basketLoyalCustomer;
    }
    
	protected void clientModelToBasketModel(LyCustomerDetail restModel, BasketLoyalCustomer basketLoyalCustomer, String cardNumber) {
		basketLoyalCustomer.setLyCustomerId(restModel.getLyCustomerId());
		basketLoyalCustomer.setName(restModel.getName());
		basketLoyalCustomer.setLastName(restModel.getLastName());
		basketLoyalCustomer.setIdentificationTypeCode(restModel.getIdentificationTypeCode());
		basketLoyalCustomer.setPostalCode(restModel.getPostalCode());
		basketLoyalCustomer.setVatNumber(restModel.getVatNumber());
		basketLoyalCustomer.setAddress(restModel.getAddress());
		basketLoyalCustomer.setLocation(restModel.getLocation());
		basketLoyalCustomer.setCity(restModel.getCity());
		basketLoyalCustomer.setProvince(restModel.getProvince());
		if(StringUtils.isNotBlank(restModel.getCountryCode())){
			basketLoyalCustomer.setCountryCode(restModel.getCountryCode());
			basketLoyalCustomer.setCountryDes(SpringContext.getBean(CountryServiceFacade.class).findById(restModel.getCountryCode()).getCountryDes());
		}
		basketLoyalCustomer.setPaperLess(restModel.getPaperLess());
		
		LyCustomerCardDetail card = restModel.getCards().stream().filter(c -> c.getCardNumber().equals(cardNumber)).findFirst().get();
        basketLoyalCustomer.setCardNumber(card.getCardNumber());
        basketLoyalCustomer.setCardActive(card.getDeactivationDate() == null || card.getDeactivationDate().after(new Date()));
        basketLoyalCustomer.setCardTypeCode(card.getCardType().getCardTypeCode());
        
        if (card.getAccount() != null) {
        	if(card.getAccount().getBalance() != null){
            	basketLoyalCustomer.setCardBalance(card.getAccount().getBalance());
            }
            if(card.getAccount().getProvisionalBalance() != null){
            	basketLoyalCustomer.setCardProvisionalBalance(card.getAccount().getProvisionalBalance());
            }
        }
		
		Set<String> codColectivos = new HashSet<>();
        if(restModel.getCollectives() != null) {
	        for (Collective collective : restModel.getCollectives()) {
	        	codColectivos.add(collective.getCollectiveCode());
			}
        }
        basketLoyalCustomer.setCollectives(codColectivos);
        
        Set<String> uidEtiquetas = new HashSet<>();
        if(restModel.getTags() != null){
        	for(LyCustomerTag tag : restModel.getTags()){
        		uidEtiquetas.add(tag.getTagUid());
        	}
        }
        basketLoyalCustomer.setTags(uidEtiquetas);
        
        // add customer cards
        List<BasketBalanceCard> balanceCards = new ArrayList<>();
        
        for (LyCustomerCardDetail cardDetail : restModel.getCards()) {        	
        	BasketBalanceCard balanceCard = new BasketBalanceCard();
        	balanceCard.setCardCode(cardDetail.getCardNumber());
        	balanceCard.setCardTypeCode(cardDetail.getCardType().getCardTypeCode());
        	balanceCard.setIsMoneyCard(cardDetail.getCardType().getPaymentsAllowed());
        	
        	balanceCards.add(balanceCard);
        }
        
        basketLoyalCustomer.setBalanceCards(balanceCards);
	}

	@Override
	public void connect() {

	}

	@Override
	public void disconnect() {

	}

	@Override
	public void setApikey(String wsApiKey) {
		this.wsApiKey = wsApiKey;
	}
	
	@Override
	public void findLoyalCustomerAndCouponsInBackground(String numTarjeta, DeviceCallback<BasketLoyalCustomer> callback) {
		currentTask = new FidelizacionTask(numTarjeta, true, callback);
		currentTask.start();
	}

	@Override
	public void findLoyalCustomerInBackground(String numTarjeta, DeviceCallback<BasketLoyalCustomer> callback) {
		currentTask = new FidelizacionTask(numTarjeta, false, callback);
		currentTask.start();
	}


	public class FidelizacionTask extends BackgroundTask<BasketLoyalCustomer> {

		private final String numTarjeta;
		private final boolean loadCoupons;
		private DeviceCallback<BasketLoyalCustomer> callback;
		

		public FidelizacionTask(String numTarjeta, boolean loadCoupons, DeviceCallback<BasketLoyalCustomer> callback) {
			super(false);
			this.numTarjeta = numTarjeta;
			this.callback = callback;
			this.loadCoupons = loadCoupons;
		}

		@Override
		protected BasketLoyalCustomer execute() throws Exception {
			BasketLoyalCustomer fidelizado = internalFindLoyalCustomerData(numTarjeta, loadCoupons);

			if (isCancelled() || Thread.interrupted()) {
				return null;
			}

			return fidelizado;
		}

		@Override
		protected void succeeded() {
			super.succeeded();
			BasketLoyalCustomer tarjeta = getValue();
			if (tarjeta != null) {
				callback.onSuccess(tarjeta);
			}
		}

		@Override
		protected void failed() {
			super.failed();
			
			if (isCancelled() || Thread.interrupted()) {
				return;
			}

			Throwable e = getException();

			log.error("Ha ocurrido un error en la petición rest: " + e.getMessage(), e);
			
			// Si no se cumple la condición anterior, damos la llamada por correcta
			BasketLoyalCustomer fidelizacionBean = new BasketLoyalCustomer();
			fidelizacionBean.setCardNumber(numTarjeta);
			fidelizacionBean.setCardActive(true);
			callback.onSuccess(fidelizacionBean);
		}

		public void ignorar() {cancel(true);
		}
	}

	@Override
	public void ignoreBackgroundTaskResult() {
		if (currentTask != null) {
			currentTask.ignorar();
		}
	}

	@Override
	public void setLoyalCustomerCoupons(BasketLoyalCustomer basketLyCustomer) throws Exception {
		log.debug("getCustomerCoupons() - Querying coupons for customer " + basketLyCustomer.getLyCustomerId());

		ModelMapper modelMapper = SpringContext.getBean(ModelMapper.class);
		
		CouponsApiClient couponsApiClient = SpringContext.getBean(CouponsApiClient.class);
		
		List<Coupon> customerCoupons = couponsApiClient.findCouponsByLyCustomer(basketLyCustomer.getLyCustomerId(), true, false, true, false, false, null, null).getBody();
		
		List<BasketLoyalCustomerCoupon> availableCoupons = new ArrayList<>();
		
		for(Coupon coupon : customerCoupons) {
			BasketLoyalCustomerCoupon loyalCustomerCoupon = modelMapper.map(coupon, BasketLoyalCustomerCoupon.class);
			loyalCustomerCoupon.setValidationRequired(false);
			availableCoupons.add(loyalCustomerCoupon);
		}

		basketLyCustomer.setAvailableCoupons(availableCoupons);
		basketLyCustomer.setActiveCoupons(new ArrayList<>());
	}

}
