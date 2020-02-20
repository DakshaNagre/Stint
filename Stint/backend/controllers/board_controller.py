# importing reuired libraries
from flask import Blueprint, request, jsonify
import jsonpickle

# importing models
from models.board import Board

# importing services
from services.board_service import BoardService

board_api = Blueprint('board_api', __name__)

# API to create a board
@board_api.route("/createBoard", methods=['POST'])
def createBoard():
    try:
        # Fetching data from post body
        data = request.json
        headers = request.headers

        # Invoking create_board method present in services
        board_service = BoardService()
        response = board_service.create_board(data, headers)

        # Returning appropriate message
        if isinstance(response, Board):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500

# API to edit a board
@board_api.route("/editBoard", methods=['POST'])
def editBoard():
    try:
        # Fetching data from post body
        data = request.json
        headers = request.headers

        # Invoking edit_board method present in services
        board_service = BoardService()
        response = board_service.edit_board(data, headers)

        # Returning appropriate message
        if isinstance(response, Board):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500

# API to retrieve Boards
@board_api.route("/getBoards", methods=['GET'])
def get_boards():
    try:
        # Fetching data from post body
        headers = request.headers
        # Invoking register method present in services
        board_service = BoardService()
        response = board_service.get_boards(headers)

        # Returning appropriate message
        if isinstance(response, list):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500
        
