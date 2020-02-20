# importing required libraries
from datetime import datetime, timedelta
from bson.objectid import ObjectId

# package for sending android notification
from pyfcm import FCMNotification

# importing mongodb handler
from handlers.mongo_handler import MongoDbHandler
from handlers.email_handler import EmailHandler

# Class responsible to send notifications
class NotificationsJob():

    # Initializing the class
    def __init__(self, tasks_collection = "tasks", users_collection = "users", mongo_db = MongoDbHandler()):
        self.tasks_collection = tasks_collection
        self.users_collection = users_collection
        self.mongo_db = mongo_db;

    # Method to hold the logic to send notifcations
    def send_task_notifications(self):
        print("---------------------Notification Job has started at %s---------------------" %(datetime.now(),))
        task_collection = self.__get_task_collection()
        now = datetime.now() - timedelta(hours = 5)
        start = now - timedelta(minutes = 5)
        end = now + timedelta(minutes = 5)
        tasks = task_collection.find({"notificationDateTime" : {"$lt": end, "$gte": start}})
        for task in tasks:
            if(task['dueDateTime'] != None and len(task['dueDateTime']) > 0):
                try:
                    self.send_notification(task)
                except Exception as ex:
                    print(ex)
        self.__disconnect_db_connections()

    def send_notification(self, task):
        user_collection = self.__get_user_collection()
        user = user_collection.find({'_id':  ObjectId(task["userId"])})
        if("deviceToken" in user[0] and len(user[0]["deviceToken"]) > 0 \
         and "pushNotification" in user[0] and user[0]["pushNotification"]):
            self.send_android_push_notification(user, task)
        if("emailNotification" in user[0] and user[0]["emailNotification"]):
            self.send_email_notification(user, task)

    def send_android_push_notification(self, user, task):
        push_service = FCMNotification(api_key="AIzaSyADTUkWvFggwfECE1nG-dovrXmkm2-qjJ8")
        registration_id = user[0]["deviceToken"]
        message_title = task['title']
        datetime_object = datetime.strptime(task['dueDateTime'], '%m/%d/%Y %H:%M:%S')
        message_body = "Hi " + user[0]['name'] + ", You have to " + task['title'] + " due on " + datetime_object.strftime('%d, %b %Y at %H:%M')
        print("Push Notification is being sent to user " + task["userId"] + " for task" + str(task["_id"]))
        result = push_service.notify_single_device(registration_id=registration_id, message_title=message_title, message_body=message_body)

    def send_email_notification(self, user, task):
        try:
            email_handler = EmailHandler(to = user[0]['email'], \
            subject = "Stint notification: " + task['title'])
            datetime_object = datetime.strptime(task['dueDateTime'], '%m/%d/%Y %H:%M:%S')
            context = {
                'username': user[0]['name'],
                'title': task['title'],
                'dueDateTime': datetime_object.strftime('%d, %b %Y at %H:%M'),
                'description': task['description']
            }
            email_handler.html('notification.html', context)
            email_handler.send()
            print("Email is being sent to user " + task["userId"] + " for task" + str(task["_id"]))
        except Exception as exception:
            print("Exception has occurred while sending notification \
            email to user: " + task["userId"] + " for task" + str(task["_id"]))

    # Disconnect from the database
    def __disconnect_db_connections(self):
        self.mongo_db.disconnect()
        print("---------------------Notification Job has ended at %s---------------------" %(datetime.now(),))

    # Method to get the tasks database collection
    def __get_task_collection(self):
        return self.mongo_db.connect()[self.tasks_collection]

    # Method to get the users database collection
    def __get_user_collection(self):
        return self.mongo_db.connect()[self.users_collection]
