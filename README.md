# Fortify

#  Fake Domain Deception

# a Create a fake static website under a primary domain (elliotwen.info).

# b Set up a fake AI backend (ai.elliotwen.info).

# c Modify the logic in Kotlin to send 10 requests to the backend, randomly

# choosing between the real and fake endpoints.

# d In the Native layer, covertly remove one “tˮ from ai.elliottwen.info.

# e Periodically send requests to the auth endpoint using randomly chosen

# real keys (to disguise the later transmission of real key fragments via

# /auth ).

#  Key Splitting

# a Integrate OpenSSL.

# b Integrate CURL.

# c Fragment 1 Encrypt with AES  Store the key in the Java layer  Store the

# ciphertext in the Native layer  Decrypt with OpenSSL.

# d Fragment 2 Encrypt with AES  Store the key in the Native layer  Store

# the ciphertext in the Native layer  Decrypt with OpenSSL.

# e Fragment 3 Decrypt a specified key  Request /auth  Extract 16

# characters starting from the 10th character of the signature  Decrypt to

# obtain the real fragment.

# i When sending the specified key to the fake domainʼs /auth endpoint,

# the returned signature is fixed.

# ii Encrypt the specified key.

# iii Encrypt the real key fragment.

# f Fragment 4 AES encryption  Write the ciphertext to the MetaData

# UserComment key  Send a request to /generate\_image .

# Fortify 1g Fragment 5 Three rounds of XOR encryption with different keys  Store

# the keys in the Native layer  Store the ciphertext in the Native layer 

# Use custom decryption.

#  Network Requests

# a Combine key fragments.

# b Use CURL in the C layer to send network requests.

# c Since requests are made in the Native layer, Charles or mitmproxy cannot

# directly proxy the traffic.

# d Typical Frida scripts also find it difficult to hook these requests.

#  Man-in-the-Middle Attack Protection

# a Use CertificatePinner with the R10 certificate for the fake domain, and do

# not assemble the key if conditions are abnormal.

# b Use OpenSSL with the R10 certificate for the fake domain, and freeze the

# screen in abnormal cases (generally cannot be triggered).

# c Check for suspicious proxy settings at startup and before sending

# requests, and freeze the screen if detected:

# i Proxy settings in system properties, such as http.proxyHost .

# ii Proxy settings in environment variables, such as HTTP\_PROXY .

#  Frida Detection

# a Before combining key fragments, the Native layer checks for the presence

# of Frida:

# i Checks process mappings.

# ii Checks default ports.

# iii Checks for library files.

#  Root Detection Can only run on devices with Google Play Store API

# a The Java layer checks for root at startup and forcefully exits if detected:

# i Checks for common root management app package names.

# ii Checks for the presence of common su binary files.

# Fortify 2iii Attempts to execute the su command.

# iv Checks if the build tags contain suspicious keywords.

# v Checks system properties.

# b The Native layer checks for root at startup and forcefully exits if detected:

# i Checks common su file paths.

# ii Checks if there are writable system partitions in /proc/mounts .

# iii Checks directories of common root apps.

# iv Checks for Magisk hide directories.

# v Attempts to execute the su command.

# vi Attempts to write to protected system directories.

#  Remove all Log Statements and Native Layer Comments

#  Apply Multiple OLLVM Obfuscation Techniques to the Native Layer During

# Compilation

# set(CMAKE\_C\_FLAGS\_DEBUG "$CMAKE\_C\_FLAGS\_DEBUG -mllvm -fla -

# mllvm -bcf -mllvm -bcf\_prob=80 -mllvm -bcf\_loop=3 -mllvm -sub -mllvm

# -sub\_loop=3 -mllvm -split -mllvm -split\_num=5")

# Fortify 3

