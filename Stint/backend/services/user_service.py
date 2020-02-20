# Importing required libraries
import re
from bson.objectid import ObjectId

from flask import jsonify

# Importing models
from models.user import User

# Importing handlers
from handlers.mongo_handler import MongoDbHandler
from handlers.crypto_handler import CryptoHandler
from handlers.jwt_handler import JwtHandler
from handlers.amazon_s3_handler import AmazonS3Handler

# Class to hold the business logic for user data
class UserService:
    # Initializing the class
    def __init__(self, collection = 'users', mongo_db = MongoDbHandler(), crypto_handler = CryptoHandler()):
        self.collection = collection
        self.mongo_db = mongo_db
        self.crypto_handler = crypto_handler

    # Method to Register a user
    def register(self, user):
        # Logic to store the user
        try:
            if(self.__is_valid_email(user['email']) and user['password'] == user['confirmPassword']):
                users_collection = self.__get_user_collection()
                if(users_collection and users_collection.find({'email': user['email']}).count() <= 0):
                    user_obj = User()
                    user_obj.name = user['name']
                    user_obj.email = user['email']
                    user_obj.phone = user['phone']
                    user_obj.password = self.crypto_handler.encrypt_the_string(user['password'])
                    saved_user = users_collection.insert_one(user_obj.__dict__)
                    user_obj.user_id = str(saved_user.inserted_id)
                    return user_obj
                else:
                    return "User Already exists"
            else:
                return "Invalid email or password"
        except Exception as exception:
            raise exception

    # Method to login
    def login(self, user):
        try:
            users_collection = self.__get_user_collection()
            if(users_collection):
                user_obj = User()
                search_user = {'email': user['email']}
                user_db_object = users_collection.find(search_user)
                if(self.crypto_handler.verify_encrypted_string(user['password'], user_db_object[0]['password'])):
                    self.update_device_token(user, str(user_db_object[0]['_id']))
                    user_obj.name = user_db_object[0]['name']
                    user_obj.email = user_db_object[0]['email']
                    user_obj.phone = user_db_object[0]['phone']
                    user_obj.user_id = str(user_db_object[0]['_id'])
                    user_obj.emailNotification = user_db_object[0]['emailNotification']
                    user_obj.pushNotification = user_db_object[0]['pushNotification']
                    user_obj.image = user_db_object[0]['image']
                    user_obj.token = JwtHandler.encode_auth_token(user_id=user_db_object[0]['_id']).decode()
                    return user_obj
                else:
                    return "Invalid Credentials"
            else:
                return "Unable to connect to database"
        except IndexError as IE:
            return "User not available"
        except Exception as e:
            raise e


    # Method to delete a user
    def delete_user(self, user_id):
        # logic to delete a user
        try:
            users_collection = self.__get_user_collection()
            if(users_collection and users_collection.find({'_id' : ObjectId(user_id)}).count() > 0):
                # deleting the user
                return users_collection.delete_one({'_id' : ObjectId(user_id)})
            else:
                return "User does not exist"
        except Exception as exception:
            raise exception

    # Method to update android device token of the user
    def update_device_token(self, user, user_id):
        try:
            if("deviceToken" in user):
                # Updating the user record
                users_collection = self.__get_user_collection()
                query = { '_id':  ObjectId(user_id)}
                updated_values = { "$set": {"deviceToken": user['deviceToken']}}
                users_collection.update_one(query, updated_values)
        except Exception as exception:
            raise exception

    # Method to check the validity of email format
    def __is_valid_email(self, email):
        return bool(re.search(r"^[\w\.\+\-]+\@[\w]+\.[a-z]{2,3}$", email))

    # Method to get the database collection
    def __get_user_collection(self):
        return self.mongo_db.connect()[self.collection]


    # Method to update the user details
    def edit_user_details(self, user, headers):
        # logic to update user data
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Creating a user object
                user_obj = User()
                user_obj.user_id = payload['sub']
                user_obj.name = user['name']
                user_obj.email = user['email']
                user_obj.image = user['image']
                user_obj.emailNotification = user['emailNotification']
                user_obj.pushNotification = user['pushNotification']

                # Checking if the image is being passed or not
                if(user['image']):
                    # Storing the image in Amazon S3 bucket
                    amazon_s3_handler = AmazonS3Handler()
                    user_obj.image = amazon_s3_handler.store_in_S3(user['image'], user_obj.user_id)

                users_collection = self.__get_user_collection()

                if ("password" in user.keys() and user['password'] != "" and user['password'] == user['confirmPassword']):
                        user_obj.password = self.crypto_handler.encrypt_the_string(user['password'])
                else:
                    try:
                        if(users_collection and users_collection.find({'_id' : ObjectId(user_obj.user_id)})):
                            cursor = users_collection.find({'_id' : ObjectId(user_obj.user_id)})
                            user_obj.password = cursor[0]['password']

                    except Exception as exception:
                        raise exception

                # Updating the user collecion
                query = { '_id':  ObjectId(user_obj.user_id)}
                updated_values = { "$set": user_obj.__dict__ }
                users_collection.update_one(query, updated_values)

            # Closing the DB connection
                self.mongo_db.disconnect()
                user_obj.password = None
                return user_obj
            else:
                return "Invalid Authorization"

        except Exception as exception:
            raise exception
