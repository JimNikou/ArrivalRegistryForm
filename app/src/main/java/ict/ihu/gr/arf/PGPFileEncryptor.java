//package ict.ihu.gr.arf;
//
//import android.content.Context;
//import android.content.res.AssetManager;
//import android.util.Log;
//
//import org.bouncycastle.bcpg.ArmoredOutputStream;
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//import org.bouncycastle.openpgp.PGPEncryptedData;
//import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
//import org.bouncycastle.openpgp.PGPException;
//import org.bouncycastle.openpgp.PGPPublicKey;
//import org.bouncycastle.openpgp.PGPPublicKeyRing;
//import org.bouncycastle.openpgp.PGPPublicKeyRingCollection;
//import org.bouncycastle.openpgp.PGPUtil;
//import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
//import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
//import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Iterator;
//
//public class PGPFileEncryptor {
//
//    private static final String TAG = "PGPFileEncryptor";
//
//    BouncyCastleProvider provider = new BouncyCastleProvider();
//
//    public static String encryptString(Context context, String inputString, String publicKeyFileName) throws Exception {
//        AssetManager assetManager = context.getAssets();
//        InputStream pubKeyIn = null;
//        try {
//            pubKeyIn = assetManager.open(publicKeyFileName);
//            PGPPublicKey encKey = readPublicKey(pubKeyIn);
//
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            ArmoredOutputStream armoredOut = new ArmoredOutputStream(out);
//
//            byte[] bytes = inputString.getBytes();
//
//            PGPEncryptedDataGenerator encGen = new PGPEncryptedDataGenerator(
//                    new JcePGPDataEncryptorBuilder(PGPEncryptedData.CAST5).setWithIntegrityPacket(true).setSecureRandom(new java.security.SecureRandom()).setProvider("BC"));
//
//            encGen.addMethod(new JcePublicKeyKeyEncryptionMethodGenerator(encKey).setProvider("BC"));
//
//            OutputStream cOut = encGen.open(armoredOut, bytes.length);
//            cOut.write(bytes);
//            cOut.close();
//            armoredOut.close();
//
//            return out.toString();
//        } finally {
//            if (pubKeyIn != null) {
//                pubKeyIn.close();
//            }
//        }
//    }
//
//    private static PGPPublicKey readPublicKey(InputStream input) throws IOException, PGPException {
//        try {
//            PGPPublicKeyRingCollection pgpPub = new PGPPublicKeyRingCollection(
//                    PGPUtil.getDecoderStream(input), new JcaKeyFingerprintCalculator());
//
//            // Iterate through all key rings
//            Iterator<PGPPublicKeyRing> keyRingIter = pgpPub.getKeyRings();
//            while (keyRingIter.hasNext()) {
//                PGPPublicKeyRing keyRing = keyRingIter.next();
//
//                // Iterate through all keys in the current key ring
//                Iterator<PGPPublicKey> keyIter = keyRing.getPublicKeys();
//                while (keyIter.hasNext()) {
//                    PGPPublicKey key = keyIter.next();
//                    if (key.isEncryptionKey()) {
//                        return key;
//                    }
//                }
//            }
//            throw new IllegalArgumentException("Can't find encryption key in key ring.");
//        } catch (PGPException e) {
//            Log.e(TAG, "PGPException in readPublicKey: " + e.getMessage());
//            e.printStackTrace();
//            throw e;
//        } catch (IOException e) {
//            Log.e(TAG, "IOException in readPublicKey: " + e.getMessage());
//            e.printStackTrace();
//            throw e;
//        }
//    }
//}