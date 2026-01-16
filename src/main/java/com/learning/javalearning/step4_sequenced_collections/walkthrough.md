# Step 4: Sequenced Collections (Java 21)

## What's New?

Before Java 21, accessing the first/last element was **inconsistent**:

| Collection | Get First | Get Last |
|------------|-----------|----------|
| List | `list.get(0)` | `list.get(list.size()-1)` |
| Deque | `deque.getFirst()` | `deque.getLast()` |
| SortedSet | `set.first()` | `set.last()` |

**Java 21 Solution**: Unified `SequencedCollection` interface!

## New Interfaces

```
SequencedCollection<E>
├── getFirst(), getLast()
├── addFirst(E), addLast(E)
├── removeFirst(), removeLast()
└── reversed() → SequencedCollection<E>

SequencedSet<E> extends SequencedCollection<E>

SequencedMap<K,V>
├── firstEntry(), lastEntry()
├── putFirst(K,V), putLast(K,V)
├── pollFirstEntry(), pollLastEntry()
└── reversed() → SequencedMap<K,V>
```

## Key Methods

```java
// Before Java 21
String first = list.get(0);
String last = list.get(list.size() - 1);

// Java 21
String first = list.getFirst();
String last = list.getLast();
```

## Reversed Views (Zero Copy!)

```java
List<Integer> nums = new ArrayList<>(List.of(1, 2, 3));
List<Integer> rev = nums.reversed();  // NOT a copy!

// Iterating in reverse is now trivial
for (int n : nums.reversed()) {
    System.out.println(n);  // 3, 2, 1
}
```

## Run the Example

```bash
cd src/main/java
java com/learning/javalearning/step4_sequenced_collections/SequencedCollectionExample.java
```

## Next: [Step 5 - Advanced Record Patterns](../step5_advanced_patterns/walkthrough.md)
