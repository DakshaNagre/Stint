# importing required libraries
import unittest
import jwt
import datetime
import base64
import requests

# importing services
from services.board_service import BoardService
from services.user_service import UserService
from handlers.mongo_handler import MongoDbHandler

# importing models
from models.board import Board

# Class to hold test cases for BoardService class
class BoardServiceTest(unittest.TestCase):

    # Class variables
    user_id = None
    headers = {}
    board = None

    # Method to initialize environment for test cases
    def setUp(self):
        # Initializing Service and invoking register(...)
        user_service = UserService("test")
        user = user_service.register({"email":"doe@gmail.com",
        "name":"John", "password":"123456", "confirmPassword":"123456",
        "phone":"+11231231231"})
        self.user_id = user.user_id
        self.headers['Authorization'] = self.__get_jwt_token(user.user_id)
        self.board = {'name' : 'board1', "color": "#fefefe"}

    # Method to release environment used by test cases
    def tearDown(self):
        # deleting the user
        user_service = UserService("test")
        user_service.delete_user(self.user_id)

    # Test case for create_board method
    def test_create_board(self):
        # Initializing BoardService and creating board
        board_service = BoardService("test")
        response = board_service.create_board(self.board, self.headers)

        # Validating the response
        assert isinstance(response, Board)
        assert response.user_id is not None

    # Test case for create_board method with invalid token
    def test_create_board_with_invalid_authorization(self):
        # Initializing BoardService and creating board
        board_service = BoardService("test")
        headers = {}
        headers['Authorization'] = "somerandomstringhere"
        response = board_service.create_board(self.board, headers)

        # Validating the response
        assert isinstance(response, str)
        self.assertEqual(response, "Invalid Authorization")

    # Test case for edit_board method
    def test_edit_board(self):
        # Initializing BoardService and editing board
        board_service = BoardService("test")
        response = board_service.create_board(self.board, self.headers)
        # Building the board object
        self.board['board_id'] = response.board_id
        self.board['name'] = "new name"
        self.board['description'] = "some description"
        self.board['color'] = "#cccccc"
        self.board['image'] = None

        # Editing the board
        response = board_service.edit_board(self.board, self.headers)

        # Validating the response
        assert isinstance(response, Board)
        assert response.user_id is not None
        self.assertEqual(response.name, "new name")
        self.assertEqual(response.description, "some description")
        self.assertEqual(response.color, "#cccccc")
        self.assertEqual(response.board_id, response.board_id)
        assert response.image is None

    # Test case for edit_board method
    def test_edit_board_with_image(self):
        # Initializing BoardService and editing board
        board_service = BoardService("test")
        response = board_service.create_board(self.board, self.headers)
        self.board['board_id'] = response.board_id
        self.board['name'] = "new name"
        self.board['description'] = "some description"
        self.board['color'] = "#cccccc"

        # Uploading the image
        self.board['image'] = self.__base64_image_string()

        # Editing the board
        response = board_service.edit_board(self.board, self.headers)

        # Validating the response
        assert isinstance(response, Board)
        assert response.user_id is not None
        assert response.image is not None
        self.assertEqual(response.name, "new name")
        self.assertEqual(response.description, "some description")
        self.assertEqual(response.color, "#cccccc")
        self.assertEqual(response.board_id, response.board_id)

    # Test case for edit_board method with invalid token
    def test_edit_board_with_invalid_authorization(self):
        # Initializing BoardService and editing board
        board_service = BoardService("test")
        headers = {}
        headers['Authorization'] = "somerandomstringhere"
        response = board_service.edit_board(self.board, headers)

        # Validating the response
        assert isinstance(response, str)
        self.assertEqual(response, "Invalid Authorization")

    # Test case for get_boards
    def test_get_boards(self):
        # Initializing BoardService
        board_service = BoardService("test")
        # Creating a board
        board_service.create_board(self.board, self.headers)
        # Retrieving boards
        response = board_service.get_boards(self.headers)

        # Validating the response
        assert isinstance(response, list)
        self.assertGreaterEqual(len(response), 1)

    # Test case for create_board method with invalid token
    def test_get_boards_with_invalid_authorization(self):
        # Initializing BoardService and retrieving board
        board_service = BoardService("test")
        headers = {}
        headers['Authorization'] = "somerandomstringhere"
        response = board_service.get_boards(headers)

        # Validating the response
        assert isinstance(response, str)
        self.assertEqual(response, "Invalid Authorization")

    # private method to generate JWT token
    def __get_jwt_token(self, user_id):
    	try:
    		payload = {'sub': str(user_id), 'exp': datetime.datetime.utcnow() + datetime.timedelta(hours = 24)}
    		return jwt.encode(payload, 'Wrsapu0F7HeRmTt4PJZR', algorithm='HS256')
    	except Exception as e:
    		return e

    # private method to return base64 image
    def __base64_image_string(self):
        # Some random image from online
        url = "https://www.google.com/url?sa=i&source=images&cd=&ved=2ahUKEwiLy6bNgeDlAhWEpFkKHYnHAyEQjRx6BAgBEAQ&url=https%3A%2F%2Fwww.pexels.com%2Fsearch%2Fflower%2F&psig=AOvVaw1ZQM1uvfHBtexrOdzVvpeq&ust=1573487968589430"
        # Returning the base64 string back
        return "data:image/jpeg;base64," + base64.b64encode(requests.get(url).content).decode("utf-8")

# Code to run the test cases in the above class
if __name__ == '__main__':
    unittest.main()
