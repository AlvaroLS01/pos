package com.comerzzia.bimbaylola.pos.test;

import java.net.URL;

import com.comerzzia.bimbaylola.ws.cliente.softek.TxPosRequest;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxPosResponse;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxServerService;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxServerServiceLocator;
import com.comerzzia.bimbaylola.ws.cliente.softek.TxServerServiceSoapBindingStub;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TxServerService locator = new TxServerServiceLocator();
		try{
			TxServerServiceSoapBindingStub stub = new TxServerServiceSoapBindingStub(new URL("https://txportwst.txhubpr.com/txserver/2"), locator);
			
			TxPosRequest request = new TxPosRequest();
			
			TxPosResponse response = stub.requestIVULoto(request);
			System.out.print(response);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
