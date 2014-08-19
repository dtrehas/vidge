package org.vidge.details;




public interface IDetailsDelegate <T>{
	
	public void show(T data);
	
	public Details getDetails(T data);

	public String getHeader(T data);
}


