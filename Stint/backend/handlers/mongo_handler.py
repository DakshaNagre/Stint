import pymongo
import ssl

# Handler to communicate with mongo database
class MongoDbHandler:

    def __init__(self, endpoint = "mongodb+srv://achanta:pusherapp@userservice-recot.mongodb.net/test?retryWrites=true&w=majority&ssl_cert_reqs=CERT_NONE"):
        self.endpoint = endpoint
        self.client = None

    # Method to provide connection with mongo db
    def connect(self):
        try:
            self.client = pymongo.MongoClient(self.endpoint)
            # returning the database instance 'Pusher'
            return self.client["Stint"]
        except Exception as e:
            # Returning False if there is any exception
            return False

    # Methof to diconnect the database
    def disconnect(self):
        self.client.close()
