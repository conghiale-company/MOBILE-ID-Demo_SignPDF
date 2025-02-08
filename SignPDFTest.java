package testopenpdf;

import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfDate;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfSigGenericPKCS;
import com.lowagie.text.pdf.PdfSignatureAppearance;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.TSAClientBouncyCastle;
import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.Security;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SignPDFTest {

    {
        Security.addProvider(new BouncyCastleProvider());
    }


    public static void main(String[] args) {
        SignPDFTest signer = new SignPDFTest();
        try {
            /*
            signer.signPdf(
                    "file\\ok.pdf",
                    "file\\ok.signed.pdf",
                    "D:\\OneDrive\\work\\mobile-id\\internal\\keystores\\rssp.p12",
                    "12345678",
                    "Test openPDF",
                    "VietNam"
            );
            */
            signer.simpleSign("file\\ok.pdf","file\\ok.signed.pdf");
            //System.out.println("PDF đã được ký thành công.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void simpleSign(String inputfile, String outputfile)
            throws Exception {
        try {

            List<SignerTest.SignerKey> keys = SignerTest.init();

            Certificate[] chain = keys.get(0).getCertChain().toArray(new Certificate[0]);

            PdfReader reader = new PdfReader(inputfile);
            FileOutputStream fout = new FileOutputStream(outputfile);
            PdfStamper stp = PdfStamper.createSignature(reader, fout, '\0', null, true);
            PdfSignatureAppearance sap = stp.getSignatureAppearance();
            sap.setCrypto(null,
                    chain,
                    null,
                    PdfSignatureAppearance.SELF_SIGNED);

            sap.setReason("reason");
            sap.setLocation("HCN");
            
            

            Rectangle cropBox = reader.getCropBox(1);
            float width = 50;
            float height = 50;
            Rectangle rectangle = new Rectangle(cropBox.getRight(width) - 20, cropBox.getTop(height) - 20,
                    cropBox.getRight() - 20, cropBox.getTop() - 20);
            sap.setVisibleSignature(rectangle, 1, null);
            sap.setLayer2Text("");

            sap.setExternalDigest(new byte[256], new byte[32], "RSA");
            sap.preClose();

            java.io.InputStream inp = sap.getRangeStream();
            byte[] dtbs = saveByteArrayOutputStream(inp);
            
            
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(dtbs);
            byte[] hashed = md.digest();

            // sign the hash value
            Signature s = Signature.getInstance("SHA256withRSA", "BC");
            s.initSign(keys.get(0).getPrivateKey());
            s.update(dtbs);
            
            byte[] signed = s.sign();

            PdfPKCS7 pdfSignature = sap.getSigStandard().getSigner();
            pdfSignature.setExternalDigest(signed, hashed, "RSA");

            PdfDictionary dic = new PdfDictionary();
            dic.put(PdfName.CONTENTS, new PdfString(pdfSignature.getEncodedPKCS1()).setHexWriting(true));
            //dic.put(PdfName.CREATOR, new PdfString("CREATOR"));
            //dic.put(PdfName.CREATORINFO, new PdfString("CREATORINFO"));
            sap.close(dic);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] saveByteArrayOutputStream(InputStream body) {
        int c;
        byte[] r = null;
        try {
            ByteArrayOutputStream f = new ByteArrayOutputStream();
            while ((c = body.read()) > -1) {
                f.write(c);
            }
            r = f.toByteArray();
            f.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return r;
    }

}
