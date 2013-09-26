package com.postpc.homeseek;

import java.io.Serializable;

import com.google.android.gms.maps.model.LatLng;

public class MapCircle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8625143534339287017L;
	private Double centerlLat;
	private Double centerLng;
	private Double radius;

	public MapCircle(LatLng center, Double radious){
		this.setCenter(center);
		this.setRadius(radious);		
	}

	public MapCircle() {
		this(new LatLng(0,0), 0.0);
	}

	public LatLng getCenter() {
		return new LatLng(centerlLat, centerLng);
	}

	public void setCenter(LatLng center) {
		this.centerlLat = center.latitude;
		this.centerLng = center.longitude;
	}

	public Double getRadius() {
		return radius;
	}

	public void setRadius(Double radius) {
		this.radius = radius;
	}	
}
