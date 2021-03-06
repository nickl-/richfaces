package org.richfaces.helloworld.domain.util.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TLDParser {

	protected String component;
	protected JarEntry tld;
	protected JarFile richfacesUI;
	protected AttributesList allAttributes;	

	public TLDParser(String str) {
		this.component = str;
		allAttributes = new AttributesList();
	}

	public AttributesList getAllAttributes() {
		allAttributes.clear();
		try {
			
			Reader isr = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResource("META-INF/richfaces.tld").openStream());
			BufferedReader reader = new BufferedReader(isr);
			String line, attr;
			Attribute attribute = new Attribute();
			int position, end;
			boolean insideTag = true;
			StringBuilder sb = new StringBuilder("");
			while (((line = reader.readLine()) != null) && insideTag) {
				if ((position = line.indexOf("<name>")) != -1) {
					end = line.indexOf("</name>");
					attr = line.substring(position + 6, end).trim();
					if (attr.equalsIgnoreCase(component)) {
						while (!(line = reader.readLine()).contains("</tag>")) {
							if (line.contains("<attribute>")) {
								do {
									// find attribute name
									if ((position = line.indexOf("<name>")) != -1) {
										end = line.indexOf("</name>");
										attribute.setName(line.substring(
												position + 6, end).trim());
									}
									// find attribute description
									if ((position = line.indexOf("<description>")) != -1) {
										
										if((end = line.indexOf("</description>")) != -1){											
											attribute.setDescription(line.substring(position + 13, line.length()-14));
										}else{
											sb.append(line.substring(position + 13, line.length()).trim().replaceAll("\t", ""));											
											line = reader.readLine();
											while ((end = line.indexOf("</description>")) == -1) {
												sb.append(line.substring(0, line.length()).replaceAll("\t", ""));													
												line = reader.readLine();
											}
											sb.append(line.substring(0, line.length() - 14).trim().replaceAll("\t", ""));											
											
											attribute.setDescription(sb.toString());
											sb.delete(0, sb.length());
										}										
									}									
									// find attribute type
									if ((position = line.indexOf("<type>")) != -1) {
										end = line.indexOf("</type>");
											attribute.setType(line.substring(position + 6, end).trim());
									}
									// find attribute method-signature
									if ((position = line.indexOf("<method-signature>")) != -1) {
										end = line.indexOf("</method-signature>");
											attribute.setType(line.substring(position + 18, end).trim());
									}
								} while (!((line = reader.readLine())
										.contains("</attribute>")));
								// define attribute status
								attribute.setStatus(Status.NOT_READY);
								allAttributes.add(attribute);
								attribute = new Attribute();
							}
						}
						insideTag = false;
					}
				}
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allAttributes;
	}

	public String getComponent() {
		return component;
	}

	public void setComponent(String component) {
		this.component = component;
	}
}
