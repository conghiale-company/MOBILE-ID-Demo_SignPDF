package testopenpdf;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Enumeration;
import java.util.List;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.operator.DefaultDigestAlgorithmIdentifierFinder;
import org.bouncycastle.operator.DigestAlgorithmIdentifierFinder;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author DELL
 */
public class SignerTest {
    
    final private static String KEY01 = "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";
    final private static String KEY01_PASS = "12345678";
    
    final private static String KEY02 = "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";
    final private static String KEY02_PASS = "12345678";

    public static List<SignerKey> init() throws Exception {

        List<SignerKey> keys = new ArrayList<>();

        KeyStore keystore = null;
        InputStream is = null;
        Enumeration<String> e = null;
        PrivateKey privKey = null;
        X509Certificate cert = null;
        List<X509Certificate> x509CertChain = null;
        List<Certificate> certChain = null;
        String aliasName = null;
//        String file = null;
//        String filePassPhrase = null;
        Certificate[] chain = null;

//        file = "D:\\OneDrive\\work\\mobile-id\\internal\\keystores\\trustedhub.p12";
//        filePassPhrase = "12345678";

        x509CertChain = new ArrayList<>();
        certChain = new ArrayList<>();
        keystore = KeyStore.getInstance("PKCS12");
        is = new ByteArrayInputStream(Base64.getDecoder().decode(KEY01));
        keystore.load(is, KEY01_PASS.toCharArray());

        e = keystore.aliases();
        while (e.hasMoreElements()) {
            aliasName = e.nextElement();
            privKey = (PrivateKey) keystore.getKey(aliasName, KEY01_PASS.toCharArray());
            if (privKey != null) {
                break;
            }
        }
        cert = (X509Certificate) keystore.getCertificate(aliasName);
        chain = keystore.getCertificateChain(aliasName);
        for (Certificate c : chain) {
            x509CertChain.add((X509Certificate) c);
            certChain.add(c);
        }
        keys.add(new SignerKey(privKey, cert, x509CertChain, certChain));
        //////////////////////////////////////////////////////

//        file = "D:\\OneDrive\\work\\mobile-id\\internal\\keystores\\rssp.p12";
//        filePassPhrase = "12345678";

        x509CertChain = new ArrayList<>();
        certChain = new ArrayList<>();
        keystore = KeyStore.getInstance("PKCS12");
        is = new ByteArrayInputStream(Base64.getDecoder().decode(KEY02));
        keystore.load(is, KEY02_PASS.toCharArray());

        e = keystore.aliases();
        while (e.hasMoreElements()) {
            aliasName = e.nextElement();
            privKey = (PrivateKey) keystore.getKey(aliasName, KEY02_PASS.toCharArray());
            if (privKey != null) {
                break;
            }
        }
        cert = (X509Certificate) keystore.getCertificate(aliasName);
        chain = keystore.getCertificateChain(aliasName);
        for (Certificate c : chain) {
            x509CertChain.add((X509Certificate) c);
            certChain.add(c);
        }
        keys.add(new SignerKey(privKey, cert, x509CertChain, certChain));

        return keys;
    }

    public static class SignerKey {

        private PrivateKey privateKey;
        private X509Certificate signerCert;
        private List<X509Certificate> x509CertChain;
        private List<Certificate> certChain;

        public SignerKey(PrivateKey privateKey, X509Certificate signerCert, List<X509Certificate> chain, List<Certificate> certChain) {
            this.privateKey = privateKey;
            this.signerCert = signerCert;
            this.x509CertChain = chain;
            this.certChain = certChain;
        }

        public PrivateKey getPrivateKey() {
            return privateKey;
        }

        public X509Certificate getSignerCert() {
            return signerCert;
        }

        public List<X509Certificate> getX509CertChain() {
            return x509CertChain;
        }

        public List<Certificate> getCertChain() {
            return certChain;
        }

    }

    public static byte[] paddingSHAOID(byte[] hashedData, String hashAlg) throws Exception {

        DigestAlgorithmIdentifierFinder hashAlgorithmFinder = new DefaultDigestAlgorithmIdentifierFinder();
        AlgorithmIdentifier hashingAlgorithmIdentifier = hashAlgorithmFinder.find(hashAlg);
        DigestInfo digestInfo = new DigestInfo(hashingAlgorithmIdentifier, hashedData);
        return digestInfo.getEncoded();
    }
}
