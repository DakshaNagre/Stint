import jwt
import datetime

# Class to handle JWT related functionalities
class JwtHandler:

	# Method to encode user_id and return a JWT token
	@staticmethod
	def encode_auth_token(user_id):
		try:
			payload = {'sub': str(user_id), 'exp': datetime.datetime.utcnow() + datetime.timedelta(hours = 24)}
			return jwt.encode(payload, 'Wrsapu0F7HeRmTt4PJZR', algorithm='HS256')
		except Exception as e:
			return e

	# Method to check the validity of the JWT token
	@staticmethod
	def decode_auth_token(token):
		try:
			payload = jwt.decode(token, 'Wrsapu0F7HeRmTt4PJZR')
			return payload
		except jwt.ExpiredSignatureError:
			return False
		except jwt.InvalidTokenError:
			return False
