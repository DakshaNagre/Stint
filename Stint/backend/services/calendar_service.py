# importing required libraries
from bson.objectid import ObjectId
from datetime import datetime

# Importing models
from models.calendar import Calendar
from models.task import Task

# Importing handlers
from handlers.mongo_handler import MongoDbHandler
from handlers.jwt_handler import JwtHandler

# Class to hold the business logic for calendar
class CalendarService:
     # Initializing the class
    def __init__(self, tasks_collection = 'tasks', swim_lane_collection = "swimlanes", mongo_db = MongoDbHandler()):
        self.tasks_collection = tasks_collection
        self.swim_lane_collection = swim_lane_collection
        self.mongo_db = mongo_db

    #Method for get calendar data
    def get_calendar(self, client_type, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Fetching thee tasks collection
                tasks_collection = self.__get_tasks_collection()
                cursor = tasks_collection.find({"userId" : payload['sub']})

                # Formatting the data as per the client type
                if(client_type == "android"):
                    return self.get_calendar_data_for_android(cursor)
                if(client_type == "web"):
                    return self.get_calendar_data_for_web(cursor)
            else:
                return "Invalid Authorization"
        except Exception as exception:
            raise exception

    # Formatting the object as per android's format
    def get_calendar_data_for_android(self, cursor):
        calendar_events = []
        # Iterating through the cursore and building object
        for calendar_event in cursor:
            if("dueDateTime" in calendar_event and calendar_event["dueDateTime"]):
                calendar = Calendar()
                calendar.taskId = str(calendar_event["_id"])
                calendar.taskBoardId = self.get_board_id_from_swim_lane_collection(calendar_event["laneId"])
                calendar.taskTitle = calendar_event["title"]
                calendar.taskDescription = calendar_event["description"]
                datetime_object = datetime.strptime(calendar_event["dueDateTime"], '%m/%d/%Y %H:%M:%S')
                calendar.taskDueDate = datetime_object.strftime("%m/%d/%Y")
                calendar.taskDueTime = datetime_object.strftime("%H:%M:%S")
                if(datetime_object < datetime.now()):
                    calendar.taskElapsed = True
                calendar_events.append(calendar)

        # Closing the DB connection
        self.mongo_db.disconnect()
        return calendar_events

    # Formatting the object as per web application(react) format
    def get_calendar_data_for_web(self, cursor):
        # Web will be implemented as per the client API being used.
        # For now it will return the android format
        return self.get_calendar_data_for_android(cursor)

    #Method for get events based on a date
    def get_events_by(self, date, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Fetching the tasks collection
                tasks_collection = self.__get_tasks_collection()
                date = str(int(date[0:2])) + "/" + str(int(date[2:4])) + "/" + str(int(date[4:8]))
                query = {"$and": [{"userId" : payload['sub']}, {"dueDateTime": {"$regex": date} }]}
                # Fetching the task based on user and date
                cursor = tasks_collection.find(query)
                # preparing events list
                events = []
                for task in cursor:
                    event = Task()
                    event.title = task["title"]
                    event.description = task["description"]
                    event.dueDateTime = task["dueDateTime"]
                    events.append(event)
                return events
            else:
                return "Invalid Authorization"
        except Exception as exception:
            raise exception

    # Method to get baord_id using swim_lane_id
    def get_board_id_from_swim_lane_collection(self, swim_lane_id):
        # Fetching the lanes using swim_lane_id
        swim_lanes_collection = self.__get_swim_lane_collection()
        cursor = swim_lanes_collection.find({"_id" : ObjectId(swim_lane_id)})
        if(cursor.count() > 0):
            return cursor[0]["boardId"]
        return None

    # Method to get the database collection
    def __get_tasks_collection(self):
        return self.mongo_db.connect()[self.tasks_collection]

    # Method to get the database collection
    def __get_swim_lane_collection(self):
        return self.mongo_db.connect()[self.swim_lane_collection]
