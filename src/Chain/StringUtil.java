package Chain;

import com.google.gson.GsonBuilder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtil {

    // Applies Sha256 to a string and returns the result.
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Applies sha256 to our input,
            byte[] hash = digest.digest(input.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer(); // This will contain hash as hexadecimal
            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //Short hand helper to turn object into a json string
    public static String getJson(Object o) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(o);
    }

    //Returns difficulty string target, to compare to hash. eg difficulty of 5 will return "00000"
    public static String getDificultyString(int difficulty) {
        return new String(new char[difficulty]).replace('\0', '0');
    }

    public class Transaction {

        public String transactionId; // this is also the hash of the transaction.
        public PublicKey sender; // senders address/public key.
        public PublicKey reciepient; // Recipients address/public key.
        public float value;
        public byte[] signature; // this is to prevent anybody else from spending funds in our wallet.

        public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

        private int sequence = 0; // a rough count of how many transactions have been generated.

        // Constructor:
        public Transaction(PublicKey from, PublicKey to, float value,  ArrayList<TransactionInput> inputs) {
            this.sender = from;
            this.reciepient = to;
            this.value = value;
            this.inputs = inputs;
        }

        // This Calculates the transaction hash (which will be used as its Id)
        private String calulateHash() {
            sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
            return StringUtil.applySha256(
                    StringUtil.getStringFromKey(sender) +
                            StringUtil.getStringFromKey(reciepient) +
                            Float.toString(value) + sequence
            );
        }
    }



    //Applies ECDSA Signature and returns the result ( as bytes ).
    public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
        Signature dsa;
        byte[] output = new byte[0];
        try {
            dsa = Signature.getInstance("ECDSA", "BC");
            dsa.initSign(privateKey);
            byte[] strByte = input.getBytes();
            dsa.update(strByte);
            byte[] realSig = dsa.sign();
            output = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return output;
    }

    //Verifies a String signature
    public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
        try {
            Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
            ecdsaVerify.initVerify(publicKey);
            ecdsaVerify.update(data.getBytes());
            return ecdsaVerify.verify(signature);
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

}