//package ict.ihu.gr.arf;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//
//import org.bouncycastle.jce.provider.BouncyCastleProvider;
//
//import java.security.KeyFactory;
//import java.security.KeyPair;
//import java.security.KeyPairGenerator;
//import java.security.PrivateKey;
//import java.security.PublicKey;
//import java.security.Security;
//import java.security.spec.PKCS8EncodedKeySpec;
//import java.security.spec.X509EncodedKeySpec;
//import java.util.Base64;
//
//import javax.crypto.Cipher;
//
//public class RSAEncryptor {
//    private String SHARED_PREFS_NAME = "";
//    private static final String PREF_PUBLIC_KEY = "public_key";
//    private static final String PREF_PRIVATE_KEY = "private_key";
//
//    private Context context;
//    private PrivateKey privateKey;
//    private PublicKey publicKey;
//
//    public RSAEncryptor(){}
//    public RSAEncryptor(Context context) {
//        this.context = context;
//        Security.addProvider(new BouncyCastleProvider());
//        loadKeys();
//        if (publicKey == null || privateKey == null) {
//            generateKeys();
//            saveKeys();
//        }
//    }
//
//    private void generateKeys() {
//        try {
//            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
//            generator.initialize(2048);
//            KeyPair pair = generator.generateKeyPair();
//            privateKey = pair.getPrivate();
//            publicKey = pair.getPublic();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private String getUniquePrefsName() {
//        return SHARED_PREFS_NAME + "_" + android.os.Build.SERIAL; // Use a unique identifier like device serial number
//    }
//
//    public void saveExternalPublicKey(String externalPublicKeyBase64) {
//        try {
//            byte[] keyBytes = Base64.getDecoder().decode(externalPublicKeyBase64);
//            PublicKey externalPublicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(keyBytes));
//            SharedPreferences sharedPreferences = context.getSharedPreferences(getUniquePrefsName(), Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPreferences.edit();
//            editor.putString("external_public_key", Base64.getEncoder().encodeToString(externalPublicKey.getEncoded()));
//            editor.apply();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private void saveKeys() {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(getUniquePrefsName(), Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString(PREF_PUBLIC_KEY, Base64.getEncoder().encodeToString(publicKey.getEncoded()));
//        editor.putString(PREF_PRIVATE_KEY, Base64.getEncoder().encodeToString(privateKey.getEncoded()));
//        editor.apply();
//    }
//
//    private void loadKeys() {
//        SharedPreferences sharedPreferences = context.getSharedPreferences(getUniquePrefsName(), Context.MODE_PRIVATE);
//        String publicKeyStr = sharedPreferences.getString(PREF_PUBLIC_KEY, null);
//        String privateKeyStr = sharedPreferences.getString(PREF_PRIVATE_KEY, null);
//        if (publicKeyStr != null && privateKeyStr != null) {
//            try {
//                publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyStr)));
//                privateKey = KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyStr)));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//
//    public String encrypt(String message) throws Exception {
//        byte[] messageToBytes = message.getBytes();
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
//        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
//        return encode(encryptedBytes);
//    }
//
//    private String encode(byte[] data) {
//        return java.util.Base64.getEncoder().encodeToString(data);
//    }
//
//    public String decrypt(String encryptedMessage) throws Exception {
//        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedMessage);
//        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
//        cipher.init(Cipher.DECRYPT_MODE, privateKey);
//        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
//        return new String(decryptedMessage, "UTF-8");
//    }
//
//    private byte[] decode(String data) {
//        return Base64.getDecoder().decode(data);
//    }
//
//    public String getPublicKey() {
//        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
//    }
//
//    // Method to get the private key as a Base64-encoded string
//    public String getPrivateKey() {
//        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
//    }
//
//    public String exportPrivateKey() {
//        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
//    }
//
//}