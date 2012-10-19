package org.vidge.inface;

/**
 * Interface for building forms may be extended by : <br>
 * <b> IFormInputChangeListener </b>- in case if you want to know where input object changed <br>
 * <b> IFormDataProvider</b> - ij case if you want to retrieve some data by example from database table<br>
 * <b>IFormFactory</b> - in case if you want to control the creation of child entities
 * 
 * @author user
 * 
 * @param <O>
 */
public interface IForm<O> {

	void setInput(O input);

	O getInput();
}
