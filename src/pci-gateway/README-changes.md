# PCI Gateway Remediation Notes

## What changed
- Jasypt upgraded to 3.0.5 to restore compatibility with newer Spring security/authorization changes and avoid encryption startup issues.
- Java target set to 21 (build/runtime) to satisfy platform requirements and allow newer dependency baselines.
- Spring Framework (spring-webmvc) lifted to 5.3.39 to address critical/high CVEs (including Spring4Shell and later traversal issues) while staying on the 5.3 line.
- Apache Commons Lang upgraded to 3.18.0 to fix the uncontrolled recursion CVE.
- Spring Cloud BOM moved to release 2020.0.6 (was snapshot) and snapshot/milestone repositories removed to pull only signed, supported artifacts and eliminate PKIX warnings.

## Why
- Address critical/high vulnerabilities reported by scanning (Spring Framework, Commons Lang) and ensure secure dependency sourcing.
- Align encryption layer (Jasypt) before other fixes so property decryption remains stable.
- Align build/runtime with Java 21 per requirement and to support patched dependency versions.

## Validation performed
- `mvn clean package -DskipTests` (module: src/pci-gateway) – success.
- `mvn clean test` (module: src/pci-gateway) – success.
- App run with profile `local`; `/actuator/health` responded with `{ "status":"UP" }`.

## Notes
- Snapshot/milestone repos removed; artifacts now resolve from release repositories only.
- If additional scans are required, rerun Dependabot/CodeQL after pulling these changes.
