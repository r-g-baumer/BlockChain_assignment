package Chain;

import com.google.gson.GsonBuilder;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.Map;

public class NoobChain {

    public static int difficulty = 5;

    public static Wallet walletA;

    public static Wallet walletB;

    public static ArrayList<Block> blockchain = new ArrayList<Block>();

    public static HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //list of all unspent transactions.


    public static Boolean isChainValid(){
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0','0');

        // loop through blockchain to check hashes
        for(int i=1; i < blockchain.size(); i++){
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            // compare registered hash and calculated hash
            if (!currentBlock.hash.equals(currentBlock.calculateHash())){
                System.out.println("Current Hashes not equal.");
                return false;
            }

            // compare previous hash and registered previous hash
            if (!currentBlock.previousHash.equals(previousBlock.hash)){
                System.out.println("Previous Hashes not equal.");
                return false;
            }

            // check if hash is solved
            if (!currentBlock.hash.substring(0,difficulty).equals(hashTarget)){
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }

    public static void blockAddition (){
    // We add our blocks to the arraylist
        System.out.println("Trying to Mine block 1...");
    addBlock(new Block("Hi I'm the first block", "0"));
    /* to enter the hashcode of the previous block and as this is an array list where
     * we don't know how many blocks will be added and thus the size of the array list;
     * we write the position/ index of the previous block as blockchain.size()-1
     * since at the creation of a new block the index of that block is the size of the BC
     */
        System.out.println("Trying to Mine block 2...");
    addBlock(new Block ("Yo I'm the second block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("Trying to Mine block 3...");
    addBlock(new Block ("Hey I'm the third block", blockchain.get(blockchain.size()-1).hash));
        System.out.println("\nBlockchain is Valid: " + isChainValid());
    String blockchainJson = StringUtil.getJson(blockchain);
        System.out.println("\nThe block chain: ");
        System.out.println(blockchainJson);
    }

    public static void main(String[] args){

        //We add blocks
        blockAddition();

        //Setup Bouncey castle as a Security Provider
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        //Create the new wallets
        walletA = new Wallet();
        walletB = new Wallet();
        System.out.println("Private and public keys:");
        System.out.println(StringUtil.getStringFromKey(walletA.privateKey));
        System.out.println(StringUtil.getStringFromKey(walletA.publicKey));
        //Create a test transaction from WalletA to walletB
        Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
        transaction.generateSignature(walletA.privateKey);
        //Verify the signature works and verify it from the public key
        System.out.println("Is signature verified");
        System.out.println(transaction.verifySignature());

    }
}