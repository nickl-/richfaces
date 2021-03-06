package org.richfaces.testng;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.ajax4jsf.template.Template;
import org.richfaces.AutoTester;
import org.richfaces.SeleniumTestBase;
import org.testng.Assert;
import org.testng.annotations.Test;

public class DataGridTest extends SeleniumTestBase {
	
	private String elements;

	private String rendered;

	private String dataGrid;

	private String reRender;

	private String outputText;
	
	private String dataTable;

	private void init(Template template) {
        renderPage(null, template, "#{dataGrid.init}");
        String attrForm = getParentId() + "attrForm";
        elements = attrForm + ":elements";
        rendered = attrForm + ":rendered";
        String mainForm = getParentId() + "mainForm";
        dataGrid = mainForm + ":dataGrid";
        reRender = mainForm + ":reRender";
        outputText = getParentId() + "outputText";
        dataTable = getParentId() + "dataTable";
	}

	/**
	 *  items from collection defined as value attribute are output to the client;
	 *  number of items is limited using elements attribute and not
	 */
	@Test
	public void testStructure(Template template) {
	    init(template);
		chekStructure();
		selenium.type(elements, "3");
		waitForAjaxCompletion();
		Assert.assertEquals(selenium.getXpathCount("id('"+ dataGrid + "')/tbody/tr"), 3);
		chekStructure();
	}
	
    /**
     *  nested input and command components work correctly
     *  for the cases of validation failure or not
     */
	@Test
	public void testComponentsProcessingWithValidation(Template template) {
        init(template);
		String trLocator = "xpath=id('"+ dataGrid + "')/tbody/tr[";
		String inputLocator = trLocator + "6]/td/input[";
		Assert.assertEquals(selenium.getText(outputText), "");
		selenium.click(inputLocator + "2]");
		waitForPageToLoad();
		Assert.assertTrue(selenium.getText(outputText).endsWith("5:submit"));
		selenium.click(inputLocator + "3]");
        waitForAjaxCompletion();
		Assert.assertTrue(selenium.getText(outputText).endsWith("5:ajaxSubmit"));
		selenium.click(inputLocator + "4]");
        waitForAjaxCompletion();
		Assert.assertTrue(selenium.getText(outputText).endsWith("5:ajaxSingleSubmit"));
		selenium.type(inputLocator + "1]", "abc5");
		selenium.click(inputLocator + "3]");
        waitForAjaxCompletion();
		chekStructure();        
		selenium.type(inputLocator + "1]", "fail");
		selenium.type(trLocator + "3]/td/input[1]", "fail");
		selenium.click(inputLocator + "3]");
        waitForAjaxCompletion();
		chekStructure(3, 6);        
	}

    /**
     *  rows defined by ajaxKeys attribute are re-rendered correctly
     */
	@Test
	public void testAjaxKeys(Template template) {
        init(template);
		chekStructure();
		clickAjaxCommandAndWait(reRender);
		chekStructure(3, 7);        
	}

    /**
     *  components using rowKeyVar and stateVar variables are correctly output to the client
     */
	@Test
	public void testVars(Template template) {
        init(template);
		Assert.assertEquals(selenium.getText(dataGrid + ":c_2:first"), "0", "Attribute 'stateVar' works wrong.");
		Assert.assertEquals(selenium.getText(dataGrid + ":c_2:rows"), "0", "Attribute 'stateVar' works wrong.");
		int rows = selenium.getXpathCount("id('"+ dataGrid + "')/tbody/tr").intValue();		
		for (int i = 0; i < rows; i++) {
			Assert.assertEquals(selenium.getText(dataGrid + ":c_" + i + ":rowKeyVar"), Integer.toString(i));			
		}
	}

    /**
     *  componentState attribute stores nested components state across requests
     */
	@Test
	public void testComponentState(Template template) {
        init(template);
		Assert.assertEquals(selenium.getText(dataGrid + ":c_2:firstState"), "0", "Attribute 'componentState' works wrong.");
		Assert.assertEquals(selenium.getText(dataGrid + ":c_2:rowsState"), "0", "Attribute 'componentState' works wrong.");
	}

    /**
     *  component with rendered = false is not present on the page
     */
	@Test
	public void testRendered(Template template) {
        init(template);
		Assert.assertTrue(selenium.isElementPresent(dataGrid));
		clickAjaxCommandAndWait(rendered);
		Assert.assertFalse(selenium.isElementPresent(dataGrid));
	}

    /**
     *  style and classes, standard HTML attributes are output to client
     */
	@Test
	public void testStandardAttributes(Template template) {
    	AutoTester autoTester = getAutoTester(this);
    	autoTester.renderPage(template, "#{dataGrid.init}");
    	Map<String, String> styleAttributes = new HashMap<String, String>();
    	styleAttributes.put("width", "100%");
    	styleAttributes.put("color", "yellow");
    	autoTester.testStyleAndClasses(new String[]{"noname"}, styleAttributes);
    	autoTester.testHTMLEvents();
	}

    /**
     *  rowKeyConverter outputs correct client ids
     */
	@Test
	public void testRowKeyConverter(Template template) {
        init(template);
		Assert.assertTrue(selenium.getAttribute("xpath=id('"+ dataGrid + "')/tbody/tr[3]/td@id").endsWith(":c_2"), "Attribute 'rowKeyConverter' works wrong.");
	}

	private void chekStructure(int ... notEqualRows) {
		String dataTableRowLocator = "id('"+ dataTable + "')/tbody/tr";
		int count = selenium.getXpathCount(dataTableRowLocator).intValue();
		for (int i = 1; i <= count; i++) {
			if (Arrays.binarySearch(notEqualRows, i) < 0) {
				Assert.assertEquals(selenium.getValue("xpath=id('"+ dataGrid + "')/tbody/tr[" + i + "]/td/input"),
						selenium.getText("xpath=" + dataTableRowLocator + "[" + i + "]"));
			} else {
				Assert.assertFalse(selenium.
						getValue("xpath=id('"+ dataGrid + "')/tbody/tr[" + i + "]/td/input").
						equals(selenium.getText("xpath=" + dataTableRowLocator + "[" + i + "]")));
			}
		}
	}
	
	@Override
	public String getTestUrl() {
		return "pages/dataGrid/dataGrid.xhtml";
	}
	
	@Override
	public String getAutoTestUrl() {
		return "pages/dataGrid/dataGridAutoTest.xhtml";
	}
}
