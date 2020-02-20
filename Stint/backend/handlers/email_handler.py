# importing required libraries
import boto.ses
from jinja2 import Environment, PackageLoader

# Class responsible to send emails
class EmailHandler(object):

    # Initializing the classs
    def __init__(self, to, subject):
        self.to = to
        self.subject = subject
        self._html = None
        self._text = None
        self.env = Environment(loader=PackageLoader('controllers', 'templates'))

    # Method to send email using a html template
    def _render(self, filename, context):
        template = self.env.get_template(filename)
        return template.render(context)

    def html(self, filename, context):
        self._html = self._render(filename, context)

    def text(self, filename, context):
        self._text = self._render(filename, context)

    # Method responsible to send email using Amazon SES
    def send(self, from_addr=None):
        body = self._html

        if not from_addr:
            from_addr = 'stint4betterlife@gmail.com'
        if not self._html and not self._text:
            raise Exception('No text or html body is found.')
        if not self._html:
            body = self._text

        connection = boto.ses.connect_to_region('us-east-1', \
        aws_access_key_id = 'AKIAJAR4RJMB6DDV6HEA', \
        aws_secret_access_key = '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')

        return connection.send_email(from_addr, self.subject, None, self.to, \
        text_body=self._text, html_body=self._html)
