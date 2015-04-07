//Created by Zheang

package icf.client;

import java.io.File;
import java.net.URL;
import java.util.Set;

import org.identityconnectors.common.IOUtil;
import org.identityconnectors.framework.api.APIConfiguration;
import org.identityconnectors.framework.api.ConfigurationProperties;
import org.identityconnectors.framework.api.ConnectorFacade;
import org.identityconnectors.framework.api.ConnectorFacadeFactory;
import org.identityconnectors.framework.api.ConnectorInfo;
import org.identityconnectors.framework.api.ConnectorInfoManager;
import org.identityconnectors.framework.api.ConnectorInfoManagerFactory;
import org.identityconnectors.framework.api.ConnectorKey;

import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.ObjectClassInfo;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;

public class MyClient {

	public static void main(String[] args) {

		try {

			System.out.println("Hello");
			File bundleDir = new File("lib");
			URL url = IOUtil.makeURL(bundleDir, "myffc.jar");

			ConnectorInfoManager cInfoManager = ConnectorInfoManagerFactory
					.getInstance().getLocalManager(url);

			ConnectorKey cKey = new ConnectorKey("icf.bundle", "1.0",
					"icf.bundle.MyFlatFileConnector");
			ConnectorInfo cInfo = cInfoManager.findConnectorInfo(cKey);
			APIConfiguration apiConfig = cInfo.createDefaultAPIConfiguration();
			ConfigurationProperties configProps = apiConfig
					.getConfigurationProperties();
			configProps.setPropertyValue("fileName", "accounts.csv");
			configProps.setPropertyValue("delimiter", ",");
			ConnectorFacade connector = ConnectorFacadeFactory.getInstance()
					.newInstance(apiConfig);
			connector.validate();
			// connector.delete(ObjectClass.ACCOUNT, new Uid("u1"), null);

			Schema schema = connector.schema();
			// print out the schema fields
			Set<ObjectClassInfo> ociSet = schema.getObjectClassInfo();
			for (ObjectClassInfo oc : ociSet) {
				Set<AttributeInfo> aiSet = oc.getAttributeInfo();
				for (AttributeInfo ai : aiSet) {
					System.out.println("\t" + ai.getName() + ", "
							+ ai.getType());
				}
			}

			ResultsHandler resultsHandler = new ResultsHandler() {
				public boolean handle(ConnectorObject cobject) {

					String uid = (String) cobject.getAttributeByName("uid")
							.getValue().get(0);
					String firstName = (String) cobject
							.getAttributeByName("first").getValue().get(0);
					String lastName = (String) cobject
							.getAttributeByName("last").getValue().get(0);
					String status = (String) cobject
							.getAttributeByName("status").getValue().get(0);
					System.out.println("Uid: " + uid + "\t" + "First: "
							+ firstName + "\t" + "Last: " + lastName + + '\t'
							+ "status: " + status);
					return true;
				}
			};

			connector.search(ObjectClass.ACCOUNT, null, resultsHandler, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
