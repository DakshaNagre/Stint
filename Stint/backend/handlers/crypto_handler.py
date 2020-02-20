# importing required libraries
from passlib.context import CryptContext

# Class to handle string encryption and to check the validity of encrypted string
class CryptoHandler:

    # Method to encrypt a string using sha256
    def encrypt_the_string(self, text):
        return self.password_context().hash(text)

    # Method to verify the encrypted string aganist the plain text using sha256
    def verify_encrypted_string(self, text, hashed):
        return self.password_context().verify(text, hashed)

    # Method to build a CryptoContext with necessary encryption algorithm
    def password_context(self):
        return CryptContext( schemes=["pbkdf2_sha256"], default="pbkdf2_sha256", pbkdf2_sha256__default_rounds=30000)
