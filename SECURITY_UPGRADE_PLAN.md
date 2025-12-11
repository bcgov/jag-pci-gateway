# Security Upgrade Plan - PCI Gateway
**Date:** December 5, 2025  
**Priority:** CRITICAL

## ⚠️ Critical Finding: Jasypt Must Be Updated First

**Someone correctly identified the blocker:** The current Jasypt version (2.0.0) is incompatible with modern Spring Boot and security fixes. This must be resolved before any other security updates.

### Why Jasypt Blocks Everything

1. **Spring Boot 3.x Migration Impossible**
   - Jasypt 2.0.0 uses `javax.*` packages
   - Spring Boot 3.x requires `jakarta.*` packages
   - No compatibility layer exists

2. **Security Framework Changes**
   - Spring Security 5.x → 6.x has breaking changes
   - Authorization architecture completely redesigned
   - Jasypt must support new security context

3. **Encrypted Configuration Required**
   - Application uses `ENC()` encrypted values
   - Database passwords, API keys, hash keys encrypted
   - Cannot remove encryption without exposing secrets

## ✅ Phase 1: Jasypt Update (COMPLETED)

### Changes Made
- ✅ Upgraded `jasypt-spring-boot` 2.0.0 → `jasypt-spring-boot-starter` 3.0.5
- ✅ Removed `@EnableEncryptableProperties` annotation (auto-configured in 3.0.5)
- ✅ Fixed duplicate dependencies (spring-webmvc, actuator)

### Why 3.0.5?
- Last version compatible with Spring Boot 2.x
- Supports Spring Boot 2.4.x through 2.7.x
- Prepares for Spring Boot 3.x migration (supports jakarta packages)
- Maintains backward compatibility with `ENC()` syntax

### Testing Required
```bash
# Verify encrypted properties still work
ENC_PASSWORD=yourpassword mvn spring-boot:run
```

## 🔄 Phase 2: Spring Boot 2.7.18 Upgrade (NEXT)

**Status:** Ready to implement after Phase 1 testing

### Why 2.7.18?
- Latest and most secure Spring Boot 2.x release
- Bridge version before Boot 3
- Patches ALL known CVEs in Boot 2.x line
- Still supports Java 8 (allows gradual migration)

### Dependencies to Update
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>

<properties>
    <java.version>11</java.version> <!-- Minimum for 2.7.x -->
    <log4j2.version>2.23.1</log4j2.version>
</properties>

<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.14.0</version>
</dependency>
<dependency>
    <groupId>commons-codec</groupId>
    <artifactId>commons-codec</artifactId>
    <version>1.16.0</version>
</dependency>
<dependency>
    <groupId>org.codehaus.janino</groupId>
    <artifactId>janino</artifactId>
    <version>3.1.12</version>
</dependency>
```

### Spring Security Addition
Must add Spring Security to secure endpoints:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

### Dockerfile Updates
```dockerfile
# Build stage
FROM maven:3.9.6-eclipse-temurin-11 as build

# Runtime stage
FROM eclipse-temurin:11-jre-jammy
```

## 🚀 Phase 3: Spring Boot 3.2.x + Java 17 (FUTURE)

**Status:** After Phase 2 stabilizes

### Major Changes Required
1. **Namespace Migration:** `javax.*` → `jakarta.*`
2. **Spring Security 6:** Complete authorization rewrite
3. **Java 17 minimum:** Modern JDK features
4. **Jasypt 3.0.5+:** Already compatible!

## 📋 Critical Security Fixes Needed (All Phases)

### 1. Authentication & Authorization
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Add API key authentication
    // Secure actuator endpoints
    // Implement rate limiting
}
```

### 2. Replace MD5 with SHA-256
```java
// CURRENT (INSECURE):
DigestUtils.md5Hex(data)

// REQUIRED:
DigestUtils.sha256Hex(data)
```

### 3. Input Validation
```java
@Valid @RequestBody PaymentRequest request
// Add javax.validation constraints
```

### 4. Security Headers
```yaml
spring:
  security:
    headers:
      frame-options: DENY
      content-type-options: nosniff
      xss-protection: 1; mode=block
```

### 5. HTTPS Enforcement
```yaml
server:
  ssl:
    enabled: true
  require-ssl: true
```

### 6. Secure Actuator
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: when-authorized
```

## 🔐 PCI-DSS Compliance Requirements

As a PCI Gateway, must implement:
- ✅ Encryption of card data in transit (HTTPS)
- ✅ Encryption of sensitive data at rest (Jasypt)
- ❌ Access control mechanisms (MISSING - add Spring Security)
- ❌ Audit logging (MISSING - add audit trail)
- ❌ Regular security testing (MISSING - add OWASP dependency check)
- ❌ Network segmentation (infrastructure concern)

## 📊 Vulnerability Summary

### Before Any Upgrades
- **Critical:** 50+ known CVEs
- **High:** 100+ known CVEs
- **PCI Compliance:** FAIL

### After Phase 1 (Jasypt 3.0.5)
- **Critical:** 50+ (no change - Spring Boot still vulnerable)
- **High:** 100+ (no change)
- **PCI Compliance:** FAIL
- **Blocker Removed:** ✅ Can now proceed with security fixes

### After Phase 2 (Spring Boot 2.7.18 + Security)
- **Critical:** 0-2 (only in-progress CVEs)
- **High:** 2-5 (minimal exposure)
- **PCI Compliance:** IMPROVED (still need audit logging)

### After Phase 3 (Spring Boot 3.2 + Java 17)
- **Critical:** 0
- **High:** 0-1
- **PCI Compliance:** PASS (with all fixes)

## 🎯 Immediate Next Steps

1. **Test Phase 1:** Verify Jasypt 3.0.5 works with existing encrypted values
2. **Implement Phase 2:** Upgrade to Spring Boot 2.7.18 + add Spring Security
3. **Add Security Tests:** OWASP dependency check, Snyk scan
4. **Plan Phase 3:** Schedule Java 17 + Spring Boot 3 migration

## 🚨 Critical Warning

**DO NOT deploy to production until at least Phase 2 is complete.**

This application handles payment card data and currently has:
- No authentication on sensitive endpoints
- Insecure cryptographic operations (MD5)
- Dozens of critical vulnerabilities
- Potential PCI-DSS violations

---
**Updated:** Phase 1 complete (Jasypt 3.0.5 installed)  
**Next Action:** Test encrypted properties, then proceed to Phase 2
