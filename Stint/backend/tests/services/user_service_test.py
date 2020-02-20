# importing required libraries
import unittest

# importing services
from services.user_service import UserService
from handlers.mongo_handler import MongoDbHandler

# importing models
from models.user import User

# Class to hold test cases for Service class
class UserServiceTest(unittest.TestCase):

    # Test case for register method
    def test_register(self):
        # Initializing Service and invoking register(...)
        user_service = UserService("test")
        response = user_service.register({"email":"doe@gmail.com",
        "name":"John", "password":"123456", "confirmPassword":"123456",
        "phone":"+11231231231"})

        # Validating the response
        assert isinstance(response, User)
        assert response.user_id is not None
        user_service.delete_user(response.user_id)

    # Test case for register method
    def test_register_invalid_email_id(self):
        # Initializing Service and invoking register(...)
        user_service = UserService("test")
        response = user_service.register({"email":"doe@g",
        "name":"John", "password":"123456", "confirmPassword":"123456",
        "phone":"+11231231231"})

        # Validating the response
        self.assertEqual(response, "Invalid email or password")

    # Test case for register method to check if duplicate account is created
    def test_register_for_duplicate_account_creation(self):
        # Initializing Service and invoking register(...)
        user_service = UserService("test")
        response = user_service.register({"email":"doe@gmail.com",
        "name":"John", "password":"123456", "confirmPassword":"123456",
        "phone":"+11231231231"})
        user_id = response.user_id

        # Trying to create already created account
        response = user_service.register({"email":"doe@gmail.com",
        "name":"John", "password":"123456", "confirmPassword":"123456",
        "phone":"+11231231231"})

        # Validating the response
        self.assertEqual(response, "User Already exists")
        user_service.delete_user(user_id)

    # Test case to delete a non-existent user
    def test_delete_non_existent_user(self):
        # Initializing Service and invoking delete(...)
        user_service = UserService("test")
        self.assertEqual(user_service.delete_user("5dc449c3749fc689b1234567"), "User does not exist")

    # Test case for login method
    def test_login(self):
        # Initializing Service and invoking login(...)
        user_service = UserService("test")

        # Creating a user to login
        response = user_service.register({"email":"doe1@gmail.com",
        "name":"John", "password":"123456", "confirmPassword":"123456",
        "phone":"+11231231231"})
        user_id = response.user_id

        # Trying to login
        response = user_service.login({"email":"doe1@gmail.com",
        "password":"123456"})
        # Validating the response
        assert isinstance(response, User)
        assert response.token is not None
        assert response.user_id is not None

        # Deleting the user
        user_service.delete_user(user_id)

    # Test case for login method with invalid password
    def test_login_invalid_password(self):
        # Initializing Service and invoking login(...)
        user_service = UserService("test")

        # Creating a user to login
        response = user_service.register({"email":"doe1@gmail.com",
        "name":"John", "password":"123456", "confirmPassword":"123456",
        "phone":"+11231231231"})
        user_id = response.user_id

        # Trying to login
        response = user_service.login({"email":"doe1@gmail.com",
        "password":"12345631289012"})

        # Validating the response
        self.assertEqual(response, "Invalid Credentials")

        # Deleting the user
        user_service.delete_user(user_id)

    # Test case for login method with invalid email id
    def test_login_invalid_email(self):
        # Initializing Service and invoking login(...)
        user_service = UserService("test")
        
        # Trying to login with invalid user email
        response = user_service.login({"email":"doe213@gmail.com",
        "password":"12345631289012"})
        # Validating the response
        self.assertEqual(response, "User not available")

# Code to run the test cases in the above class
if __name__ == '__main__':
    unittest.main()
