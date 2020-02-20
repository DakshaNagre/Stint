# base64 for image handling
import base64

# importing S3 related package 'boto'
import boto
from boto.s3.key import Key
from boto.s3.connection import OrdinaryCallingFormat


# Class to handle amazon S3 bucket
class AmazonS3Handler:
    # Initializing the S3 bucket connection
    def __init__(self):
        AWS_ACCESS_KEY = 'AKIAIFCVURCK6MYMZOQQ'
        AWS_ACCESS_SECRET_KEY = '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'
        self.connect = boto.s3.connect_to_region('us-east-1', \
        aws_access_key_id = AWS_ACCESS_KEY, \
        aws_secret_access_key = AWS_ACCESS_SECRET_KEY, \
        is_secure = False, calling_format = OrdinaryCallingFormat())
        self.bucket = self.connect.get_bucket('task-management-app',validate=False)

    # Storing the image on S3
    def store_in_S3(self, img_str, img_name):
        try:
            object = Key(self.bucket)
            object.key = img_name
            img_str = img_str.split(",", 1)[1]
            decode_img = base64.b64decode(img_str)
            object.set_metadata('Content-Type', 'image/jpeg')
            object.set_contents_from_string(decode_img)
            object.make_public()
            self.connect.close()
            return True
        except Exception as e:
            raise e
