Generate private key

```openssl genrsa -out keys.pem 2048```

Create certificate signing request

`openssl req -new -sha256 -key keys.pem -out csr.csr`

Generate a self signed certificate to share

`openssl req -x509 -sha256 -days 365 -key keys.pem -in csr.csr -out certificate.pem`

Create PKCS12 keystore (set key password and alias)

`openssl pkcs12 -export -out identity.p12 -inkey keys.pem -in certificate.pem -name gd-client-2`

Convert PKCS12 keystore to JKS (set keystore password)

`keytool -importkeystore -srckeystore identity.p12 -srcstoretype pkcs12 -destkeystore keystore.jks -deststoretype jks`

Optional: Update key password

`keytool -keypasswd -alias gd-client-2 -keystore keystore.jks`