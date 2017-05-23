package com.iso8583;

import org.jpos.iso.ISOException;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.packager.GenericPackager;
import java.io.InputStream;
/**
*
* @author dntan
*/
public class PackISOMessage {
	public static void main(String[] args) {
		PackISOMessage iso = new PackISOMessage();
		try {
			String message = iso.buildISOMessage();
			System.out.printf("Message = %s", message);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String buildISOMessage() throws Exception {
		try {
			// Load package from resources directory.
			InputStream is = getClass().getResourceAsStream("basic.xml");
			GenericPackager packager = new GenericPackager(is);
			ISOMsg isoMsg = new ISOMsg();
			isoMsg.setPackager(packager);
			isoMsg.setHeader("ISO026000075".getBytes());
			System.out.println("Head err..........."
					+ new String(isoMsg.getHeader()));
			isoMsg.setMTI("0200");
			isoMsg.set(47, "47001");
			isoMsg.set("48.0", "abc");
			isoMsg.set("48.1", "abc");
			isoMsg.set("48.2", "abc");
			isoMsg.set(128, "100A8D053C69FF80");
			byte[] result = isoMsg.pack();
			isoMsg.dump(System.out, "");
			printISOMessage(isoMsg);
			return new String(result);
		} catch (ISOException e) {
			throw new Exception(e);
		}
	}

	private void printISOMessage(ISOMsg isoMsg) {
		try {
			System.out.printf("MTI = %s%n", isoMsg.getMTI());
			for (int i = 1; i <= isoMsg.getMaxField(); i++) {
				if (isoMsg.hasField(i)) {
					System.out.printf("Field (%s) = %s%n", i,
							isoMsg.getString(i));
				}
			}
			System.out.println("===" + isoMsg.toString());
		} catch (ISOException e) {
			e.printStackTrace();
		}
	}
}
