# importing reuired libraries
from flask import Blueprint, request, jsonify
import jsonpickle

# importing models
from models.user import User

# importing services
from services.user_service import UserService

user_api = Blueprint('user_api', __name__)

# API to register a user to application
@user_api.route("/registerUser", methods = ['POST'])
def register():
    try:
        # Fetching data from post body
        data = request.json

        # Invoking register method present in services
        user_service = UserService()
        response = user_service.register(data)

        # Returning appropriate message
        if isinstance(response, User):
            return jsonpickle.encode(response, unpicklable = False), 200
        else:
            return jsonify({'errorMessage' : response}), 500
    except Exception as e:
        return jsonify({'errorMessage' : e}), 500

@user_api.route("/login", methods = ['POST'])
def login():
    try:
        # Fetching data from post body
        data = request.json

        # Invoking login method present in services
        user_service = UserService()
        response = user_service.login(data)

        # Returning appropriate message
        if isinstance(response, User):
            return jsonpickle.encode(response, unpicklable = False), 200
        else:
            return jsonify({'errorMessage' : response}), 500
    except Exception as e:
        return jsonify({'errorMessage' : e}), 500

@user_api.route("/editUser", methods = ['POST'])
def edit_user_details():
    try: 
        # Fetching data from post body
        data = request.json
        headers = request.headers

        # Invoking edit_user_details method present in services
        user_service = UserService()
        response = user_service.edit_user_details(data, headers)

        # Returning appropriate message
        if isinstance(response, User):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
            print(response)
    except Exception as e:
        return jsonify({'errorMessage': e}), 500