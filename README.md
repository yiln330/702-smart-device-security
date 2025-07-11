# 702 Smart Device Security Project

This project demonstrates multiple layers of security obfuscation, key protection, and anti-reverse engineering techniques applied to a smart device application. The goal is to prevent key leakage, resist static/dynamic analysis, and withstand network-level attacks such as man-in-the-middle (MitM).

##  Features

###  Fake Domain Deception
- Created a fake static website (`elliotwen.info`)
- Deployed a fake backend API (`ai.elliotwen.info`)
- Modified app logic to alternate between real and fake endpoints
- Covert domain mutation (e.g., removing a character from `ai.elliottwen.info`)
- Obfuscated key transmission via `/auth`

###  Key Splitting & Protection
- AES encryption used across multiple layers (Java/Native/OpenSSL)
- Key fragments are encrypted and stored in separate layers
- A specific fragment is extracted from the signature of the `/auth` response
- Fragment hidden in EXIF metadata (`UserComment`)
- XOR encrypted fragments stored and decrypted in Native layer

###  Native-Layer Network Requests
- Key fragments are combined in C layer
- Uses `CURL` to send requests natively (bypasses tools like Charles and mitmproxy)
- Frida scripts are ineffective on native requests

###  Man-in-the-Middle (MitM) Attack Protection
- Implements `CertificatePinner` and OpenSSL pinning with R10 cert
- Detects proxy settings in system properties and environment variables
- Aborts execution if suspicious behavior is detected

###  Frida Detection
- Detects Frida by:
  - Scanning process mappings
  - Checking default ports
  - Looking for suspicious libraries

###  Root Detection
- Java and Native layers independently check for:
  - Root apps, su binaries, Magisk files
  - Writable system partitions
  - Suspicious build tags or system properties

###  Anti-Reverse Engineering
- Removed all logs and comments in native code
- Applied multiple LLVM obfuscation techniques (`fla`, `bcf`, `sub`, `split`)

##  Technologies Used
- Java / Kotlin (Android)
- Native C (NDK)
- OpenSSL
- CURL
- Frida / mitmproxy (for testing bypasses)
- OLLVM for obfuscation

##  Project Structure
- `app/` – Android source code
- `native/` – Native C source files with key protection and network logic
- `metadata/` – EXIF embedding logic for key fragments
- `certs/` – Certificate pinning materials
- `docs/` – Report and diagrams

##  Disclaimer
This project is for academic and educational purposes only. All fake domains are used purely for demonstrating security design.

##  Project Demo
> [Optionally insert a link to a short YouTube demo or GitHub Pages]

---

