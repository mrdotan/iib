package com.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.Key;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.w3c.dom.NodeList;

public class SignUtils {
	public static void verifyXML(String fileFullName) throws KeyStoreException,
			IOException, NoSuchAlgorithmException, CertificateException,
			UnrecoverableKeyException, Exception {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		Document doc = dbFactory.newDocumentBuilder().parse(
				new FileInputStream(fileFullName));
		String providerName = System.getProperty("jsr105Provider",
				"org.jcp.xml.dsig.internal.dom.XMLDSigRI");
		NodeList nl = doc.getElementsByTagNameNS(XMLSignature.XMLNS,
				"Signature");
		System.out.println("So luong chu ky " + nl.getLength());
		if (nl.getLength() == 0) {
			throw new Exception("File khong co chu ky nao");
		}

		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM",
				(Provider) Class.forName(providerName).newInstance());
		StringBuilder flagCheckCKSOK = new StringBuilder("");
		for (int i = 0; i < nl.getLength(); i++) {
			KeyInfoKeySelector keySelector = new KeyInfoKeySelector();
			DOMValidateContext valContext = new DOMValidateContext(keySelector,
					nl.item(i));
			XMLSignature signature = fac.unmarshalXMLSignature(valContext);
			boolean coreValidity = signature.validate(valContext);
			if (!coreValidity) {
				flagCheckCKSOK.append("false");
			} else {
				flagCheckCKSOK.append("true");
			}
		}

		System.out.println(flagCheckCKSOK.toString());
		if (flagCheckCKSOK.toString().indexOf("true") < 0) {
			throw new Exception("Nội dung chu ky  da bi thay doi !");
		}
	}

	public static class KeyInfoKeySelector extends KeySelector implements
			KeySelectorResult {
		private X509Certificate certificate;
		private X509Certificate[] certChain;
		@Override
		public KeySelectorResult select(KeyInfo keyInfo,
				KeySelector.Purpose purpose, AlgorithmMethod method,
				XMLCryptoContext context) throws KeySelectorException {
			ArrayList certList = new ArrayList();
			if (null == keyInfo) {
				throw new KeySelectorException("no ds:KeyInfo present");
			}
			List<XMLStructure> keyInfoContent = keyInfo.getContent();
			this.certificate = null;
			for (XMLStructure keyInfoStructure : keyInfoContent) {
				if (false == (keyInfoStructure instanceof X509Data)) {
					continue;
				}
				X509Data x509Data = (X509Data) keyInfoStructure;
				List<Object> x509DataList = x509Data.getContent();
				for (Object x509DataObject : x509DataList) {
					if (false == (x509DataObject instanceof X509Certificate)) {
						continue;
					}
					certList.add(x509DataObject);
				}
				if (!certList.isEmpty()) {
					this.certChain = (X509Certificate[]) certList
							.toArray(new X509Certificate[0]);
					this.certificate = (X509Certificate) this.certChain[0];
					return this;
				}
			}
			throw new KeySelectorException("No key found!");
		}

		@Override
		public Key getKey() {
			return this.certificate.getPublicKey();
		}

		/**
		 * Gives back the X509 certificate used during the last signature
		 * verification operation.
		 * 
		 * @return
		 */
		public X509Certificate getCertificate() {
			return this.certificate;
		}

		public X509Certificate[] getCertChain() {
			return certChain;
		}
	}
}
