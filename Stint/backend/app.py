# importing reuired libraries
from flask import Flask
from flask_cors import CORS,cross_origin

# importing controllers
from controllers.user_controller import user_api
from controllers.board_controller import board_api
from controllers.swim_lane_controller import swim_lane_api
from controllers.task_controller import task_api
from controllers.calendar_controller import calendar_api


# initializing flask application
app = Flask(__name__, static_url_path='/static')
app.config.from_object(__name__)

# enabling cross origin access to any source
# Once the applciation is deployed, the '*' will be replaced with
# source URL
app.config['CORS_HEADERS'] = 'Content-Type'
cors = CORS(app, resources={r"/*": {"origins": "*"}})

# user_api hold all the API related to login and registration
app.register_blueprint(user_api)
# board_api to hold the API related to boards
app.register_blueprint(board_api)
# swim_lane_api to hold the API related to swim lanes
app.register_blueprint(swim_lane_api)
# task_api to hold the API related to tasks
app.register_blueprint(task_api)
# calendar_api to hold the API related to calendar
app.register_blueprint(calendar_api)

# Starting the application server
if __name__ == '__main__':
	app.run(host = '0.0.0.0', port = 5000)
