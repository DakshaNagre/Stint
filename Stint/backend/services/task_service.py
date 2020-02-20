# importing required libraries
from bson.objectid import ObjectId
from datetime import datetime, timedelta

# Importing models
from models.task import Task

# Importing handlers
from handlers.mongo_handler import MongoDbHandler
from handlers.jwt_handler import JwtHandler

# Class to hold the business logic for tasks data
class TaskService:
     # Initializing the class
    def __init__(self, collection = 'tasks', mongo_db = MongoDbHandler()):
        self.collection = collection
        self.mongo_db = mongo_db

    # Method to store tasks in database
    def create_task(self, task, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Creating a task object
                task_obj = Task()
                task_obj.title = task['title']
                task_obj.laneId = task['laneId']
                task_obj.description = task['description']
                task_obj.userId = payload['sub']
                task_obj.position = task['position']
                task_obj.notificationTimeMinutes = task['notificationTimeMinutes']

                # Storing the task in tasks collection
                task_collection = self.__get_task_collection()
                saved_task = task_collection.insert_one(task_obj.__dict__)

                # Storing the task_id in the object and returning
                task_obj.id = str(saved_task.inserted_id)

                # Closing the DB connection
                self.mongo_db.disconnect()

                return task_obj
            else:
                return "Invalid Authorization"

        except Exception as exception:
            raise exception

    # Method to edit a task in database
    def edit_task(self, task, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Creating a task object
                task_obj = Task()
                task_obj.title = task['title']
                task_obj.laneId = task['laneId']
                task_obj.id = task['id']
                task_obj.dueDateTime = task['dueDateTime']
                task_obj.notificationTimeMinutes = task['notificationTimeMinutes']
                if(task_obj.dueDateTime and task['notificationTimeMinutes'] != -1):
                    datetime_object = datetime.strptime(task_obj.dueDateTime, '%m/%d/%Y %H:%M:%S')
                    task_obj.notificationDateTime = datetime_object - timedelta(minutes = int(task['notificationTimeMinutes']))
                task_obj.description = task['description']
                task_obj.userId = payload['sub']
                task_obj.position = task['position']


                # Updating the task in tasks collection
                task_collection = self.__get_task_collection()
                query = { '_id':  ObjectId(task_obj.id)}
                updated_values = { "$set": task_obj.__dict__ }
                task_collection.update_one(query, updated_values)

                # Closing the DB connection
                self.mongo_db.disconnect()

                return task_obj
            else:
                return "Invalid Authorization"

        except Exception as exception:
            raise exception

    #Method for retrieving tasks
    def get_tasks(self, swim_lane_id, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                task_obj = Task()
                task_obj.laneId = swim_lane_id

                # Fetching the tasks collection
                task_collection = self.__get_task_collection()
                cursor = task_collection\
                .find({"laneId" : task_obj.laneId }).sort("position")

                tasks = []
                for task in cursor:
                    task_obj = Task()
                    task_obj.laneId = task['laneId']
                    task_obj.id = str(task['_id'])
                    task_obj.title = task['title']
                    task_obj.dueDateTime = task['dueDateTime']
                    task_obj.notificationDateTime = task['notificationDateTime']
                    task_obj.notificationTimeMinutes = task['notificationTimeMinutes']
                    task_obj.description = task['description']
                    task_obj.position = task['position']

                    tasks.append(task_obj)

                # Closing the DB connection
                self.mongo_db.disconnect()
                # Returning list of tasks
                return tasks
            else:
                return "Invalid Authorization"
        except Exception as exception:
            raise exception

    # Method to get the database collection
    def __get_task_collection(self):
        return self.mongo_db.connect()[self.collection]
