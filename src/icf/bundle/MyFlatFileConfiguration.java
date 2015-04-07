//Created by Zheang
package icf.bundle;

import java.io.File;

import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class MyFlatFileConfiguration extends AbstractConfiguration {
	private String fileName = "";
	private String delimiter = ",";

	@Override
	public void validate() {
		System.out.println("Validate");
		File f = new File(fileName);
		if(!f.isFile())
			throw new ConnectorException("File not exist");
	}

	@ConfigurationProperty(order = 1, helpMessageKey = "USER_ACCOUNT_STORE_HELP", displayMessageKey = "USER_ACCOUNT_STORE_DISPLAY")
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@ConfigurationProperty(order = 2, helpMessageKey = "USER_STORE_TEXT_DELIM_HELP", displayMessageKey = "USER_STORE_TEXT_DELIM_DISPLAY")
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getFileName() {
		return this.fileName;
	}

	public String getDelimiter() {
		return this.delimiter;
	}

}
