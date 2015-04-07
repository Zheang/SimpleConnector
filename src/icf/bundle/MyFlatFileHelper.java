//Created by Zheang
package icf.bundle;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.identityconnectors.framework.common.exceptions.ConnectorException;
import org.identityconnectors.framework.common.objects.Attribute;
import org.identityconnectors.framework.common.objects.Uid;
import org.identityconnectors.framework.common.objects.ConnectorObject;
import org.identityconnectors.framework.common.objects.ConnectorObjectBuilder;


public class MyFlatFileHelper {
	private MyFlatFileConfiguration config;
	private File targetFile;
	private List<String> header;
	private Set<Map<String, String>> accounts;

	public MyFlatFileHelper(MyFlatFileConfiguration config) {
		this.config = config;
		this.targetFile = new File(config.getFileName());
		initHeader();
	}

	public List<String> getHeader() {
		return header;
	}

	public void initHeader() {
		try {
			this.header = new ArrayList<String>();
			BufferedReader reader = new BufferedReader(new FileReader(
					targetFile));
			String headerLine = reader.readLine();
			StringTokenizer sTokenizer = new StringTokenizer(headerLine,
					config.getDelimiter());
			while (sTokenizer.hasMoreTokens()) {
				String token = sTokenizer.nextToken();
				this.header.add(token);
			}
			accounts = new HashSet<Map<String, String>>();
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().equals(""))
					continue;
				Map<String, String> account = parse(line);
				accounts.add(account);
			}
			reader.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public Map<String, String> parse(String line) {

		Map<String, String> account = new HashMap<String, String>();
		StringTokenizer sTokenizer = new StringTokenizer(line,
				config.getDelimiter());
		if (sTokenizer.countTokens() != header.size())
			return null;
		for (String key : header) {
			account.put(key, sTokenizer.nextToken());
			// System.out.println(key+'\t'+attr.get(key));
		}
		return account;
	}

	public void delete(Uid uid) {
		for (Map<String, String> account : accounts) {
			if (uid.getUidValue().equals((account.get("uid")))) {
				accounts.remove(account);
				System.out.println("delete it");
				break;
			}
		}
	}

	public Uid create(Set<Attribute> attrs) {
		String uid = null;
		for (Attribute attr : attrs) {
			if (attr.is("uid")) {
				uid = (String) attr.getValue().get(0);
				System.out.println("Find Uid: " + uid);
				break;
			}
		}
		if (uid == null)
			throw new ConnectorException("Format not match");
		for (Map<String, String> account : accounts) {
			if (account.get("uid").equals(uid)) {
				throw new ConnectorException("Account exist");
			}
		}
		Map<String, String> newAccount = new HashMap<String, String>();
		for (Attribute attr : attrs) {

			String key = attr.getName();
			if (header.contains(key))
				newAccount.put(key, (String) attr.getValue().get(0));
		}
		this.accounts.add(newAccount);
		return new Uid(uid);
	}

	public Uid update(Uid arg1, Set<Attribute> arg2) {
		for (Map<String, String> account : accounts) {
			if (account.get("uid").equals(arg1.getUidValue())) {
				for (Attribute attr : arg2) {
					if (account.containsKey(attr.getName())) {
						account.put(attr.getName(), (String) attr.getValue()
								.get(0));
					}
					return arg1;
				}
			}
		}
		throw new ConnectorException("Account not found");
	}

	public List<ConnectorObject> search(Map<String, String> arg1) {
		List<ConnectorObject> rst = new ArrayList<ConnectorObject>();
		for (Map<String, String> account : accounts) {
			boolean match = true;
			for (String key : arg1.keySet()) {
				if (account.containsKey(key)
						&& account.get(key).equals(arg1.get(key)))
					continue;
				else {
					match = false;
					break;
				}
			}
			if (match) {
				rst.add(generateConnObj(account));
			}
		}
		return rst;
	}

	public ConnectorObject generateConnObj(Map<String, String> account) {
		ConnectorObjectBuilder conObjBld = new ConnectorObjectBuilder();
		for (String key : header) {
			conObjBld.addAttribute(key, account.get(key));
		}
		return conObjBld.build();
	}

	public void finish() throws IOException {
		PrintWriter writer = new PrintWriter(new FileWriter(targetFile));
		System.out.println("finish");
		int size = header.size();
		// print header
		for (int i = 0; i < size; i++) {
			writer.print(header.get(i));
			if (i == size - 1)
				writer.print('\n');
			else
				writer.print(',');
		}
		for (Map<String, String> account : accounts) {
			for (int i = 0; i < size; i++) {
				writer.print(account.get(header.get(i)));
				if (i == size - 1)
					writer.print('\n');
				else
					writer.print(',');
			}
		}
		writer.close();
	}

	public void printAccounts() {
		for (Map<String, String> acct : accounts) {
			for (String field : header) {
				System.out.println(field + ": " + acct.get(field));
			}
		}
	}
}
