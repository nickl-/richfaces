package org.richfaces.demo.datatable;

import java.io.Serializable;

import org.richfaces.datatable.ExpenseReport;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;
	ExpenseReport expReport;

	public ExpenseReport getExpReport() {
		if (expReport == null)
			expReport = new ExpenseReport();
		return expReport;
	}

	public void setExpReport(ExpenseReport expReport) {
		this.expReport = expReport;
	}
}
