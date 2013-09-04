package br.ufpb.lp2.ccarro.controller;

import br.ufpb.lp2.ccarro.model.Device;

public interface DeviceListener {

	public void onNewDevice(final Device d);
	public void onDeviceOut(final Device d);
	
}
