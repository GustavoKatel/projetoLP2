package br.ufpb.lp2.wau.controller;

import br.ufpb.lp2.wau.model.Device;

public interface DeviceListener {

	public void onNewDevice(final Device d);
	public void onDeviceOut(final Device d);
	
}
