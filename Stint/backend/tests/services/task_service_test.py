# importing required libraries
import unittest
import jwt
import datetime
import base64
import requests

# importing services
from services.task_service import TaskService
from services.swim_lane_service import SwimLaneService
from services.board_service import BoardService
from services.user_service import UserService
from handlers.mongo_handler import MongoDbHandler

# importing models
from models.task import Task

# Class to hold test cases for TaskService class
class TaskServiceTest(unittest.TestCase):

    # Class variables
    user_id = None
    board_id = None
    headers = {}
    board = None
    swim_lane = None
    swim_lane_id = None
    task = None
    task_id = None

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

        # Initializing BoardService and creating board
        board_service = BoardService("test")
        response = board_service.create_board(self.board, self.headers)
        self.board_id = response.board_id

        # Initializing SwimLaneService and creating a swim lane
        swim_lane_service = SwimLaneService("test")
        self.swim_lane = {"title": "todo", "boardId": self.board_id, "position": "0"}
        response = swim_lane_service.create_swim_lane(self.swim_lane, self.headers)

    # Method to release environment used by test cases
    def tearDown(self):
        # deleting the user
        user_service = UserService("test")
        user_service.delete_user(self.user_id)

    # Test case for create_task method
    def test_create_task(self):
        # Initializing TaskService and invoking create_task()
        task_service = TaskService("test")
        self.task = {"title": "Order iphone on Amazon", "laneId": self.swim_lane_id,\
         "description": "Orderign phone to nephew", "userId": self.user_id, "position": "0",\
         "notificationTimeMinutes": "10"}
        response = task_service.create_task(self.task, self.headers)

        # Validating the response
        assert isinstance(response, Task)
        assert response.id is not None

    # Test case for create_task method with invalid token
    def test_create_task_with_invalid_authorization(self):
        # Initializing TaskService and invoking create_task()
        task_service = TaskService("test")
        headers = {}
        headers['Authorization'] = "somerandomstringhere"
        response = task_service.create_task(self.task, headers)

        # Validating the response
        assert isinstance(response, str)
        self.assertEqual(response, "Invalid Authorization")

    # Test case for edit_task method
    def test_edit_task(self):
        # Initializing TaskService and invoking create_task()
        task_service = TaskService("test")
        self.task = {"title": "Order iphone on Amazon", "laneId": self.swim_lane_id,\
         "description": "Ordering phone to nephew", "userId": self.user_id, "position": "0",\
         "notificationTimeMinutes": "10"}
        response = task_service.create_task(self.task, self.headers)
        self.task_id = response.id
        # Initializing TaskService and invoking edit_task()
        task_service = TaskService("test")
        self.task = {"title": "Order from Flipkart", "laneId": self.swim_lane_id, \
        "id": self.task_id, "dueDateTime": None, \
        "notificationDateTime": None, "description": "Ordering phone to niece",\
        "userId": self.user_id, "position": "1", "notificationTimeMinutes": "10" }

        response = task_service.edit_task(self.task, self.headers)

        # Validating the response
        assert isinstance(response, Task)
        assert response.id is not None

    # Test case for edit_task method with invalid token
    def test_edit_task_with_invalid_authorization(self):
        # Initializing TaskServicee and invoking edit_task()
        task_service = TaskService("test")
        headers = {}
        headers['Authorization'] = "somerandomstringhere"
        response = task_service.edit_task(self.swim_lane, headers)

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

# Code to run the test cases in the above class
if __name__ == '__main__':
    unittest.main()
