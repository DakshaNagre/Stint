# importing reuired libraries
from flask import Blueprint, request, jsonify
import jsonpickle

# importing models
from models.swim_lane import SwimLane

# importing services
from services.swim_lane_service import SwimLaneService

swim_lane_api = Blueprint('swim_lane_api', __name__)

# API to create a swimlane
@swim_lane_api.route("/createSwimLane", methods=['POST'])
def createSwimLane():
    try:
        # Fetching data from post body
        data = request.json
        headers = request.headers

        # Invoking create_swim_lane method present in services
        swim_lane_service = SwimLaneService()
        response = swim_lane_service.create_swim_lane(data, headers)

        # Returning appropriate message
        if isinstance(response, SwimLane):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500


# API to retrieve SwimLanes
@swim_lane_api.route("/getSwimLanes/boardId/<board_id>", methods=['GET'])   
def get_swimLane(board_id):
    try:
        # Fetching data from post body
        headers = request.headers

        # Invoking register method present in services
        swim_lane_service = SwimLaneService()
        response = swim_lane_service.get_swim_lane(board_id, headers)

        # Returning appropriate message
        if isinstance(response, list):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500

# API to edit a swimlane
@swim_lane_api.route("/editSwimLane", methods=['POST'])
def editSwimLane():
    try:
        # Fetching data from post body
        data = request.json
        headers = request.headers

        # Invoking edit_swim_lane method present in services
        swim_lane_service = SwimLaneService()
        response = swim_lane_service.edit_swim_lane(data, headers)

        # Returning appropriate message
        if isinstance(response, SwimLane):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500
