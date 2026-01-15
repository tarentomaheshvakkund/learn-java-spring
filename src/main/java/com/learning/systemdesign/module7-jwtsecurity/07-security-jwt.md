    # Module 7: Security (JWT Authentication)

    ## 1. Overview
    Security is often the most complex part of System Design.
    In this module, we move from a "Trust Everyone" model to a **"Zero Trust"** model using **Stateless Authentication**.

    We replace traditional **Session IDs** (Stateful) with **JSON Web Tokens** (Stateless).

    ### Stateful vs. Stateless
    | Feature | Session (Stateful) | JWT (Stateless) |
    | :--- | :--- | :--- |
    | **Storage** | Server Memory / Redis | Client Side (Header) |
    | **Scalability** | Hard (Sticky Sessions needed) | Easy (Any server can verify) |
    | **Revocation** | Easy (Delete session) | Hard (Token is valid until expiry) |
    | **Data** | Opaque ID | JSON Payload (Claims) |

    ---

    ## 2. JWT Architecture (The Anatomy)
    A JWT looks like `aaaaa.bbbbb.ccccc`. It has 3 parts:

    ### A. Header (Algorithm)
    Describes *how* the token is signed.
    ```json
    { "alg": "HS256", "typ": "JWT" }
    ```

    ### B. Payload (Claims)
    The actual data. **DO NOT** put sensitive secrets (passwords) here; it is Base64 encoded, not encrypted!
    ```json
    {
    "sub": "bond",             // Subject (Username)
    "iat": 1516239022,         // Issued At
    "exp": 1516249022          // Expiration
    }
    ```

    ### C. Signature (The Security)
    This is what makes it tamper-proof.
    ```
    HMACSHA256(
    base64UrlEncode(header) + "." + base64UrlEncode(payload),
    SECRET_KEY
    )
    ```
    If a hacker changes the payload (e.g., `"role": "admin"`), the signature won't match unless they know the `SECRET_KEY`.

    ---

    ## 3. The Spring Security Chain
    Spring Security is essentially a giant chain of Filters. We injected our own filter into this chain.

    ```mermaid
    sequenceDiagram
        participant Client
        participant FilterChain
        participant JwtFilter
        participant UsernamePassFilter
        participant API

        Client->>FilterChain: Request (GET /api/v7/users)
        FilterChain->>JwtFilter: 1. Authorization Header?
        
        alt Token Valid
            JwtFilter->>JwtFilter: SecurityContextHolder.setAuth(User)
            JwtFilter-->>FilterChain: Continue
        else Token Missing/Invalid
            JwtFilter-->>FilterChain: Continue (Anonymous)
        end

        FilterChain->>UsernamePassFilter: 2. Check Context
        Note right of UsernamePassFilter: If Context is Empty AND URL requires Auth -> Throw 403
        
        UsernamePassFilter->>API: 3. Reach Controller
        API-->>Client: 200 OK
    ```

    ---

    ## 4. Implementation Logic

    ### `SecurityConfig`
    This is the firewall. We explicitly disabled CSRF because we are stateless (CSRF is an attack on Cookies).
    ```java
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(s -> s.sessionCreationPolicy(STATELESS)) // Server forgets you immediately
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/v7/auth/**").permitAll() // Open Door
            .anyRequest().authenticated()                   // Locked Door
        );
    ```

    ### `JwtAuthenticationFilter`
    This filter runs **Once Per Request**. It bridges the gap between the JWT "String" and Spring's "User Object".

    ---

    ## 5. Verification Steps

    ### Step 1: Registration (Public)
    **POST** `/api/v7/users`
    ```json
    { "username": "james_bond", "email": "007@mi6.gov" }
    ```
    *(If this fails with 403, check SecurityConfig allow rules)*.

    ### Step 2: Login (Get the Key)
    **POST** `/api/v7/auth/login`
    ```json
    { "username": "james_bond", "password": "password" }
    ```
    **Response**:
    ```json
    { "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW1l..." }
    ```

    ### Step 3: Access Secured Endpoint
    **GET** `/api/v7/users` (Without Header) -> **403 Forbidden**

    **GET** `/api/v7/users` (With Header) -> **200 OK**
    *   **Header**: `Authorization`
    *   **Value**: `Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqYW1l...`

    ---

    ## 6. What's Next? (System Design)
    In a real production system:
    1.  **Refresh Tokens**: JWTs should be short-lived (15 mins). Use a Refresh Token (Database backed) to get new JWTs.
    2.  **HTTPS**: Mandatory. If you send a JWT over HTTP, anyone sniffing WiFi can steal it and become you.
    3.  **Role Based Access (RBAC)**: Add `"role": "ADMIN"` to claims and use `@PreAuthorize("hasRole('ADMIN')")`.
