# Step 7a: HexFormat Utility (Java 17)

> ☕ **Java 17 Feature** - Works in Java 17, 21, and beyond!

## What is HexFormat?

A utility class for converting between bytes and hex strings. No more manual conversion or external libraries!

---

## Quick Examples

```java
HexFormat hex = HexFormat.of();

// Bytes to hex
byte[] bytes = {0xCA, 0xFE, 0xBA, 0xBE};
String hexStr = hex.formatHex(bytes);  // "cafebabe"

// Hex to bytes
byte[] parsed = hex.parseHex("cafebabe");
```

---

## Format Options

```java
// Uppercase
HexFormat.of().withUpperCase();  // "CAFEBABE"

// With delimiter
HexFormat.ofDelimiter(":");  // "CA:FE:BA:BE"

// With prefix
HexFormat.of().withPrefix("0x");  // "0xCA0xFE0xBA0xBE"

// Combined
HexFormat.ofDelimiter(" ")
    .withPrefix("[").withSuffix("]")
    .withUpperCase();  // "[CA] [FE] [BA] [BE]"
```

---

## Use Cases

| Use Case | Example |
|----------|---------|
| MAC Address | `00:1A:2B:3C:4D:5E` |
| Hash Display | `5d41402abc4b2a76...` |
| Color Codes | `#FF69B4` |
| Debug Binary | `CA FE BA BE` |

---

## Run Example

```bash
cd src/main/java
java com/learning/javalearning/step7a_java17_hex_format/HexFormatExample.java
```
