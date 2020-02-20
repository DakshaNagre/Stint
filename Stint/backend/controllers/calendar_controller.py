# importing reuired libraries
from flask import Blueprint, request, jsonify
import jsonpickle

# importing models
from models.calendar import Calendar

# importing services
from services.calendar_service import CalendarService

calendar_api = Blueprint('calendar_api', __name__)

# API to get calendar data for a user
@calendar_api.route("/getCalendar/type/<client_type>", methods=['GET'])
def getCalendar(client_type):
    try:
        # Fetching data from post body
        headers = request.headers

        # Invoking get_calendar method present in services
        calendar_service = CalendarService()
        response = calendar_service.get_calendar(client_type, headers)

        # Returning appropriate message
        if isinstance(response, list):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500

@calendar_api.route("/getEventsBy/date/<date>", methods=['GET'])
def getEventsBy(date):
    try:
        # Fetching data from post body
        headers = request.headers

        # Invoking get_calendar method present in services
        calendar_service = CalendarService()
        response = calendar_service.get_events_by(date, headers)

        # Returning appropriate message
        if isinstance(response, list):
            return jsonpickle.encode(response, unpicklable=False), 200
        else:
            return jsonify({'errorMessage': response}), 500
    except Exception as e:
        return jsonify({'errorMessage': e}), 500
