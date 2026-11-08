---
status: done
---

# Instruction: Accounts in domain and data

## Architecture projection

> Tree of the final files. ✅ create · ✏️ modify · ❌ delete

```txt
.
├── gradle/libs.versions.toml ✏️ android-minSdk 24 → 26
├── domain/src/commonMain/kotlin/app/homeout/domain/
│   ├── Account.kt ✅ name, email
│   ├── Credentials.kt ✅ isValidEmail, isValidPassword (8+), normalizeEmail
│   ├── AccountRepository.kt ✅ find, add, session read and write
│   ├── PasswordHasher.kt ✅ suspend hash, verify
│   ├── CreateAccount.kt ✅ Created | EmailTaken, signs in
│   ├── LogIn.kt ✅ SignedIn | Mismatch
│   ├── ContinueWithGoogle.kt ✅ fake session
│   └── GetSession.kt ✅ the signed-in email, or null
├── domain/src/commonTest/kotlin/app/homeout/domain/
│   ├── AccountFixtures.kt ✅ in-memory repository, plain-text test hasher
│   ├── CredentialsTest.kt ✅
│   ├── CreateAccountTest.kt ✅
│   ├── LogInTest.kt ✅
│   └── ContinueWithGoogleTest.kt ✅
├── data/src/commonMain/kotlin/app/homeout/data/
│   ├── KeyValueStore.kt ✅ getString, putString
│   ├── InMemoryKeyValueStore.kt ✅ Web and tests
│   ├── StoredAccountRepository.kt ✅ accounts and session over a KeyValueStore
│   ├── Pbkdf2PasswordHasher.kt ✅ iterations parameter, default 600,000; shared format and compare
│   ├── Pbkdf2.kt ✅ expect pbkdf2Sha256, secureRandomBytes
│   └── DataModule.kt ✏️ AccountRepository, PasswordHasher
├── data/src/androidMain/kotlin/app/homeout/data/
│   ├── SharedPreferencesStore.kt ✅
│   └── Pbkdf2.android.kt ✅ javax.crypto, SecureRandom
├── data/src/iosMain/kotlin/app/homeout/data/
│   ├── UserDefaultsStore.kt ✅
│   └── Pbkdf2.ios.kt ✅ CCKeyDerivationPBKDF, SecRandomCopyBytes
├── data/src/wasmJsMain/kotlin/app/homeout/data/Pbkdf2.wasmJs.kt ✅ throws: Web has no accounts
├── data/src/commonTest/kotlin/app/homeout/data/
│   ├── StoredAccountRepositoryTest.kt ✅
│   └── (Pbkdf2PasswordHasherTest.kt moves to androidHostTest)
├── data/src/androidHostTest/kotlin/app/homeout/data/Pbkdf2PasswordHasherTest.kt ✅
├── composeApp/src/commonMain/kotlin/app/homeout/di/
│   ├── InitKoin.kt ✏️ initKoin(platform, overrides)
│   └── DomainModule.kt ✏️ the four use cases
├── composeApp/src/wasmJsMain/kotlin/app/homeout/main.kt ✏️ in-memory store
├── composeApp/src/iosMain/kotlin/app/homeout/MainViewController.kt ✏️ UserDefaultsStore
├── androidApp/build.gradle.kts ✏️ data and koin-core (via the BOM), which composeApp keeps as implementation
├── androidApp/src/main/kotlin/app/homeout/android/HomeoutApplication.kt ✏️ SharedPreferencesStore
├── androidApp/src/androidTest/kotlin/app/homeout/android/SharedPreferencesStoreTest.kt ✅
├── composeApp/src/commonTest/kotlin/app/homeout/KoinTestHarness.kt ✏️ in-memory store, fast hasher, signedIn = true
└── composeApp/src/commonTest/kotlin/app/homeout/di/KoinGraphTest.kt ✏️ passes an in-memory platform module; resolves the new bindings
```

## Test Scope

| Scenario     | Action                  | Observable outcome   | Actor                           |
| ------------ | ----------------------- | -------------------- | ------------------------------- |
| Setup        | An in-memory store and a 1-iteration hasher | No accounts, no session | system |
| Happy path   | `CreateAccount("Ana", "Ana@X.pt ", "12345678", terms = true)`, then `LogIn("ana@x.pt", "12345678")` | `Created` with session `ana@x.pt`, then `SignedIn` | system |
| Edge case    | `CreateAccount` with an email that already has an account, in any case | `EmailTaken`, and no second account is written | system |
| Edge case    | `LogIn` with an unknown email, or with a wrong password | `Mismatch`, and the session is unchanged | system |
| Edge case    | `CreateAccount` with a blank name, an invalid email, a 7-character password or terms unticked | Throws `IllegalArgumentException`, and nothing is stored | system |
| Edge case    | `isValidEmail` on `a@b.c`, `maya@example`, `a b@c.d`, `""` | true, false, false, false | system |
| Edge case    | `ContinueWithGoogle()` | Session `google@homeout.local`, no account stored | system |
| Edge case    | Read the store after hashing | No stored value contains the password | system |
| Edge case    | `Pbkdf2PasswordHasher` hashes the same password twice | Different salts and hashes, both verify, a wrong password doesn't | system |
| Edge case    | A new `SharedPreferencesStore` on the same context reads a value the first one wrote | The value is there | system |
| Edge case    | `withKoin` with the default `signedIn = true` | `GetSession()` returns an email | system |
| Teardown     | Clear the test SharedPreferences file | Empty, as before | system |

## Tasks to do

### `1)` Platform minimum

> Make the Android version support the hash.

1. Raise `android-minSdk` from 24 to 26. Android's `PBKDF2WithHmacSHA256` exists only from API 26, so on Android 7 every sign-in would crash.
2. Run `./gradlew build`. No library is added: cryptography-kotlin breaks the Wasm tests (see the plan).

### `2)` Domain accounts

> Accounts and their rules, free of platform code.

1. Add `Account(name, email)`, and `Credentials` with `normalizeEmail` (trim, lowercase), `isValidEmail` (`^[^\s@]+@[^\s@]+\.[^\s@]+$`) and `isValidPassword` (8 or more characters).
2. Add `AccountRepository`: `suspend find(email)`, `suspend add(account, passwordHash)`, `suspend passwordHash(email)`, `fun session(): String?` (synchronous), `suspend signIn(email)`.
3. Add the `PasswordHasher` interface: `suspend hash(password): String`, `suspend verify(password, stored): Boolean`.
4. Add the use cases:
   - `CreateAccount`: checks the rules (`require`), returns `EmailTaken` when the email exists, otherwise stores the hash, signs in and returns `Created`.
   - `LogIn`: `SignedIn` when the hash verifies, otherwise `Mismatch`.
   - `ContinueWithGoogle`: signs in as `google@homeout.local`.
   - `GetSession`: the signed-in email.
5. Domain tests with an in-memory repository and a plain test hasher.

### `3)` Data stores and hashing

> Keep accounts and the session on the device.

1. `KeyValueStore` (`getString(key): String?`, `putString(key, value)`) and `InMemoryKeyValueStore`.
2. `SharedPreferencesStore(context)` (file `homeout.accounts`, `apply()`), and `UserDefaultsStore` over `NSUserDefaults.standardUserDefaults`.
3. `StoredAccountRepository(store)`:
   - keys `account.<email>.name`, `account.<email>.password`, `session.email`
   - reads through to the store on every call, with no in-memory cache, so clearing the store in a test is seen by the Koin singleton
4. `Pbkdf2PasswordHasher(iterations = 600_000)`:
   - PBKDF2-HMAC-SHA256, a 16-byte salt, a 32-byte key
   - stored as `<iterations>:<salt b64>:<hash b64>`; `verify` uses the stored iteration count
   - runs on `Dispatchers.Default`, so 600,000 rounds never block the main thread (ANR)
   - `verify` compares in constant time
   - gets `pbkdf2Sha256(password, salt, iterations, length)` and `secureRandomBytes(n)` from `expect` functions: `javax.crypto` and `SecureRandom` on Android, CommonCrypto (`CCKeyDerivationPBKDF`) and `SecRandomCopyBytes` on iOS, and a throwing actual on Wasm
5. Bind `AccountRepository` and `PasswordHasher` as singletons in `dataModule`. `KeyValueStore` comes from the platform module.
6. Common data tests over `InMemoryKeyValueStore` and a fake hasher. The real hasher's test runs on Android host tests, where `javax.crypto` exists.

### `4)` Wiring

> Each platform supplies its store, and tests keep opening on Home.

1. `initKoin(platform: Module, overrides: List<Module> = emptyList())`.
2. In `androidApp/build.gradle.kts`, add `implementation(projects.data)`, the Koin BOM and `koin-core`, so `HomeoutApplication` and the device test can see `SharedPreferencesStore` and `module {}`. `composeApp` keeps them as `implementation`, which doesn't pass them on.
3. Platform modules:
   - `HomeoutApplication`: `SharedPreferencesStore(applicationContext)`
   - `MainViewController`: `UserDefaultsStore()`
   - Web `main`: `InMemoryKeyValueStore()`
4. Add `CreateAccount`, `LogIn`, `ContinueWithGoogle` and `GetSession` to `domainModule`.
5. `withKoin(clock, timeZone, overrides, signedIn = true)`:
   - binds an `InMemoryKeyValueStore` and `Pbkdf2PasswordHasher(iterations = 1)`
   - when `signedIn`, records the session `maya@example.com` before `block`
6. `KoinGraphTest`'s bare `initKoin()` becomes `initKoin(module { single<KeyValueStore> { InMemoryKeyValueStore() } })`. It resolves the new use cases, with `AccountRepository` and `PasswordHasher` as singletons.
7. Android `SharedPreferencesStoreTest`: a value written by one instance is read by a new one.

## Test acceptance criteria

| Task | Acceptance criteria              |
| ---- | -------------------------------- |
| 1 | `./gradlew build` passes on every target, with minSdk 26 and no new library |
| 2 | Creating an account signs in. A duplicate email in any case is `EmailTaken`. A wrong password or unknown email is `Mismatch`. Invalid input never reaches the store |
| 3 | Two hashes of one password differ and both verify. No stored value contains the password. The repository reads back what it wrote |
| 4 | `androidApp` compiles against `data` and Koin. The Koin graph resolves on every platform. Every existing UI test still opens on Home. A SharedPreferences value survives a new store instance |
