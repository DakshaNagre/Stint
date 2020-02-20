from apscheduler.schedulers.blocking import BlockingScheduler

# importing background jobs
from jobs.notifications_job import NotificationsJob

# Intializing notifications_job
notifications_job = NotificationsJob()

# initializing a scheduler
sched = BlockingScheduler()

# Running the job for every 10 minutes
@sched.scheduled_job('interval', minutes = 10)
def send_notifications_every_ten_minutes():
    notifications_job.send_task_notifications()

# Starting the job
sched.start()
