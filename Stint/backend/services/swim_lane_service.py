# importing required libraries
from bson.objectid import ObjectId

# Importing models
from models.swim_lane import SwimLane

# Importing Services
from services.task_service import TaskService

# Importing handlers
from handlers.mongo_handler import MongoDbHandler
from handlers.jwt_handler import JwtHandler

# Class to hold the business logic for swim lanes data
class SwimLaneService:
     # Initializing the class
    def __init__(self, collection = 'swimlanes', mongo_db = MongoDbHandler()):
        self.collection = collection
        self.mongo_db = mongo_db

    # Method to store swimlane in database
    def create_swim_lane(self, swimlane, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Creating a swim_lane object
                swim_lane_obj = SwimLane()
                swim_lane_obj.title = swimlane['title']
                swim_lane_obj.boardId = swimlane['boardId']
                swim_lane_obj.position = swimlane['position']

                # Storing the swim_lane in swimlanes collection
                swim_lanes_collection = self.__get_swim_lane_collection()
                saved_swim_lane = swim_lanes_collection.insert_one(swim_lane_obj.__dict__)

                # Storing the swimlane_id in the object and returning
                swim_lane_obj.id = str(saved_swim_lane.inserted_id)

                # Closing the DB connection
                self.mongo_db.disconnect()

                return swim_lane_obj
            else:
                return "Invalid Authorization"

        except Exception as exception:
            raise exception

    # Method to edit a swimlane in database
    def edit_swim_lane(self, swimlane, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Creating a swim_lane object
                swim_lane_obj = SwimLane()
                swim_lane_obj.title = swimlane['title']
                swim_lane_obj.boardId = swimlane['boardId']
                swim_lane_obj.position = swimlane['position']
                swim_lane_obj.id = swimlane['id']

                # Updating the swim_lane in swimlanes collection
                swim_lanes_collection = self.__get_swim_lane_collection()
                query = { '_id':  ObjectId(swim_lane_obj.id)}
                updated_values = { "$set": swim_lane_obj.__dict__ }
                swim_lanes_collection.update_one(query, updated_values)

                # Closing the DB connection
                self.mongo_db.disconnect()

                return swim_lane_obj
            else:
                return "Invalid Authorization"

        except Exception as exception:
            raise exception

    #Method for retreiving SwimLanes
    def get_swim_lane(self, board_id, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                swim_lane_obj = SwimLane()
                swim_lane_obj.boardId = board_id

                # Fetching the swimlane collection
                swim_lanes_collection = self.__get_swim_lane_collection()
                cursor = swim_lanes_collection\
                .find({"boardId" : swim_lane_obj.boardId}).sort("position")

                swim_lanes = []
                for swim_lane in cursor:
                    swim_lane_obj = SwimLane()
                    swim_lane_obj.boardId = str(swim_lane['boardId'])
                    swim_lane_obj.laneId = str(swim_lane['_id'])
                    swim_lane_obj.title = swim_lane['title']
                    swim_lane_obj.position = swim_lane['position']
                    task_service = TaskService()
                    swim_lane_obj.cards = task_service.get_tasks(str(swim_lane['_id']), headers)
                    swim_lanes.append(swim_lane_obj)

                # Closing the DB connection
                self.mongo_db.disconnect()
                # Returning list of swimlanes
                return swim_lanes
            else:
                return "Invalid Authorization"
        except Exception as exception:
            raise exception

    # Method to get the database collection
    def __get_swim_lane_collection(self):
        return self.mongo_db.connect()[self.collection]
