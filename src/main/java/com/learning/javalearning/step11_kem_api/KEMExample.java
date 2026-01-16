package com.learning.javalearning.step11_kem_api;

import javax.crypto.*;
import java.security.*;
import java.util.*;

/**
 * JAVA 21: KEY ENCAPSULATION MECHANISM API (JEP 452 - Final!)
 * 
 * KEM is a modern cryptographic technique for secure key exchange.
 * It's used in post-quantum cryptography and modern protocols like TLS 1.3.
 * 
 * HOW KEM WORKS:
 * 1. Receiver generates a key pair (public + private)
 * 2. Sender uses receiver's PUBLIC key to encapsulate a shared secret
 * 3. Sender gets: shared secret + encapsulation (ciphertext)
 * 4. Receiver uses PRIVATE key to decapsulate and get the same shared secret
 * 5. Both parties now have the same symmetric key for encryption!
 * 
 * ADVANTAGE OVER TRADITIONAL KEY EXCHANGE:
 * - Simpler API than Diffie-Hellman
 * - Better suited for post-quantum algorithms
 * - Integrated into Java's security framework
 */
public class KEMExample {

    public static void main(String[] args) throws Exception {
        System.out.println("=== JAVA 21: Key Encapsulation Mechanism (KEM) API ===\n");

        // 1. Explain KEM concept
        explainKEMConcept();

        // 2. Demonstrate KEM workflow
        demonstrateKEMWorkflow();

        // 3. Compare with traditional key exchange
        compareWithTraditional();

        // 4. Show use cases
        showUseCases();
    }

    static void explainKEMConcept() {
        System.out.println("--- 1. What is KEM? ---\n");

        System.out.println("""
                KEY ENCAPSULATION MECHANISM (KEM):

                A modern approach to securely share symmetric keys between parties.

                ┌─────────────────────────────────────────────────────────────────┐
                │                         RECEIVER                                │
                │  1. Generate key pair                                           │
                │     KeyPairGenerator.getInstance("X25519")                      │
                │     → publicKey (share this)                                    │
                │     → privateKey (keep secret)                                  │
                └─────────────────────────────────────────────────────────────────┘
                                  │
                                  │ Share public key
                                  ▼
                ┌─────────────────────────────────────────────────────────────────┐
                │                          SENDER                                 │
                │  2. Encapsulate using receiver's public key                     │
                │     KEM.Encapsulator enc = kem.newEncapsulator(publicKey);      │
                │     KEM.Encapsulated result = enc.encapsulate();                │
                │     → sharedSecret (use for encryption)                         │
                │     → encapsulation (send to receiver)                          │
                └─────────────────────────────────────────────────────────────────┘
                                  │
                                  │ Send encapsulation
                                  ▼
                ┌─────────────────────────────────────────────────────────────────┐
                │                         RECEIVER                                │
                │  3. Decapsulate using private key                               │
                │     KEM.Decapsulator dec = kem.newDecapsulator(privateKey);     │
                │     SecretKey sharedSecret = dec.decapsulate(encapsulation);    │
                │     → Same sharedSecret as sender!                              │
                └─────────────────────────────────────────────────────────────────┘

                ✅ Both parties now have the same symmetric key!
                ✅ Can use it for AES encryption, HMAC, etc.
                """);
    }

    static void demonstrateKEMWorkflow() throws Exception {
        System.out.println("--- 2. KEM Workflow Demo ---\n");

        // Step 1: Receiver generates key pair
        System.out.println("📥 RECEIVER: Generating key pair...");
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("X25519");
        KeyPair receiverKeyPair = kpg.generateKeyPair();
        PublicKey receiverPublicKey = receiverKeyPair.getPublic();
        PrivateKey receiverPrivateKey = receiverKeyPair.getPrivate();
        System.out.println("   Public key algorithm: " + receiverPublicKey.getAlgorithm());
        System.out.println("   Key format: " + receiverPublicKey.getFormat());

        // Step 2: Sender encapsulates using receiver's public key
        System.out.println("\n📤 SENDER: Encapsulating shared secret...");
        KEM kem = KEM.getInstance("DHKEM");
        KEM.Encapsulator encapsulator = kem.newEncapsulator(receiverPublicKey);
        KEM.Encapsulated encapsulated = encapsulator.encapsulate();

        SecretKey senderSharedSecret = encapsulated.key();
        byte[] encapsulation = encapsulated.encapsulation();

        System.out.println("   Shared secret algorithm: " + senderSharedSecret.getAlgorithm());
        System.out.println("   Shared secret length: " + senderSharedSecret.getEncoded().length + " bytes");
        System.out.println("   Encapsulation length: " + encapsulation.length + " bytes");

        // Step 3: Receiver decapsulates
        System.out.println("\n📥 RECEIVER: Decapsulating...");
        KEM.Decapsulator decapsulator = kem.newDecapsulator(receiverPrivateKey);
        SecretKey receiverSharedSecret = decapsulator.decapsulate(encapsulation);

        System.out.println("   Shared secret algorithm: " + receiverSharedSecret.getAlgorithm());
        System.out.println("   Shared secret length: " + receiverSharedSecret.getEncoded().length + " bytes");

        // Verify both have the same secret
        boolean secretsMatch = Arrays.equals(
                senderSharedSecret.getEncoded(),
                receiverSharedSecret.getEncoded());

        System.out.println("\n✅ Secrets match: " + secretsMatch);
        System.out.println("   Both parties now have the same symmetric key!");
        System.out.println();
    }

    static void compareWithTraditional() {
        System.out.println("--- 3. KEM vs Traditional Key Exchange ---\n");

        System.out.println("""
                ┌─────────────────────────────────────────────────────────────────────────┐
                │              Traditional DH              │             KEM               │
                ├─────────────────────────────────────────────────────────────────────────│
                │ KeyAgreement ka = KeyAgreement          │ KEM kem = KEM.getInstance     │
                │     .getInstance("ECDH");               │     ("DHKEM");                │
                │ ka.init(privateKey);                    │                               │
                │ ka.doPhase(otherPublicKey, true);       │ // Sender                     │
                │ byte[] secret = ka.generateSecret();    │ var enc = kem.newEncapsulator │
                │                                         │     (publicKey);              │
                │ // Need to derive key manually!         │ var result = enc.encapsulate. │
                │ // Complex error handling               │     ();                       │
                │ // Both parties need to exchange        │ SecretKey key = result.key(); │
                │ // public keys                          │                               │
                │                                         │ // Receiver                   │
                │                                         │ var dec = kem.newDecapsulator │
                │                                         │     (privateKey);             │
                │                                         │ SecretKey key = dec           │
                │                                         │     .decapsulate(bytes);      │
                └─────────────────────────────────────────────────────────────────────────┘

                ADVANTAGES OF KEM:
                ✅ Simpler API - encapsulate/decapsulate
                ✅ Returns SecretKey directly - no manual derivation
                ✅ One-way communication - sender only needs public key
                ✅ Post-quantum ready - designed for future algorithms
                ✅ Better for TLS 1.3 and modern protocols
                """);
    }

    static void showUseCases() {
        System.out.println("--- 4. Use Cases ---\n");

        System.out.println("""
                WHEN TO USE KEM:

                1. 🔐 Secure Key Exchange
                   - Establishing shared secrets between parties
                   - Client-server communication setup

                2. 🌐 TLS/HTTPS Implementations
                   - Modern TLS 1.3 uses KEM-like mechanisms
                   - Secure web communications

                3. 📧 End-to-End Encryption
                   - Messaging apps (Signal-like protocols)
                   - Email encryption

                4. 🔮 Post-Quantum Cryptography
                   - KEM is the standard for PQC algorithms
                   - Future-proof your security

                5. 🔑 Hybrid Encryption
                   - Use KEM to exchange symmetric keys
                   - Then use AES for bulk data encryption

                ─────────────────────────────────────────────────────────────────

                AVAILABLE ALGORITHMS IN JAVA 21:

                Algorithm      │ Description
                ───────────────┼────────────────────────────────────
                DHKEM          │ Diffie-Hellman based KEM

                Future (expected with PQC):
                - ML-KEM (Kyber)     │ NIST post-quantum standard
                - BIKE, Classic McEliece, etc.
                """);
    }
}
