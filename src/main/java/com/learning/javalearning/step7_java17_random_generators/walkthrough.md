# Step 7: Enhanced Random Generators (Java 17)

> ☕ **Java 17 Feature** - Works in Java 17, 21, and beyond!

## What's New?

Java 17 introduced a unified random number generation framework with:
- `RandomGenerator` interface
- New algorithms (Xoshiro, LXM family)
- Factory pattern for discovery
- Stream support
- Parallel-friendly splittable generators

---

## Quick Comparison

```java
// ❌ Old way (still works)
Random random = new Random();
int n = random.nextInt(100);

// ✅ New way (Java 17+)
RandomGenerator random = RandomGenerator.getDefault();
int n = random.nextInt(100);

// ✅ Specific algorithm
RandomGenerator xoshiro = RandomGenerator.of("Xoshiro256PlusPlus");
```

---

## Key Interfaces

| Interface | Purpose |
|-----------|---------|
| `RandomGenerator` | Base interface for all |
| `SplittableGenerator` | Can split for parallelism |
| `JumpableGenerator` | Can jump ahead in sequence |
| `RandomGeneratorFactory` | Create/discover algorithms |

---

## Stream Support

```java
RandomGenerator random = RandomGenerator.getDefault();

// Generate 10 random ints between 0-100
random.ints(10, 0, 100).forEach(System.out::println);

// Sum 1000 random doubles
double sum = random.doubles(1000, 0, 1).sum();
```

---

## Parallel Processing

```java
SplittableGenerator gen = (SplittableGenerator) 
    RandomGenerator.of("L64X128MixRandom");

// Split for parallel processing
long sum = gen.splits(4)
    .parallel()
    .mapToLong(g -> g.longs(1000).sum())
    .sum();
```

---

## Algorithm Recommendations

| Use Case | Algorithm |
|----------|-----------|
| General purpose | `L64X128MixRandom` |
| Fast, high quality | `Xoshiro256PlusPlus` |
| Parallel processing | Any `SplittableGenerator` |
| Cryptographic | `SecureRandom` |

---

## Run Example

```bash
cd src/main/java
java com/learning/javalearning/step7_java17_random_generators/EnhancedRandomExample.java
```
