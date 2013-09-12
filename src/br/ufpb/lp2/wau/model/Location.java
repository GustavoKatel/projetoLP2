package br.ufpb.lp2.wau.model;

public class Location {

	private double lat;
	private double lon;

	public Location()
	{
		lat = -7.135855;
		lon = -34.842932;
	}
	
	public Location(double lat, double lon)
	{
		this.lat = lat;
		this.lon = lon;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}
	
}
