#!/usr/bin/env bash
#
# generate-jwt-keys.sh
#
# Generates an RSA 2048-bit keypair for JWT signing (RS256).
# The private key is in PKCS#8 format (required by Java), the public key
# is in X.509/SPKI format (used by Spring's NimbusJwtDecoder).
#
# Output:
#   src/main/resources/keys/jwt-private-pkcs8.pem  (gitignored - keep secret!)
#   src/main/resources/keys/jwt-public.pem         (gitignored - distribute freely)
#
# Usage:  bash scripts/generate-jwt-keys.sh

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
KEYS_DIR="${SCRIPT_DIR}/../src/main/resources/keys"

mkdir -p "${KEYS_DIR}"

TMP_PRIVATE="${KEYS_DIR}/jwt-private-tmp.pem"
PRIVATE_KEY="${KEYS_DIR}/jwt-private-pkcs8.pem"
PUBLIC_KEY="${KEYS_DIR}/jwt-public.pem"

echo "Generating RSA 2048-bit keypair in ${KEYS_DIR} ..."

# Generate raw RSA private key (PKCS#1)
openssl genrsa -out "${TMP_PRIVATE}" 2048

# Convert to PKCS#8 (Java requires this format)
openssl pkcs8 -topk8 -inform PEM -outform PEM -nocrypt \
    -in "${TMP_PRIVATE}" \
    -out "${PRIVATE_KEY}"

# Extract public key
openssl rsa -in "${TMP_PRIVATE}" -pubout -out "${PUBLIC_KEY}"

# Clean up tmp file
rm -f "${TMP_PRIVATE}"

# Restrict file permissions
chmod 600 "${PRIVATE_KEY}"
chmod 644 "${PUBLIC_KEY}"

echo "Done."
echo "  Private key: ${PRIVATE_KEY} (DO NOT COMMIT)"
echo "  Public key:  ${PUBLIC_KEY}  (DO NOT COMMIT)"
echo
echo "Add these files to .gitignore if not already present."