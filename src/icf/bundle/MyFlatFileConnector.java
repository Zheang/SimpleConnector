//Created by Zheang
package icf.bundle;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.AttributeInfo;
import org.identityconnectors.framework.common.objects.AttributeInfoBuilder;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ObjectClass;
import org.identityconnectors.framework.common.objects.OperationOptions;
import org.identityconnectors.framework.common.objects.ResultsHandler;
import org.identityconnectors.framework.common.objects.Schema;
import org.identityconnectors.framework.common.objects.SchemaBuilder;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.filter.AbstractFilterTranslator;
import org.identityconnectors.framework.common.objects.filter.FilterTranslator;
import org.identityconnectors.framework.spi.Configuration;
import org.identityconnectors.framework.spi.Connector;
import org.identityconnectors.framework.spi.ConnectorClass;
import org.identityconnectors.framework.spi.operations.CreateOp;
import org.identityconnectors.framework.spi.operations.DeleteOp;
import org.identityconnectors.framework.spi.operations.SchemaOp;
import org.identityconnectors.framework.spi.operations.SearchOp;
import org.identityconnectors.framework.spi.operations.UpdateOp;

@ConnectorClass(configurationClass = MyFlatFileConfiguration.class, displayNameKey = "MyFlatFileConnector")
public class MyFlatFileConnector implements Connector, SchemaOp, CreateOp,
		DeleteOp, UpdateOp, SearchOp<Map<String, String>> {

	private MyFlatFileConfiguration config;
	private MyFlatFileHelper helper;

	@Override
	public void dispose() {
		try {
			helper.finish();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public Configuration getConfiguration() {
		return (Configuration) config;
	}

	@Override
	public void init(Configuration arg0) {
		this.config = (MyFlatFileConfiguration) arg0;
		this.helper = new MyFlatFileHelper(config);
	}

	@Override
	public FilterTranslator<Map<String, String>> createFilterTranslator(
			ObjectClass arg0, OperationOptions arg1) {
		return new AbstractFilterTranslator<Map<String, String>>() {
		};
	}

	@Override
	public void executeQuery(ObjectClass arg0, Map<String, String> arg1,
			ResultsHandler arg2, OperationOptions arg3) {
		List<ConnectorObject> rst = helper.search(arg1);
		for (ConnectorObject obj : rst) {
			if (!arg2.handle(obj)) {
				System.out.println("Not able to handle " + obj);
				break;
			}
		}
	}

	@Override
	public Uid create(ObjectClass arg0, Set<Attribute> arg1,
			OperationOptions arg2) {
		return helper.create(arg1);
	}

	@Override
	public Schema schema() {
		SchemaBuilder schemaBld = new SchemaBuilder(getClass());
		// Get the schema attributes
		Set<AttributeInfo> attrInfos = new HashSet<AttributeInfo>();
		List<String> fieldNames = helper.getHeader();
		for (String fieldName : fieldNames) {
			System.out.println(fieldName);
			AttributeInfoBuilder attrBld = new AttributeInfoBuilder();
			attrBld.setName(fieldName);
			attrBld.setCreateable(false);
			attrBld.setUpdateable(false);
			attrInfos.add(attrBld.build());
		}
		schemaBld.defineObjectClass(ObjectClass.ACCOUNT.getDisplayNameKey(),
				attrInfos);
		return schemaBld.build();
	}

	@Override
	public Uid update(ObjectClass arg0, Uid arg1, Set<Attribute> arg2,
			OperationOptions arg3) {
		return helper.update(arg1, arg2);
	}

	@Override
	public void delete(ObjectClass arg0, Uid arg1, OperationOptions arg2) {
		helper.delete(arg1);
	}

	public void display() {
		helper.printAccounts();
	}

}
