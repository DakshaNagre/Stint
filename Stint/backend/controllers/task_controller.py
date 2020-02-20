# importing reuired libraries
from flask import Blueprint, request, jsonify
import jsonpickle

# importing models
from models.task import Task

# importing services
from services.task_service import TaskService

task_api = Blueprint('task_api', __name__)

# API to create a task
@task_api.route("/createTask", methods=['POST'])
def createTask():
    try:
        # Fetching data from post body
        data = request.json
        headers = request.headers

        # Invoking create_task method present in TaskService
        task_service = TaskService()
        response = task_service.create_task(data, headers)

        # Returning appropriate message
        if isinstance(response, Task):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500

# API to edit a task
@task_api.route("/editTask", methods=['POST'])
def editTaskLane():
    try:
        # Fetching data from post body
        data = request.json
        headers = request.headers

        # Invoking edit_task method present in TaskService
        task_service = TaskService()
        response = task_service.edit_task(data, headers)

        # Returning appropriate message
        if isinstance(response, Task):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500
