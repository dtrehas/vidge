package org.vidge.form;

/**
 * Interface for building forms may be extended by : <br>
 * <b>IFormInputChangeListener </b>- in case if you want to know where input object changed <br>
 * <b>IFormDataProvider</b> - ij case if you want to retrieve some data by example from database table<br>
 * <b>IFormFactory</b> - in case if you want to control the creation of child entities <br>
 * <b>IFormAbstractFactory</b> - in case if you want to control the creation of child entities but there must be selection of instance depends on context- so it will be shown a dialog of available
 * types<br>
 * <b>IFormObjectWizard</b> - in case if you want to create or edit object in own wizard things <br>
 * <b>IColorForm</b> - in case if you want to color a table row or other widget <br>
 * <b>IFormFactory</b> - in case if you want to control the creation of child entities<br>
 * <b>IFormView</b> - in case if you want use specific toString method depends on context <br>
 * <b>IFormColor</b> - in case if you want use specific Color depends on context <br>
 * <b>IDialogForm</b> - in case if you want use specific Header and description on dialogs of creation or edition depends on context <br>
 * <b>IFormImage</b> - in case if you want use specific Image depends on context<br>
 * <b>@FormContextRule</b> - in case if you want manage automatically created actions in inner lists <br>
 * <br>
 * suffix-prefix templates for names of properties <br>
 * HIERARCHY_DATA_PROVIDER = "HierarchyDataProvider"<br>
 * CHECKED = "Checked"<br>
 * SET = "set"<br>
 * NEW_WIZARD = "NewWizard"<br>
 * VALIDATOR2 = "Validator"<br>
 * VALID_VALUES = "ValidValues"<br>
 * 
 * @author user
 * 
 * @param <O>
 */
public interface IForm<O> {

	void setInput(O input);

	O getInput();
}
