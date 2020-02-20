# importing required libraries
from bson.objectid import ObjectId

# Importing models
from models.board import Board

# Importing handlers
from handlers.mongo_handler import MongoDbHandler
from handlers.jwt_handler import JwtHandler
from handlers.amazon_s3_handler import AmazonS3Handler

# Class to hold the business logic for user data
class BoardService:
     # Initializing the class
    def __init__(self, collection = 'boards', mongo_db = MongoDbHandler()):
        self.collection = collection
        self.mongo_db = mongo_db

    #Method for Boards
    def create_board(self, board, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Creating a board object
                board_obj = Board()
                board_obj.user_id = payload['sub']
                board_obj.name = board['name']
                board_obj.color = board['color']

                # Storing the board in boards collection
                boards_collection = self.__get_board_collection()
                saved_board = boards_collection.insert_one(board_obj.__dict__)

                # Stroing the board_id in the object and returning
                board_obj.board_id = str(saved_board.inserted_id)

                # Closing the DB connection
                self.mongo_db.disconnect()

                return board_obj
            else:
                return "Invalid Authorization"

        except Exception as exception:
            raise exception

    #Method for Boards
    def edit_board(self, board, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                # Creating a board object
                board_obj = Board()
                board_obj.user_id = payload['sub']
                board_obj.name = board['name']
                board_obj.board_id = board['board_id']
                board_obj.description = board['description']
                board_obj.color = board['color']
                board_obj.image = board['image']
                # Checking if the image is being passed or not
                if(board['image'] and len(board['image'])>0):
                    # Storing the image in Amazon S3 bucket
                    amazon_s3_handler = AmazonS3Handler()
                    board_obj.image = amazon_s3_handler.store_in_S3(board['image'], board['board_id'])

                # Updating the board
                boards_collection = self.__get_board_collection()
                query = { '_id':  ObjectId(board_obj.board_id)}
                updated_values = { "$set": board_obj.__dict__ }
                boards_collection.update_one(query, updated_values)

                # Closing the DB connection
                self.mongo_db.disconnect()

                return board_obj
            else:
                return "Invalid Authorization"

        except Exception as exception:
            raise exception

    #Method for sending Boards
    def get_boards(self, headers):
        try:
            # Checking if the token is valid or not
            payload = JwtHandler.decode_auth_token(headers['Authorization'])
            if(payload):
                board_obj = Board()
                board_obj.user_id = payload['sub']

                # Fetching thee board collection
                boards_collection = self.__get_board_collection()
                cursor = boards_collection.find({"user_id" : board_obj.user_id})
                # Creating a list named boards
                boards = []
                for board in cursor:
                    board_obj = Board()
                    board_obj.board_id = str(board['_id'])
                    board_obj.user_id = board['user_id']
                    board_obj.description = board['description']
                    board_obj.color = board['color']
                    board_obj.name = board['name']
                    board_obj.image = board['image']
                    boards.append(board_obj)
                # Returning list
                return boards
            else:
                return "Invalid Authorization"
        except Exception as exception:
            raise exception

    # Method to get the database collection
    def __get_board_collection(self):
        return self.mongo_db.connect()[self.collection]
