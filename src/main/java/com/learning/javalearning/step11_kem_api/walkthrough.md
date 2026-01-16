# Step 11: Key Encapsulation Mechanism API (Java 21 - Final!)

> ✅ **Final Feature**: No preview flags needed!

## What is KEM?

**Key Encapsulation Mechanism** is a modern cryptographic technique for secure key exchange, used in post-quantum cryptography and TLS 1.3.

---

## How It Works

```
RECEIVER                          SENDER
────────                          ──────
1. Generate key pair
   → publicKey (share)
   → privateKey (keep)
         │
         │ Share public key
         ▼
                              2. Encapsulate
                                 → sharedSecret
                                 → encapsulation
         │
         │ Send encapsulation
         ▼
3. Decapsulate
   → Same sharedSecret!

✅ Both have the same symmetric key for encryption!
```

---

## Code Example

```java
// 1. Receiver generates key pair
KeyPairGenerator kpg = KeyPairGenerator.getInstance("X25519");
KeyPair keyPair = kpg.generateKeyPair();

// 2. Sender encapsulates
KEM kem = KEM.getInstance("DHKEM");
KEM.Encapsulator enc = kem.newEncapsulator(keyPair.getPublic());
KEM.Encapsulated result = enc.encapsulate();
SecretKey sharedSecret = result.key();
byte[] encapsulation = result.encapsulation();

// 3. Receiver decapsulates
KEM.Decapsulator dec = kem.newDecapsulator(keyPair.getPrivate());
SecretKey sameSecret = dec.decapsulate(encapsulation);

// Both have the same symmetric key!
```

---

## KEM vs Traditional Key Exchange

| Aspect | Traditional DH | KEM |
|--------|----------------|-----|
| API | Complex | Simple ✅ |
| Output | Raw bytes | SecretKey ✅ |
| Direction | Two-way | One-way ✅ |
| Post-Quantum | Not ready | Ready ✅ |

---

## Use Cases

- 🔐 Secure key exchange
- 🌐 TLS 1.3 implementations
- 📧 End-to-end encryption
- 🔮 Post-quantum cryptography
- 🔑 Hybrid encryption (KEM + AES)

---

## Run Example

```bash
cd src/main/java
java com/learning/javalearning/step11_kem_api/KEMExample.java
```

## Back to: [Step 10 - Unnamed Classes](../step10_unnamed_classes/walkthrough.md)
